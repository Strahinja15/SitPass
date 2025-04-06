package com.example.sispas.controller;

import com.example.sispas.dto.ChangePasswordDTO;
import com.example.sispas.dto.RejectionReasonDTO;
import com.example.sispas.dto.UserDTO;
import com.example.sispas.model.AccountRequest;
import com.example.sispas.model.RequestStatus;
import com.example.sispas.model.Role;
import com.example.sispas.model.User;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.security.JwtUtil;
import com.example.sispas.service.AccountRequestService;
import com.example.sispas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AccountRequestService accountRequestService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, AccountRequestService accountRequestService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
        this.userService = userService;
        this.accountRequestService = accountRequestService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsersWithRoleUserAndManager() {
        List<UserDTO> userDTOs = userService.getAllUsersWithRoleUserAndManager();
        return ResponseEntity.ok(userDTOs);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setAddress(user.getAddress());
        userDTO.setCity(user.getCity());
        userDTO.setRole(user.getRole());
        userDTO.setZipCode(user.getZipCode());
        userDTO.setImageUrl(user.getImage() != null ? "/images/" + user.getImage().getPath() : null);

        Map<String, Object> response = new HashMap<>();
        response.put("profile", userDTO);

        if (user.getRole() == Role.administrator) {
            List<AccountRequest> accountRequests = accountRequestService.getAllRequests();
            response.put("accountRequests", accountRequests);
        }

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @PutMapping("/request/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        accountRequestService.updateAccountRequestStatus(id, RequestStatus.accepted, null);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @PutMapping("/request/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id, @RequestBody RejectionReasonDTO rejectionReasonDTO) {
        accountRequestService.updateAccountRequestStatus(id, RequestStatus.rejected, rejectionReasonDTO.getReason());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordDTO changePasswordDTO) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtUtil.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }

        User user = userService.findByEmail(email);
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRepeatNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully."));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, Authentication authentication) {
        String email = authentication.getName();

        if (!email.equals(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You can only update your own profile.");
        }

        userService.updateUser(userDTO);
        return ResponseEntity.ok(Collections.singletonMap("message", "User updated successfully."));
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @GetMapping("/requests")
    public ResponseEntity<Page<AccountRequest>> getRequests(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountRequest> requests = userService.getAccountRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        try {
            String imagePath = userService.saveImage(image, user);
            return ResponseEntity.ok(Collections.singletonMap("imageUrl", imagePath));
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image.");
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(toDTO(user));
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(toDTO(user));
    }


    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        return dto;
    }
}

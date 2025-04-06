package com.example.sispas.service.impl;

import com.example.sispas.dto.UserDTO;
import com.example.sispas.model.*;
import com.example.sispas.repository.AccountRequestRepository;
import com.example.sispas.repository.ImageRepository;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AccountRequestRepository accountRequestRepository;
    private final ImageRepository imageRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, AccountRequestRepository accountRequestRepository, ImageRepository imageRepository){
        this.userRepository = userRepository;
        this.accountRequestRepository = accountRequestRepository;
        this.imageRepository = imageRepository;
    }
    @Override
    public User findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseThrow(() -> new IllegalArgumentException("No user found with email " + email));
    }
    public List<UserDTO> getAllUsersWithRoleUserAndManager() {
        List<User> users = userRepository.findByRole(Role.administrator);
        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }
    @Override
    public void updateUser(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No user found with email " + userDTO.getEmail()));

        // Check if the new email already exists for another user
        if (!user.getEmail().equals(userDTO.getEmail())) {
            boolean emailExists = userRepository.findByEmail(userDTO.getEmail()).isPresent();
            if (emailExists) {
                throw new IllegalArgumentException("Email already exists.");
            }
            user.setEmail(userDTO.getEmail());
        }
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
        user.setCity(userDTO.getCity());
        user.setZipCode(userDTO.getZipCode());

        userRepository.save(user);
    }
    public Page<AccountRequest> getAccountRequests(Pageable pageable) {
        List<AccountRequest> allRequests = accountRequestRepository.findAll();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<AccountRequest> list;

        if (allRequests.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, allRequests.size());
            list = allRequests.subList(startItem, toIndex);
        }

        Page<AccountRequest> requestPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), allRequests.size());

        return requestPage;
    }


    @Override
    public String saveImage(MultipartFile image, User user) throws IOException {
        try {
            String originalFileName = image.getOriginalFilename();
            Optional<Image> existingImage = imageRepository.findByPath(originalFileName);

            if (existingImage.isPresent()) {
                // Ako slika već postoji, poveži je sa korisnikom
                user.setImage(existingImage.get());
                userRepository.save(user);
                return existingImage.get().getPath();
            } else {
                // Ako slika ne postoji, sačuvaj je
                Path path = Paths.get("src/main/resources/static/images/" + originalFileName);
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                Image img = new Image();
                img.setPath(originalFileName);
                imageRepository.save(img);

                user.setImage(img);
                userRepository.save(user);

                return originalFileName;
            }
        } catch (IOException e) {
            // Log the error
            e.printStackTrace();
            throw new IOException("Failed to save image", e);
        }
    }
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBirthday(user.getBirthday());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setRole(user.getRole());
        dto.setZipCode(user.getZipCode());
        return dto;
    }

}

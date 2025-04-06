package com.example.sispas.controller;

import com.example.sispas.dto.ChangePasswordDTO;
import com.example.sispas.dto.LoginDTO;
import com.example.sispas.model.User;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.security.JwtUtil;
import com.example.sispas.service.InvalidatedTokenService;
import com.example.sispas.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/authorization")
public class AuthorizationController {

    private final JwtUtil jwtUtil;
    private final InvalidatedTokenService invalidatedTokenService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthorizationController(JwtUtil jwtUtil, InvalidatedTokenService invalidatedTokenService, UserDetailsService userDetailsService,
                                   AuthenticationManager authenticationManager, UserService userService){
        this.jwtUtil = jwtUtil;
        this.invalidatedTokenService = invalidatedTokenService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.findByEmail(loginDTO.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            invalidatedTokenService.invalidateToken(token);
            return ResponseEntity.ok(Collections.singletonMap("message", "Successfully logged out."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Invalid token."));
        }
    }


}

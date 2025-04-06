package com.example.sispas.controller;

import com.example.sispas.dto.UserDTO;
import com.example.sispas.model.AccountRequest;
import com.example.sispas.service.AccountRequestService;
import com.example.sispas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/account-requests")
public class AccountRequestController {

    private final AccountRequestService accountRequestService;
    private final UserService userService;

    @Autowired
    public AccountRequestController(AccountRequestService accountRequestService, UserService userService) {
        this.accountRequestService = accountRequestService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            AccountRequest accountRequest = accountRequestService.createAccountRequest(userDTO);
            return ResponseEntity.status(CREATED).body(accountRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

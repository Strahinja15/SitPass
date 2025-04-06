package com.example.sispas.service.impl;

import com.example.sispas.dto.UserDTO;
import com.example.sispas.model.AccountRequest;
import com.example.sispas.model.RequestStatus;
import com.example.sispas.model.Role;
import com.example.sispas.model.User;
import com.example.sispas.repository.AccountRequestRepository;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.service.AccountRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountRequestServiceImpl implements AccountRequestService {

    private final AccountRequestRepository accountRequestRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountRequestServiceImpl(AccountRequestRepository accountRequestRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.accountRequestRepository = accountRequestRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountRequest createAccountRequest(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setEmail(userDTO.getEmail());
        accountRequest.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        accountRequest.setAddress(userDTO.getAddress());
        accountRequest.setCreatedAt(LocalDateTime.now());
        accountRequest.setStatus(RequestStatus.pending);

        return accountRequestRepository.save(accountRequest);
    }

    public void updateAccountRequestStatus(Long id, RequestStatus status, String rejectionReason) {
        AccountRequest accountRequest = accountRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account request ID"));

        if (status == RequestStatus.rejected) {
            accountRequest.setStatus(status);
            accountRequest.setRejectionReason(rejectionReason);
            accountRequestRepository.save(accountRequest);
        } else {
            accountRequest.setStatus(status);
            User user = new User();
            user.setEmail(accountRequest.getEmail());
            user.setPassword(accountRequest.getPassword());
            user.setAddress(accountRequest.getAddress());
            user.setRole(Role.user);
            user.setCreatedAt(LocalDateTime.now());
            accountRequestRepository.save(accountRequest);
            userRepository.save(user);
        }
    }

    @Override
    public List<AccountRequest> getAllRequests() {
        return accountRequestRepository.findAll();
    }

}

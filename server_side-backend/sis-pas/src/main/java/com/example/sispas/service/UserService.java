package com.example.sispas.service;

import com.example.sispas.dto.UserDTO;
import com.example.sispas.model.AccountRequest;
import com.example.sispas.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User findByEmail(String email);
    List<UserDTO> getAllUsersWithRoleUserAndManager();
    void updateUser(UserDTO userDTO);
    Page<AccountRequest> getAccountRequests(Pageable pageable);
    String saveImage(MultipartFile image, User user) throws IOException;
}

package com.example.sispas.service;

import com.example.sispas.dto.UserDTO;
import com.example.sispas.model.AccountRequest;
import com.example.sispas.model.RequestStatus;

import java.util.List;

public interface AccountRequestService {
    AccountRequest createAccountRequest(UserDTO userDTO);
    void updateAccountRequestStatus(Long id, RequestStatus status, String rejectionReason);

    List<AccountRequest> getAllRequests();
}

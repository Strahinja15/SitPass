package com.example.sispas.repository;

import com.example.sispas.model.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
}

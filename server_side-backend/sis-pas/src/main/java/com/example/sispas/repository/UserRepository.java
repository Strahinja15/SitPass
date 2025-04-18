package com.example.sispas.repository;

import com.example.sispas.model.Role;
import com.example.sispas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("select u from User u where u.role != :role")
    List<User> findByRole(Role role);
    List<User> findAllByImageId(Long imageId);
    boolean existsByEmail(String email);

}

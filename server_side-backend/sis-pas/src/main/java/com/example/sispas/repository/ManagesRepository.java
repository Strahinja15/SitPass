package com.example.sispas.repository;

import com.example.sispas.model.Facility;
import com.example.sispas.model.Manages;
import com.example.sispas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagesRepository extends JpaRepository<Manages, Long> {
    List<Manages> findByFacilityIdAndIsDeletedFalse(Long facilityId);
    List<Manages> findByUserIdAndIsDeletedFalse(Long userId);
    boolean existsByFacilityIdAndUserIdAndIsDeletedFalse(Long facilityId, Long userId);
}

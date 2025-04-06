package com.example.sispas.repository;

import com.example.sispas.model.Image;
import com.example.sispas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByPath(String path);
    List<Image> findByFacilityId(Long facilityId);
}

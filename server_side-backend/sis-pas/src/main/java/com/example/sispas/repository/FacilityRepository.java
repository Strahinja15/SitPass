package com.example.sispas.repository;

import com.example.sispas.model.Facility;
import com.example.sispas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
    @Query("select f from Facility f where f.isDeleted = false")
    List<Facility> findAll();
    boolean existsByName(String name);
}

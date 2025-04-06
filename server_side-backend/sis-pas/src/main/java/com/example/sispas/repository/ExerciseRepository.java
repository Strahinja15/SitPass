package com.example.sispas.repository;

import com.example.sispas.model.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByFacilityId(Long facilityId);
    Page<Exercise> findByUserId(Long userId, Pageable pageable);
    List<Exercise> findByUserId(Long userId);
    List<Exercise> findByUserIdAndFacilityId(Long userId, Long facilityId);
}

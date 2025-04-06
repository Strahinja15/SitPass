package com.example.sispas.service;

import com.example.sispas.dto.ExerciseDTO;
import com.example.sispas.model.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExerciseService {
    List<ExerciseDTO> getExercisesByFacilityId(Long facilityId);
    Exercise saveExercise(Long facilityId, ExerciseDTO exerciseRequest);
    Page<ExerciseDTO> getExercisesByUserId(Long userId, Pageable pageable);
    List<ExerciseDTO> getExercisesByUserId(Long userId);
}

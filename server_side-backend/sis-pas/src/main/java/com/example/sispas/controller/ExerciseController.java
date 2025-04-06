package com.example.sispas.controller;

import com.example.sispas.dto.ExerciseDTO;
import com.example.sispas.model.Exercise;
import com.example.sispas.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByFacilityId(@PathVariable Long facilityId) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByFacilityId(facilityId);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator', 'ROLE_user')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ExerciseDTO>> getExercisesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExerciseDTO> exercises = exerciseService.getExercisesByUserId(userId, pageable);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<ExerciseDTO>> getAllExercisesByUserId(@PathVariable Long userId) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByUserId(userId);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @PostMapping("/reserve")
    public ResponseEntity<Exercise> reserveExercise(@RequestBody ExerciseDTO exerciseDTO) {
        try {
            Exercise exercise = exerciseService.saveExercise(exerciseDTO.getFacilityId(), exerciseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(exercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

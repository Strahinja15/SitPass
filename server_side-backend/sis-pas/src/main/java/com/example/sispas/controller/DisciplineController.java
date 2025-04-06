package com.example.sispas.controller;

import com.example.sispas.dto.DisciplinesDTO;
import com.example.sispas.service.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/disciplines")
public class DisciplineController {

    private final DisciplineService disciplineService;

    @Autowired
    public DisciplineController(DisciplineService disciplineService) {
        this.disciplineService = disciplineService;
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<DisciplinesDTO>> getDisciplinesByFacilityId(@PathVariable Long facilityId) {
        System.out.println("Received request for facilityId: " + facilityId);
        List<DisciplinesDTO> disciplines = disciplineService.getDisciplinesByFacilityId(facilityId);
        return ResponseEntity.ok(disciplines);
    }

    @PostMapping("/createDiscipline")
    public ResponseEntity<DisciplinesDTO> createDiscipline(
            @RequestBody DisciplinesDTO disciplineDTO,
            @RequestParam Long facilityId) {
        disciplineDTO.setFacility(facilityId);
        DisciplinesDTO createdDiscipline = disciplineService.createDiscipline(disciplineDTO, facilityId);
        return ResponseEntity.status(CREATED).body(createdDiscipline);
    }


    @DeleteMapping("/deleteDiscipline/{id}")
    public ResponseEntity<Void> deleteDiscipline(@PathVariable Long id) {
        disciplineService.deleteDiscipline(id);
        return ResponseEntity.ok().build();
    }
}

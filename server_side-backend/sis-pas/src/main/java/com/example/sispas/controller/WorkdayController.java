package com.example.sispas.controller;

import com.example.sispas.dto.WorkdaysDTO;
import com.example.sispas.service.WorkdayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/workdays")
public class WorkdayController {

    private final WorkdayService workdayService;

    @Autowired
    public WorkdayController(WorkdayService workdayService) {
        this.workdayService = workdayService;
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<WorkdaysDTO>> getWorkdaysByFacilityId(@PathVariable Long facilityId) {
        List<WorkdaysDTO> workdays = workdayService.getWorkdaysByFacilityId(facilityId);
        return ResponseEntity.ok(workdays);
    }
    @PostMapping("/createWorkday")
    public ResponseEntity<WorkdaysDTO> createWorkday(
            @RequestBody WorkdaysDTO workdayDTO,
            @RequestParam Long facilityId) {
        workdayDTO.setFacilityId(facilityId);
        WorkdaysDTO createdWorkday = workdayService.createWorkday(workdayDTO);
        return ResponseEntity.status(CREATED).body(createdWorkday);
    }

    @DeleteMapping("/deleteWorkday/{id}")
    public ResponseEntity<Void> deleteWorkday(@PathVariable Long id) {
        workdayService.deleteWorkday(id);
        return ResponseEntity.ok().build();
    }
}

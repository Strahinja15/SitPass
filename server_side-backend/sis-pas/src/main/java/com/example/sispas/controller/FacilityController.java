package com.example.sispas.controller;

import com.example.sispas.dto.ActivationRequestDTO;
import com.example.sispas.dto.FacilityDTO;
import com.example.sispas.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/facility")
public class FacilityController {

    private final FacilityService facilityService;

    @Autowired
    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @PostMapping("/add-facility")
    public ResponseEntity<?> createFacility(@RequestBody FacilityDTO facilityDTO) {
        try {
            FacilityDTO createdFacility = facilityService.createFacility(facilityDTO);
            return ResponseEntity.status(CREATED).body(createdFacility);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/{id}")
    public ResponseEntity<FacilityDTO> getFacilityById(@PathVariable Long id) {
        FacilityDTO facilityDTO = facilityService.getFacilityById(id);
        return ResponseEntity.ok(facilityDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/all")
    public ResponseEntity<List<FacilityDTO>> getAllFacilities() {
        List<FacilityDTO> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @PutMapping("/activate/{id}")
    public ResponseEntity<FacilityDTO> activateFacility(@PathVariable Long id, @RequestBody ActivationRequestDTO activationRequest) {
        FacilityDTO updatedFacility = facilityService.activateFacility(id, activationRequest.getUserId(), activationRequest.getStartDate(), activationRequest.getEndDate());
        return ResponseEntity.ok(updatedFacility);
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<FacilityDTO> deactivateFacility(@PathVariable Long id) {
        FacilityDTO updatedFacility = facilityService.deactivateFacility(id);
        return ResponseEntity.ok(updatedFacility);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FacilityDTO> updateFacility(@PathVariable Long id, @RequestBody FacilityDTO facilityDTO) {
        System.out.println("Received update request for facility ID: " + id);
        FacilityDTO updatedFacility = facilityService.updateFacility(id, facilityDTO);
        return ResponseEntity.ok(updatedFacility);
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<FacilityDTO> deleteFacility(@PathVariable Long id) {
        FacilityDTO updatedFacility = facilityService.deleteFacility(id);
        return ResponseEntity.ok(updatedFacility);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/filter")
    public ResponseEntity<List<FacilityDTO>> filterFacilities(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String discipline,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String workday) {
        List<FacilityDTO> filteredFacilities = facilityService.filterFacilities(city, discipline, minRating, maxRating, workday);
        return ResponseEntity.ok(filteredFacilities);
    }

    @GetMapping("/isManagerOfFacility/{facilityId}/{userId}")
    public ResponseEntity<Boolean> isManagerOfFacility(@PathVariable Long facilityId, @PathVariable Long userId) {
        boolean isManager = facilityService.isManagerOfFacility(facilityId, userId);
        return ResponseEntity.ok(isManager);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/facilitiesByManager/{userId}")
    public ResponseEntity<List<Long>> getFacilitiesByManager(@PathVariable Long userId) {
        List<Long> facilityIds = facilityService.findFacilityIdsByManagerId(userId);
        return ResponseEntity.ok(facilityIds);
    }
}


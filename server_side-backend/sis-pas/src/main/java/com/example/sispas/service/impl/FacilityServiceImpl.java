package com.example.sispas.service.impl;

import com.example.sispas.dto.FacilityDTO;
import com.example.sispas.model.Facility;
import com.example.sispas.model.Manages;
import com.example.sispas.model.Role;
import com.example.sispas.model.User;
import com.example.sispas.repository.FacilityRepository;
import com.example.sispas.repository.ManagesRepository;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;
    private final ManagesRepository managesRepository;

    @Autowired
    public FacilityServiceImpl(FacilityRepository facilityRepository, UserRepository userRepository, ManagesRepository managesRepository) {
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
        this.managesRepository = managesRepository;
    }

    @Override
    public boolean isManagerOfFacility(Long facilityId, Long userId) {
        return managesRepository.existsByFacilityIdAndUserIdAndIsDeletedFalse(facilityId, userId);
    }

    @Override
    public FacilityDTO createFacility(FacilityDTO facilityDTO) {
        if (facilityRepository.existsByName(facilityDTO.getName())) {
            throw new IllegalArgumentException("Facility name already exists");
        }
        Facility facility = new Facility();
        facility.setName(facilityDTO.getName());
        facility.setDescription(facilityDTO.getDescription());
        facility.setAddress(facilityDTO.getAddress());
        facility.setCity(facilityDTO.getCity());
        facility.setCreatedAt(facilityDTO.getCreatedAt());
        facility.setRating(facilityDTO.getRating());
        facility.setActive(facilityDTO.isActive());
        facility.setDeleted(false);

        Facility savedFacility = facilityRepository.save(facility);

        return toDTO(savedFacility);
    }

    @Override
    public FacilityDTO getFacilityById(Long id) {
        Facility facility = facilityRepository.findById(id).orElseThrow(() -> new RuntimeException("Facility not found"));
        return toDTO(facility);
    }

    @Override
    public List<FacilityDTO> getAllFacilities() {
        return facilityRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public FacilityDTO activateFacility(Long id, Long userId, LocalDate startDate, LocalDate endDate) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        if (facility.isActive()) {
            throw new RuntimeException("Facility is already active");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        facility.setActive(true);

        Manages manages = new Manages();
        manages.setFacility(facility);
        manages.setUser(user);
        manages.setStartDate(startDate);
        manages.setEndDate(endDate);
        manages.setIsDeleted(false);
        managesRepository.save(manages);

        Facility savedFacility = facilityRepository.save(facility);

        return toDTO(savedFacility);
    }

    @Override
    public FacilityDTO deactivateFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        if (!facility.isActive()) {
            throw new RuntimeException("Facility is already inactive");
        }

        facility.setActive(false);
        Facility savedFacility = facilityRepository.save(facility);

        List<Manages> managesList = managesRepository.findByFacilityIdAndIsDeletedFalse(id);
        for (Manages manages : managesList) {
            manages.setIsDeleted(true);
            managesRepository.save(manages);
        }

        return toDTO(savedFacility);
    }

    @Override
    public FacilityDTO updateFacility(Long id, FacilityDTO facilityDTO) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        facility.setName(facilityDTO.getName());
        facility.setDescription(facilityDTO.getDescription());
        facility.setAddress(facilityDTO.getAddress());
        facility.setCity(facilityDTO.getCity());

        Facility updatedFacility = facilityRepository.save(facility);

        return toDTO(updatedFacility);
    }

    @Override
    public FacilityDTO deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));


        facility.setDeleted(true);
        Facility savedFacility = facilityRepository.save(facility);

        List<Manages> managesList = managesRepository.findByFacilityIdAndIsDeletedFalse(id);
        for (Manages manages : managesList) {
            manages.setIsDeleted(true);
            managesRepository.save(manages);
        }

        User user = managesList.get(0).getUser();
        List<Manages> activeManages = managesRepository.findByUserIdAndIsDeletedFalse(user.getId());
        if (activeManages.isEmpty()) {
            user.setRole(Role.user);
            userRepository.save(user);
        }

        return toDTO(savedFacility);
    }

    @Override
    public List<FacilityDTO> filterFacilities(String city, String discipline, Double minRating, Double maxRating, String workday) {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream()
                .filter(facility -> (city == null || facility.getCity().equalsIgnoreCase(city)))
                .filter(facility -> (discipline == null || facility.getDisciplines().stream().anyMatch(d -> d.getName().equalsIgnoreCase(discipline))))
                .filter(facility -> (minRating == null || facility.getRating() >= minRating))
                .filter(facility -> (maxRating == null || facility.getRating() <= maxRating))
                .filter(facility -> (workday == null || facility.getWorkdays().stream().anyMatch(w -> w.getDays().name().equalsIgnoreCase(workday))))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findFacilityIdsByManagerId(Long userId) {
        return managesRepository.findByUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(manages -> manages.getFacility().getId())
                .collect(Collectors.toList());
    }

    private FacilityDTO toDTO(Facility facility) {
        FacilityDTO dto = new FacilityDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setDescription(facility.getDescription());
        dto.setAddress(facility.getAddress());
        dto.setCity(facility.getCity());
        dto.setCreatedAt(facility.getCreatedAt());
        dto.setRating(facility.getRating());
        dto.setActive(facility.isActive());


        if (facility.getImages() != null) {
            dto.setImageIds(facility.getImages().stream().map(image -> image.getId()).collect(Collectors.toList()));
        }

        if (facility.getExercises() != null) {
            dto.setExerciseIds(facility.getExercises().stream().map(exercise -> exercise.getId()).collect(Collectors.toList()));
        }

        if (facility.getDisciplines() != null) {
            dto.setDisciplinesIds(facility.getDisciplines().stream().map(discipline -> discipline.getId()).collect(Collectors.toList()));
        }

        if (facility.getWorkdays() != null) {
            dto.setWorkdays(facility.getWorkdays().stream().map(workday -> workday.getId()).collect(Collectors.toList()));
        }

        dto.setDeleted(facility.isDeleted());

        return dto;
    }
}

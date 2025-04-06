package com.example.sispas.service.impl;

import com.example.sispas.dto.DisciplinesDTO;
import com.example.sispas.model.Discipline;
import com.example.sispas.model.Facility;
import com.example.sispas.repository.DisciplineRepository;
import com.example.sispas.repository.FacilityRepository;
import com.example.sispas.service.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisciplineServiceImpl implements DisciplineService {

    private final DisciplineRepository disciplineRepository;
    private final FacilityRepository facilityRepository;

    @Autowired
    public DisciplineServiceImpl(DisciplineRepository disciplineRepository, FacilityRepository facilityRepository) {
        this.disciplineRepository = disciplineRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<DisciplinesDTO> getDisciplinesByFacilityId(Long facilityId) {
        List<Discipline> disciplines = disciplineRepository.findByFacilityId(facilityId);
        return disciplines.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DisciplinesDTO createDiscipline(DisciplinesDTO disciplineDTO, Long facilityID) {
        System.out.println("Creating discipline for facility ID: " + facilityID);
        if (facilityID == null) {
            throw new IllegalArgumentException("Facility ID must not be null");
        }
        Discipline discipline = new Discipline();
        discipline.setName(disciplineDTO.getName());
        Facility facility = facilityRepository.findById(facilityID)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        discipline.setFacility(facility);
        Discipline savedDiscipline = disciplineRepository.save(discipline);
        return toDTO(savedDiscipline);
    }


    @Override
    public void deleteDiscipline(Long id) {
        disciplineRepository.deleteById(id);
    }

    private DisciplinesDTO toDTO(Discipline discipline) {
        DisciplinesDTO dto = new DisciplinesDTO();
        dto.setId(discipline.getId());
        dto.setName(discipline.getName());
        dto.setFacility(discipline.getFacility().getId());
        return dto;
    }
}

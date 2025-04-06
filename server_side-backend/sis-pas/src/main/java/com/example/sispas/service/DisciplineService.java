package com.example.sispas.service;

import com.example.sispas.dto.DisciplinesDTO;

import java.util.List;

public interface DisciplineService {
    List<DisciplinesDTO> getDisciplinesByFacilityId(Long facilityId);
    DisciplinesDTO createDiscipline(DisciplinesDTO disciplineDTO, Long facilityID);
    void deleteDiscipline(Long id);
}

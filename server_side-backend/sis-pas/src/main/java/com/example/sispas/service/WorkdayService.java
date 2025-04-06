package com.example.sispas.service;

import com.example.sispas.dto.WorkdaysDTO;

import java.util.List;

public interface WorkdayService {
    List<WorkdaysDTO> getWorkdaysByFacilityId(Long facilityId);
    WorkdaysDTO createWorkday(WorkdaysDTO workdayDTO);
    void deleteWorkday(Long id);
}

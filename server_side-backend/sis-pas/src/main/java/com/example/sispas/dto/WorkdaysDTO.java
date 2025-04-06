package com.example.sispas.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkdaysDTO {
    private Long id;
    private LocalDate validFrom;
    private String days;
    private LocalTime from;
    private LocalTime until;
    private Long facilityId;
}

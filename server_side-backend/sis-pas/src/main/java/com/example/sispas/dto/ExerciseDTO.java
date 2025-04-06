package com.example.sispas.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private Long id;
    private LocalDate startDate;
    private String from;
    private String until;
    private Long facilityId;
    private String facilityName;
    private long userId;
}



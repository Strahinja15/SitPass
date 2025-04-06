package com.example.sispas.dto;

import com.example.sispas.model.Facility;
import com.example.sispas.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ManagesDTO {
    private Long id;
    private User user;
    private Facility facility;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private Boolean isDeleted;
}

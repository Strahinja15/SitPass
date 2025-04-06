package com.example.sispas.model;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workdays")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workday_id", nullable = false)
    private Long id;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "days", nullable = false)
    private DaysOfWeek days;

    @Column(name = "from_time", nullable = false)
    private LocalTime from;

    @Column(name = "until_time", nullable = false)
    private LocalTime until;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}

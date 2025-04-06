package com.example.sispas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rate") // Ime tabele
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id", nullable = false)
    private Long id;

    @Column(name = "equipment", nullable = false)
    private int equipment;

    @Column(name = "staff", nullable = false)
    private int staff;

    @Column(name = "hygiene", nullable = false)
    private int hygiene;

    @Column(name = "space", nullable = false)
    private int space;

    @Column(name = "is_deleted")
    private boolean IsDeleted;

    @OneToOne
    @JoinColumn(name = "review_id", nullable = false)
    @JsonBackReference
    private Review review;
}

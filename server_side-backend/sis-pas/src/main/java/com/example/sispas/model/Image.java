package com.example.sispas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image_path", nullable = false)
    private String path;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;
}

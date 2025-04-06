package com.example.sispas.dto;

import com.example.sispas.model.Role;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String phoneNumber;
    private LocalDate birthday;
    private String address;
    private String city;
    private Role role;
    private String zipCode;
    private  String imageUrl;
}

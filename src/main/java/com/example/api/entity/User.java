package com.example.api.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @jakarta.persistence.Id
    @Id
    private String email;

    private String password;
    private boolean isActive;

}

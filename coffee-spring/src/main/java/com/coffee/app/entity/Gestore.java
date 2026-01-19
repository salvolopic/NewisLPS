package com.coffee.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gestori")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gestore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(length = 50)
    @Builder.Default
    private String ruolo = "Admin";

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione;

    @PrePersist
    protected void onCreate() {
        if (dataCreazione == null) {
            dataCreazione = LocalDateTime.now();
        }
    }
}

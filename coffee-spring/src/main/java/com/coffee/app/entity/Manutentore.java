package com.coffee.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "manutentori")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manutentore {

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

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String zona;

    @Column(length = 20)
    private String stato;

    @Column(name = "data_assunzione")
    private LocalDateTime dataAssunzione;

    @PrePersist
    protected void onCreate() {
        if (dataAssunzione == null) {
            dataAssunzione = LocalDateTime.now();
        }
        if (stato == null) {
            stato = "ATTIVO";
        }
    }
}

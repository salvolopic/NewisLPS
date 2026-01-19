package com.coffee.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clienti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal credito = BigDecimal.ZERO;

    @Column(name = "data_registrazione")
    private LocalDateTime dataRegistrazione;

    @Column(name = "distributore_connesso_id")
    private String distributoreConnessoId;

    @Column(length = 20)
    @Builder.Default
    private String ruolo = "CLIENTE";

    @PrePersist
    protected void onCreate() {
        if (dataRegistrazione == null) {
            dataRegistrazione = LocalDateTime.now();
        }
    }

    public boolean haCredito() {
        return credito.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isConnessoADistributore() {
        return distributoreConnessoId != null;
    }
}

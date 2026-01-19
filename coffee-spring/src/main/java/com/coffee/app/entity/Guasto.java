package com.coffee.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "guasti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoGuasto tipo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GravitaGuasto gravita;

    @Column(name = "data_rilevazione")
    private LocalDateTime dataRilevazione;

    @Column(name = "risolto")
    @Builder.Default
    private Boolean risolto = false;

    @Column(name = "data_risoluzione")
    private LocalDateTime dataRisoluzione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributore_id")
    @ToString.Exclude
    @JsonBackReference
    private Distributore distributore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addetto_id")
    @ToString.Exclude
    private Addetto addettoAssegnato;

    @PrePersist
    protected void onCreate() {
        if (dataRilevazione == null) {
            dataRilevazione = LocalDateTime.now();
        }
    }
}

package com.coffee.app.entity;

import jakarta.persistence.*;
import lombok.*;
import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addetti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "addetto")
@XmlAccessorType(XmlAccessType.FIELD)
public class Addetto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RuoloAddetto ruolo;

    @Column(name = "attivo")
    @Builder.Default
    private Boolean attivo = true;

    @Column(name = "data_assunzione")
    private LocalDateTime dataAssunzione;

    @OneToMany(mappedBy = "addettoAssegnato")
    @Builder.Default
    @ToString.Exclude
    @XmlTransient
    private List<Guasto> guastiAssegnati = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dataAssunzione == null) {
            dataAssunzione = LocalDateTime.now();
        }
    }
}

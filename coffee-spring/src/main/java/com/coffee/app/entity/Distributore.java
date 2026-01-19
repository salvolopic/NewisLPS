package com.coffee.app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "distributori")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "distributore")
@XmlAccessorType(XmlAccessType.FIELD)
public class Distributore {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 200)
    private String ubicazione;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatoDistributore stato;

    @Column(name = "livello_caffe")
    private Integer livelloCaffe;

    @Column(name = "livello_latte")
    private Integer livelloLatte;

    @Column(name = "livello_zucchero")
    private Integer livelloZucchero;

    @Column(name = "livello_cioccolato")
    private Integer livelloCioccolato;

    @Column(name = "livello_te")
    private Integer livelloTe;

    @Column(name = "livello_bicchieri")
    private Integer livelloBicchieri;

    @Column(name = "livello_biscotti")
    private Integer livelloBiscotti;

    @Column(name = "ultima_manutenzione")
    private LocalDateTime ultimaManutenzione;

    @OneToMany(mappedBy = "distributore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @XmlTransient
    @JsonManagedReference
    private List<Guasto> guasti = new ArrayList<>();

    public boolean haLivelliBassi() {
        return livelloCaffe < 30 || livelloLatte < 30 || livelloZucchero < 30
            || livelloCioccolato < 30 || livelloTe < 30 || livelloBicchieri < 30
            || livelloBiscotti < 30;
    }

    public boolean haGuastiAttivi() {
        return guasti != null && guasti.stream().anyMatch(g -> !g.getRisolto());
    }
}

package com.coffee.monitoring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Dati essenziali distributore per sistema monitoring (DB separato)
@Entity
@Table(name = "distributori_monitoring")
public class DistributoreMonitoring {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 200)
    private String ubicazione;

    @Column(nullable = false)
    private Double latitudine;

    @Column(nullable = false)
    private Double longitudine;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatoDistributore stato;

    @Column(name = "ultimo_heartbeat")
    private LocalDateTime ultimoHeartbeat;

    // Getter e Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUbicazione() {
        return ubicazione;
    }

    public void setUbicazione(String ubicazione) {
        this.ubicazione = ubicazione;
    }

    public Double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }

    public StatoDistributore getStato() {
        return stato;
    }

    public void setStato(StatoDistributore stato) {
        this.stato = stato;
    }

    public LocalDateTime getUltimoHeartbeat() {
        return ultimoHeartbeat;
    }

    public void setUltimoHeartbeat(LocalDateTime ultimoHeartbeat) {
        this.ultimoHeartbeat = ultimoHeartbeat;
    }
}

package com.coffee.monitoring.service;

import com.coffee.monitoring.entity.Distributore;
import com.coffee.monitoring.entity.StatoDistributore;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

// Service layer per gestione monitoring - tiene traccia heartbeat
// @Singleton crea una sola istanza del servizio condivisa da tutti
@Singleton
public class MonitoringService {

    private static final Logger LOGGER = Logger.getLogger(MonitoringService.class.getName());

    // EntityManager gestisce le operazioni sul database
    @PersistenceContext(unitName = "MonitoringPU")
    private EntityManager em;

    public List<Distributore> getAllDistributori() {
        return em.createQuery("SELECT d FROM Distributore d", Distributore.class)
                 .getResultList();
    }

    // Riceve e registra heartbeat da distributore
    public void registraHeartbeat(String distributoreId) {
        Distributore distributore = em.find(Distributore.class, distributoreId);

        if (distributore == null) {
            LOGGER.warning("Distributore non trovato: " + distributoreId);
            return;
        }

        distributore.setUltimoHeartbeat(LocalDateTime.now());

        // Rimette attivo solo se non è in manutenzione
        if (distributore.getStato() != StatoDistributore.MANUTENZIONE) {
            distributore.setStato(StatoDistributore.ATTIVO);
        }

        em.merge(distributore);
        LOGGER.info("Heartbeat registrato per distributore: " + distributoreId);
    }

    // Controlla quali distributori non rispondono e li marca come guasti
    public void controllaDistributoriGuasti() {
        LocalDateTime treMinutiFA = LocalDateTime.now().minusMinutes(3);

        List<Distributore> distributoriDaControllare = em.createQuery(
            "SELECT d FROM Distributore d WHERE d.stato <> :manutenzione",
            Distributore.class
        )
        .setParameter("manutenzione", StatoDistributore.MANUTENZIONE)
        .getResultList();

        for (Distributore distributore : distributoriDaControllare) {
            LocalDateTime ultimoHeartbeat = distributore.getUltimoHeartbeat();

            // Timeout di 3 minuti senza heartbeat = guasto
            if (ultimoHeartbeat == null || ultimoHeartbeat.isBefore(treMinutiFA)) {
                distributore.setStato(StatoDistributore.GUASTO);
                em.merge(distributore);
                LOGGER.warning("Distributore marcato come GUASTO (no heartbeat): " + distributore.getId());
            }
        }
    }

    public Distributore aggiungiDistributore(Distributore distributore) {
        distributore.setStato(StatoDistributore.ATTIVO);
        distributore.setUltimoHeartbeat(LocalDateTime.now());
        em.persist(distributore);
        return distributore;
    }

    public void rimuoviDistributore(String id) {
        Distributore distributore = em.find(Distributore.class, id);
        if (distributore != null) {
            em.remove(distributore);
        }
    }

    public Distributore mettiInManutenzione(String id) {
        Distributore distributore = em.find(Distributore.class, id);
        if (distributore != null) {
            distributore.setStato(StatoDistributore.MANUTENZIONE);
            em.merge(distributore);
        }
        return distributore;
    }

    public Distributore attivaDistributore(String id) {
        Distributore distributore = em.find(Distributore.class, id);
        if (distributore != null) {
            distributore.setStato(StatoDistributore.ATTIVO);
            distributore.setUltimoHeartbeat(LocalDateTime.now());
            em.merge(distributore);
        }
        return distributore;
    }
}

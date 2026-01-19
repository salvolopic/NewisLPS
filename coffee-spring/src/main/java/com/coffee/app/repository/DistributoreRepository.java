package com.coffee.app.repository;

import com.coffee.app.entity.Distributore;
import com.coffee.app.entity.StatoDistributore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

// Repository per l'entità Distributore, con metodi per operazioni di ricerca basate su stato, livelli e ubicazione.
public interface DistributoreRepository extends JpaRepository<Distributore, String> {

    // Find by stato
    List<Distributore> findByStato(StatoDistributore stato);
    
    // Find distributori con livelli bassi
    @Query("SELECT d FROM Distributore d WHERE d.livelloCaffe < :soglia " +
           "OR d.livelloLatte < :soglia " +
           "OR d.livelloZucchero < :soglia " +
           "OR d.livelloCioccolato < :soglia " +
           "OR d.livelloTe < :soglia " +
           "OR d.livelloBicchieri < :soglia")
    List<Distributore> findByLivelliBassi(@Param("soglia") Integer soglia);

    // Find by ubicazione
    List<Distributore> findByUbicazioneContainingIgnoreCase(String ubicazione);
}

package com.coffee.app.repository;

import com.coffee.app.entity.Addetto;
import com.coffee.app.entity.RuoloAddetto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddettoRepository extends JpaRepository<Addetto, Long> {

    Optional<Addetto> findByUsername(String username);

    List<Addetto> findByRuolo(RuoloAddetto ruolo);

    List<Addetto> findByAttivo(Boolean attivo);

    boolean existsByUsername(String username);
}

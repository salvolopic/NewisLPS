package com.coffee.app.repository;

import com.coffee.app.entity.Guasto;
import com.coffee.app.entity.GravitaGuasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuastoRepository extends JpaRepository<Guasto, Long> {

    List<Guasto> findByRisolto(Boolean risolto);

    List<Guasto> findByDistributoreId(String distributoreId);

    List<Guasto> findByDistributoreIdAndRisolto(String distributoreId, Boolean risolto);

    List<Guasto> findByGravita(GravitaGuasto gravita);

    List<Guasto> findByAddettoAssegnatoId(Long addettoId);
}

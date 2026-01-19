package com.coffee.app.service;

import com.coffee.app.entity.Addetto;
import com.coffee.app.entity.Guasto;
import com.coffee.app.entity.GravitaGuasto;
import com.coffee.app.repository.AddettoRepository;
import com.coffee.app.repository.GuastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuastoService {

    private final GuastoRepository guastoRepository;
    private final AddettoRepository addettoRepository;

    public List<Guasto> findAll() {
        return guastoRepository.findAll();
    }

    public Optional<Guasto> findById(Long id) {
        return guastoRepository.findById(id);
    }

    public List<Guasto> findNonRisolti() {
        return guastoRepository.findByRisolto(false);
    }

    public List<Guasto> findByDistributore(String distributoreId) {
        return guastoRepository.findByDistributoreId(distributoreId);
    }

    public List<Guasto> findAttiviByDistributore(String distributoreId) {
        return guastoRepository.findByDistributoreIdAndRisolto(distributoreId, false);
    }

    public List<Guasto> findByGravita(GravitaGuasto gravita) {
        return guastoRepository.findByGravita(gravita);
    }

    public List<Guasto> findByAddetto(Long addettoId) {
        return guastoRepository.findByAddettoAssegnatoId(addettoId);
    }

    public Guasto create(Guasto guasto) {
        return guastoRepository.save(guasto);
    }

    public Guasto risolvi(Long guastoId) {
        Optional<Guasto> guastoOpt = guastoRepository.findById(guastoId);
        if (guastoOpt.isEmpty()) {
            throw new RuntimeException("Guasto non trovato: " + guastoId);
        }
        Guasto guasto = guastoOpt.get();

        guasto.setRisolto(true);
        guasto.setDataRisoluzione(LocalDateTime.now());

        return guastoRepository.save(guasto);
    }

    public Guasto assegna(Long guastoId, Long addettoId) {
        Guasto guasto = guastoRepository.findById(guastoId)
            .orElseThrow(() -> new RuntimeException("Guasto non trovato: " + guastoId));

        Addetto addetto = addettoRepository.findById(addettoId)
            .orElseThrow(() -> new RuntimeException("Addetto non trovato: " + addettoId));

        guasto.setAddettoAssegnato(addetto);
        return guastoRepository.save(guasto);
    }

    public void delete(Long id) {
        guastoRepository.deleteById(id);
    }
}

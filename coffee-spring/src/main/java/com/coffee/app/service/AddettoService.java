package com.coffee.app.service;

import com.coffee.app.entity.Addetto;
import com.coffee.app.entity.RuoloAddetto;
import com.coffee.app.repository.AddettoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddettoService {

    private final AddettoRepository addettoRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Addetto> login(String username, String password) {
        Optional<Addetto> addettoOpt = addettoRepository.findByUsername(username);
        if (addettoOpt.isPresent()) {
            Addetto addetto = addettoOpt.get();
            if (passwordEncoder.matches(password, addetto.getPassword())) {
                return Optional.of(addetto);
            }
        }
        return Optional.empty();
    }

    public List<Addetto> findAll() {
        return addettoRepository.findAll();
    }

    public Optional<Addetto> findById(Long id) {
        return addettoRepository.findById(id);
    }

    public List<Addetto> findByRuolo(RuoloAddetto ruolo) {
        return addettoRepository.findByRuolo(ruolo);
    }

    public List<Addetto> findAttivi() {
        return addettoRepository.findByAttivo(true);
    }

    public Addetto create(Addetto addetto) {
        if (addettoRepository.existsByUsername(addetto.getUsername())) {
            throw new RuntimeException("Username già esistente: " + addetto.getUsername());
        }
        addetto.setPassword(passwordEncoder.encode(addetto.getPassword()));
        return addettoRepository.save(addetto);
    }

    public Addetto update(Addetto addetto) {
        return addettoRepository.save(addetto);
    }

    public void delete(Long id) {
        addettoRepository.deleteById(id);
    }

    public Addetto disattiva(Long id) {
        Optional<Addetto> addettoOpt = addettoRepository.findById(id);
        if (addettoOpt.isEmpty()) {
            throw new RuntimeException("Addetto non trovato: " + id);
        }
        Addetto addetto = addettoOpt.get();
        addetto.setAttivo(false);
        return addettoRepository.save(addetto);
    }

    public Addetto attiva(Long id) {
        Optional<Addetto> addettoOpt = addettoRepository.findById(id);
        if (addettoOpt.isEmpty()) {
            throw new RuntimeException("Addetto non trovato: " + id);
        }
        Addetto addetto = addettoOpt.get();
        addetto.setAttivo(true);
        return addettoRepository.save(addetto);
    }
}

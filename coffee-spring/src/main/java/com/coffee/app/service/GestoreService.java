package com.coffee.app.service;

import com.coffee.app.entity.Gestore;
import com.coffee.app.repository.GestoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestoreService {

    private final GestoreRepository gestoreRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Gestore> login(String username, String password) {
        Optional<Gestore> gestoreOpt = gestoreRepository.findByUsername(username);
        if (gestoreOpt.isPresent()) {
            Gestore gestore = gestoreOpt.get();
            if (passwordEncoder.matches(password, gestore.getPassword())) {
                return Optional.of(gestore);
            }
        }
        return Optional.empty();
    }

    public Optional<Gestore> findById(Long id) {
        return gestoreRepository.findById(id);
    }
}

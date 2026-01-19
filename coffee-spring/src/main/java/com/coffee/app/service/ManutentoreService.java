package com.coffee.app.service;

import com.coffee.app.entity.Manutentore;
import com.coffee.app.repository.ManutentoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManutentoreService {

    private final ManutentoreRepository manutentoreRepository;

    public Optional<Manutentore> login(String username, String password) {
        return manutentoreRepository.findByUsernameAndPassword(username, password);
    }

    public Optional<Manutentore> findById(Long id) {
        return manutentoreRepository.findById(id);
    }

    public List<Manutentore> findAll() {
        return manutentoreRepository.findAll();
    }

    public Manutentore create(Manutentore manutentore) {
        if (manutentoreRepository.findByUsername(manutentore.getUsername()).isPresent()) {
            throw new RuntimeException("Username " + manutentore.getUsername() + " già esistente");
        }
        return manutentoreRepository.save(manutentore);
    }

    public void delete(Long id) {
        manutentoreRepository.deleteById(id);
    }
}

package com.coffee.app.controller;

import com.coffee.app.entity.Manutentore;
import com.coffee.app.service.ManutentoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/manutentori")
@RequiredArgsConstructor
public class ManutentoreController {

    private final ManutentoreService manutentoreService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Manutentore>> findAll() {
        return ResponseEntity.ok(manutentoreService.findAll());
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Manutentore> login(@RequestBody Map<String, String> credentials) {
        Optional<Manutentore> manutentore = manutentoreService.login(credentials.get("username"), credentials.get("password"));
        if (manutentore.isPresent()) {
            return ResponseEntity.ok(manutentore.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Manutentore> findById(@PathVariable Long id) {
        Optional<Manutentore> manutentore = manutentoreService.findById(id);
        if (manutentore.isPresent()) {
            return ResponseEntity.ok(manutentore.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> create(@RequestBody Manutentore manutentore) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(manutentoreService.create(manutentore));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        manutentoreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

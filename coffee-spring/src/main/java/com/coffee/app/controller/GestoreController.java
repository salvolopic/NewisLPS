package com.coffee.app.controller;

import com.coffee.app.entity.Gestore;
import com.coffee.app.service.GestoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/gestori")
@RequiredArgsConstructor
public class GestoreController {

    private final GestoreService gestoreService;

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Gestore> login(@RequestBody Map<String, String> credentials) {
        Optional<Gestore> gestore = gestoreService.login(credentials.get("username"), credentials.get("password"));
        if (gestore.isPresent()) {
            return ResponseEntity.ok(gestore.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Gestore> findById(@PathVariable Long id) {
        Optional<Gestore> gestore = gestoreService.findById(id);
        if (gestore.isPresent()) {
            return ResponseEntity.ok(gestore.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

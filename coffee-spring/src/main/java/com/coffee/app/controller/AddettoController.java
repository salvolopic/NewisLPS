package com.coffee.app.controller;

import com.coffee.app.dto.AddettiWrapper;
import com.coffee.app.entity.Addetto;
import com.coffee.app.entity.RuoloAddetto;
import com.coffee.app.service.AddettoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/addetti")
@RequiredArgsConstructor
public class AddettoController {

    private final AddettoService addettoService;

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Addetto> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<Addetto> addetto = addettoService.login(username, password);
        if (addetto.isPresent()) {
            return ResponseEntity.ok(addetto.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Addetto>> findAll() {
        List<Addetto> addetti = addettoService.findAll();
        return ResponseEntity.ok(addetti);
    }

    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<AddettiWrapper> getAllXml() {
        List<Addetto> addetti = addettoService.findAll();
        AddettiWrapper wrapper = new AddettiWrapper(addetti);
        return ResponseEntity.ok(wrapper);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Addetto> findById(@PathVariable Long id) {
        Optional<Addetto> addetto = addettoService.findById(id);
        if (addetto.isPresent()) {
            return ResponseEntity.ok(addetto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/ruolo/{ruolo}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Addetto>> findByRuolo(@PathVariable RuoloAddetto ruolo) {
        List<Addetto> addetti = addettoService.findByRuolo(ruolo);
        return ResponseEntity.ok(addetti);
    }

    @GetMapping(value = "/attivi", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Addetto>> findAttivi() {
        List<Addetto> addetti = addettoService.findAttivi();
        return ResponseEntity.ok(addetti);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Addetto> create(@RequestBody Addetto addetto) {
        try {
            Addetto created = addettoService.create(addetto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{id}",
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Addetto> update(@PathVariable Long id, @RequestBody Addetto addetto) {
        addetto.setId(id);
        Addetto updated = addettoService.update(addetto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addettoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/disattiva", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Addetto> disattiva(@PathVariable Long id) {
        try {
            Addetto addetto = addettoService.disattiva(id);
            return ResponseEntity.ok(addetto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/attiva", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Addetto> attiva(@PathVariable Long id) {
        try {
            Addetto addetto = addettoService.attiva(id);
            return ResponseEntity.ok(addetto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

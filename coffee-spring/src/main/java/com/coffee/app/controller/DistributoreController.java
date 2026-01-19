package com.coffee.app.controller;

import com.coffee.app.dto.DistributoriWrapper;
import com.coffee.app.dto.ErogazioneRequest;
import com.coffee.app.entity.Distributore;
import com.coffee.app.entity.StatoDistributore;
import com.coffee.app.service.DistributoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/distributori")
@RequiredArgsConstructor
public class DistributoreController {

    private final DistributoreService distributoreService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Distributore>> findAll() {
        return ResponseEntity.ok(distributoreService.findAll());
    }

    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<DistributoriWrapper> getAllXml() {
        return ResponseEntity.ok(new DistributoriWrapper(distributoreService.findAll()));
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Distributore> findById(@PathVariable String id) {
        Optional<Distributore> distributore = distributoreService.findById(id);
        if (distributore.isPresent()) {
            return ResponseEntity.ok(distributore.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/attivi", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Distributore>> findAttivi() {
        return ResponseEntity.ok(distributoreService.findAttivi());
    }

    @GetMapping(value = "/livelli-bassi", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Distributore>> findConLivelliBassi(@RequestParam(defaultValue = "20") Integer soglia) {
        return ResponseEntity.ok(distributoreService.findConLivelliBassi(soglia));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> create(@RequestBody Distributore distributore) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(distributoreService.create(distributore));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errore", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}",
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Distributore> update(@PathVariable String id, @RequestBody Distributore distributore) {
        distributore.setId(id);
        return ResponseEntity.ok(distributoreService.update(distributore));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        distributoreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/ripristina-livelli", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Distributore> ripristinaLivelli(@PathVariable String id) {
        try {
            return ResponseEntity.ok(distributoreService.ripristinaLivelli(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/eroga", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> erogaBevanda(@PathVariable String id, @RequestBody ErogazioneRequest request) {
        try {
            if (request.getTipoBevanda() == null || request.getTipoBevanda().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errore", "Tipo bevanda obbligatorio"));
            }

            Distributore distributore = distributoreService.erogaBevanda(
                id,
                request.getTipoBevanda(),
                request.getZuccheri() != null ? request.getZuccheri() : 0,
                request.getConBiscotto() != null ? request.getConBiscotto() : false
            );

            return ResponseEntity.ok(distributore);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errore", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}/stato/{stato}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Distributore> cambiaStato(@PathVariable String id, @PathVariable StatoDistributore stato) {
        try {
            return ResponseEntity.ok(distributoreService.cambiaStato(id, stato));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

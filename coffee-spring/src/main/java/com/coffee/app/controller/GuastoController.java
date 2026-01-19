package com.coffee.app.controller;

import com.coffee.app.entity.Guasto;
import com.coffee.app.entity.GravitaGuasto;
import com.coffee.app.service.GuastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/guasti")
@RequiredArgsConstructor
public class GuastoController {

    private final GuastoService guastoService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Guasto>> findAll() {
        List<Guasto> guasti = guastoService.findAll();
        return ResponseEntity.ok(guasti);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Guasto> findById(@PathVariable Long id) {
        Optional<Guasto> guasto = guastoService.findById(id);
        if (guasto.isPresent()) {
            return ResponseEntity.ok(guasto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/non-risolti", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Guasto>> findNonRisolti() {
        List<Guasto> guasti = guastoService.findNonRisolti();
        return ResponseEntity.ok(guasti);
    }

    @GetMapping(value = "/distributore/{distributoreId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Guasto>> findByDistributore(@PathVariable String distributoreId) {
        List<Guasto> guasti = guastoService.findByDistributore(distributoreId);
        return ResponseEntity.ok(guasti);
    }

    @GetMapping(value = "/distributore/{distributoreId}/attivi", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Guasto>> findAttiviByDistributore(@PathVariable String distributoreId) {
        List<Guasto> guasti = guastoService.findAttiviByDistributore(distributoreId);
        return ResponseEntity.ok(guasti);
    }

    @GetMapping(value = "/gravita/{gravita}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Guasto>> findByGravita(@PathVariable GravitaGuasto gravita) {
        List<Guasto> guasti = guastoService.findByGravita(gravita);
        return ResponseEntity.ok(guasti);
    }

    @GetMapping(value = "/addetto/{addettoId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Guasto>> findByAddetto(@PathVariable Long addettoId) {
        List<Guasto> guasti = guastoService.findByAddetto(addettoId);
        return ResponseEntity.ok(guasti);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Guasto> create(@RequestBody Guasto guasto) {
        try {
            Guasto created = guastoService.create(guasto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/{id}/risolvi", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Guasto> risolvi(@PathVariable Long id) {
        try {
            Guasto guasto = guastoService.risolvi(id);
            return ResponseEntity.ok(guasto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/assegna", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Guasto> assegna(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Long addettoId = Long.valueOf(request.get("addettoId").toString());
            Guasto guasto = guastoService.assegna(id, addettoId);
            return ResponseEntity.ok(guasto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guastoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.coffee.app.controller;

import com.coffee.app.entity.Cliente;
import com.coffee.app.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Cliente>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Cliente> login(@RequestBody Map<String, String> credentials) {
        Optional<Cliente> cliente = clienteService.login(credentials.get("email"), credentials.get("password"));
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/registra",
                 consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Cliente> registra(@RequestBody Cliente cliente) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.registra(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/email/{email}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Cliente> findByEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteService.findByEmail(email);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/connetti", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> connetti(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            return ResponseEntity.ok(clienteService.connetti(id, request.get("distributoreId")));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}/disconnetti", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Cliente> disconnetti(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clienteService.disconnetti(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/ricarica", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> ricaricaCredito(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal importo = new BigDecimal(request.get("importo").toString());
            return ResponseEntity.ok(clienteService.ricaricaCredito(id, importo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}/scala-credito", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Cliente> scalaCredito(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal importo = new BigDecimal(request.get("importo").toString());
            return ResponseEntity.ok(clienteService.scalaCredito(id, importo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/distributore/{distributoreId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Cliente> findByDistributoreConnesso(@PathVariable String distributoreId) {
        Optional<Cliente> cliente = clienteService.findByDistributoreConnesso(distributoreId);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

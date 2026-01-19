package com.coffee.app.service;

import com.coffee.app.entity.Cliente;
import com.coffee.app.entity.Distributore;
import com.coffee.app.entity.StatoDistributore;
import com.coffee.app.repository.ClienteRepository;
import com.coffee.app.repository.DistributoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final DistributoreRepository distributoreRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Cliente> login(String email, String password) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (passwordEncoder.matches(password, cliente.getPassword())) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }

    public Cliente registra(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("Email già registrata: " + cliente.getEmail());
        }

        if (cliente.getCredito() == null) {
            cliente.setCredito(BigDecimal.ZERO);
        }

        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        cliente.setRuolo("CLIENTE");

        return clienteRepository.save(cliente);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Cliente connetti(Long clienteId, String distributoreId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente non trovato: " + clienteId);
        }
        Cliente cliente = clienteOpt.get();

        Optional<Distributore> distributoreOpt = distributoreRepository.findById(distributoreId);
        if (distributoreOpt.isEmpty()) {
            throw new RuntimeException("Distributore non trovato: " + distributoreId);
        }
        Distributore distributore = distributoreOpt.get();

        if (distributore.getStato() != StatoDistributore.ATTIVO) {
            throw new RuntimeException("Distributore non attivo. Stato: " + distributore.getStato());
        }

        cliente.setDistributoreConnessoId(distributoreId);
        return clienteRepository.save(cliente);
    }


    public Cliente disconnetti(Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente non trovato: " + clienteId);
        }
        Cliente cliente = clienteOpt.get();
        cliente.setDistributoreConnessoId(null);
        return clienteRepository.save(cliente);
    }

    public Cliente ricaricaCredito(Long clienteId, BigDecimal importo) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente non trovato: " + clienteId);
        }
        Cliente cliente = clienteOpt.get();

        BigDecimal nuovoCredito = cliente.getCredito().add(importo);
        cliente.setCredito(nuovoCredito);
        return clienteRepository.save(cliente);
    }


    public Cliente scalaCredito(Long clienteId, BigDecimal importo) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente non trovato: " + clienteId);
        }
        Cliente cliente = clienteOpt.get();

        if (cliente.getCredito().compareTo(importo) < 0) {
            throw new RuntimeException("Credito insufficiente");
        }

        BigDecimal nuovoCredito = cliente.getCredito().subtract(importo);
        cliente.setCredito(nuovoCredito);

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> findByDistributoreConnesso(String distributoreId) {
        return clienteRepository.findByDistributoreConnessoId(distributoreId);
    }
}

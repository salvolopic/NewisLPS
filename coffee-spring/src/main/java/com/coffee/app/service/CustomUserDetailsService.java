package com.coffee.app.service;

import com.coffee.app.entity.Addetto;
import com.coffee.app.entity.Cliente;
import com.coffee.app.repository.AddettoRepository;
import com.coffee.app.repository.ClienteRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final AddettoRepository addettoRepository;

    public CustomUserDetailsService(ClienteRepository clienteRepository, AddettoRepository addettoRepository) {
        this.clienteRepository = clienteRepository;
        this.addettoRepository = addettoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(username);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return new User(
                cliente.getEmail(),
                cliente.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + cliente.getRuolo()))
            );
        }

        Optional<Addetto> addettoOpt = addettoRepository.findByUsername(username);
        if (addettoOpt.isPresent()) {
            Addetto addetto = addettoOpt.get();
            return new User(
                addetto.getUsername(),
                addetto.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + addetto.getRuolo().name()))
            );
        }

        throw new UsernameNotFoundException("Utente non trovato: " + username);
    }
}

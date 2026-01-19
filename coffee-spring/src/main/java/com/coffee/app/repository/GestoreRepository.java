package com.coffee.app.repository;

import com.coffee.app.entity.Gestore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GestoreRepository extends JpaRepository<Gestore, Long> {

    Optional<Gestore> findByUsername(String username);

    Optional<Gestore> findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);
}

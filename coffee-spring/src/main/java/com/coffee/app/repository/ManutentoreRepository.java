package com.coffee.app.repository;

import com.coffee.app.entity.Manutentore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManutentoreRepository extends JpaRepository<Manutentore, Long> {

    Optional<Manutentore> findByUsername(String username);

    Optional<Manutentore> findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);
}

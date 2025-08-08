package com.tcc.epidemiologia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcc.epidemiologia.domain.EventoClinico;

@Repository
public interface EventoClinicoRepository extends JpaRepository<EventoClinico, Long> {
}

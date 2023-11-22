package com.desafio.coopvoting.repository;

import com.desafio.coopvoting.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}

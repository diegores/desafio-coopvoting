package com.desafio.coopvoting.model;

import com.desafio.coopvoting.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AgendaRepositoryTest {

    @Autowired
    private AgendaRepository agendaRepository;

    @Test
    public void testSaveAndRetrieveAgenda() {
        // Cria uma nova Agenda
        Agenda agenda = new Agenda();
        agenda.setTitle("Test Agenda");
        agenda.setVotingSessionEndTime(LocalDateTime.now().plusHours(1));
        agenda.setVotingResult("Open");

        // Salva a Agenda no banco de dados
        agendaRepository.save(agenda);

        // Recupera a Agenda do banco de dados pelo ID
        Agenda savedAgenda = agendaRepository.findById(agenda.getId()).orElse(null);

        // Verifica se a Agenda foi salva corretamente e pode ser recuperada
        assertEquals("Test Agenda", savedAgenda.getTitle());
        assertEquals(LocalDateTime.now().plusHours(1).truncatedTo(SECONDS), savedAgenda.getVotingSessionEndTime().truncatedTo(SECONDS));
        assertEquals("Open", savedAgenda.getVotingResult());
    }
}


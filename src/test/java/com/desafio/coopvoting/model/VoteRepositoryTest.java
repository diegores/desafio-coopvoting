package com.desafio.coopvoting.model;

import com.desafio.coopvoting.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class VoteRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private VoteRepository voteRepository;

    @Test
    @Transactional
    public void testSaveAndRetrieveVote() {
        // Criar uma instância de Vote para salvar no banco de dados
        Vote vote = new Vote();
        vote.setAgendaId(1L);
        vote.setMemberId(1L);
        vote.setVote("Yes");

        // Salvar a instância no banco de dados
        voteRepository.save(vote);
        entityManager.flush();
        entityManager.clear();

        // Recuperar a instância do banco de dados
        Vote savedVote = voteRepository.findById(vote.getId()).orElse(null);

        // Verificar se a instância foi salva corretamente e pode ser recuperada
        assertNotNull(savedVote);
        assertEquals(1L, savedVote.getAgendaId());
        assertEquals(1L, savedVote.getMemberId());
        assertEquals("Yes", savedVote.getVote());
    }
}


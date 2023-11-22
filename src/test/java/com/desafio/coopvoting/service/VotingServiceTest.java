package com.desafio.coopvoting.service;

import com.desafio.coopvoting.model.Agenda;
import com.desafio.coopvoting.model.Vote;
import com.desafio.coopvoting.repository.AgendaRepository;
import com.desafio.coopvoting.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ExternalSystemService externalSystemService;

    @InjectMocks
    private VotingService votingService;

    @Test
    public void testCreateAgenda() {
        String title = "Nova Pauta";
        when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> {
            Agenda agenda = invocation.getArgument(0);
            agenda.setId(1L); // Simula a atribuição de ID pelo banco de dados
            return agenda;
        });

        Long agendaId = votingService.createAgenda(title);

        assertNotNull(agendaId);
        assertEquals(1L, agendaId.longValue());
    }

    @Test
    public void testOpenVotingSession() {
        Long agendaId = 1L;
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(10);

        Agenda agenda = new Agenda();
        agenda.setId(agendaId);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));

        votingService.openVotingSession(agendaId, endTime);

        assertEquals(endTime, agenda.getVotingSessionEndTime());
        verify(agendaRepository, times(1)).save(agenda);
    }

    @Test
    public void testVote() {
        Long agendaId = 1L;
        Long memberId = 1L;
        String cpf = "648.402.160-02";
        String vote = "Sim";

        Agenda agenda = new Agenda();
        agenda.setId(agendaId);
        agenda.setVotingSessionEndTime(LocalDateTime.now().plusMinutes(1));

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(externalSystemService.isUserAbleToVote(anyString())).thenReturn(true);
        when(voteRepository.findByAgendaIdAndMemberId(agendaId, memberId)).thenReturn(Collections.emptyList());

        boolean result = votingService.vote(agendaId, memberId, cpf, vote);

        assertTrue(result);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    public void testCloseVotingSession() {
        Long agendaId = 1L;
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(1);

        Agenda agenda = new Agenda();
        agenda.setId(agendaId);
        agenda.setVotingSessionEndTime(endTime);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(voteRepository.findByAgendaId(agendaId)).thenReturn(Collections.emptyList());

        votingService.closeVotingSession(agendaId);

        // Verifica se a chamada para save ocorreu (o resultado pode variar com base na lógica específica)
        verify(agendaRepository, times(1)).save(agenda);
    }

    @Test
    public void testCalculateVotingResult() {
        Map<String, Integer> voteCount = new HashMap<>();
        voteCount.put("Sim", 5);
        voteCount.put("Não", 3);

        String result = votingService.calculateVotingResult(voteCount);

        assertEquals("Aprovado", result);
    }

    @Test
    public void testHasMemberVoted() {
        Long agendaId = 1L;
        Long memberId = 1L;

        when(voteRepository.findByAgendaIdAndMemberId(agendaId, memberId)).thenReturn(Collections.emptyList());

        assertFalse(votingService.hasMemberVoted(agendaId, memberId));
    }


    @Test
    public void testGetVotingResult() {
        Long agendaId = 1L;
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(1);
        String votingResult = "Aprovado";

        Agenda agenda = new Agenda();
        agenda.setId(agendaId);
        agenda.setVotingSessionEndTime(endTime);
        agenda.setVotingResult(votingResult);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));

        String result = votingService.getVotingResult(agendaId);

        assertEquals(votingResult, result);
    }

    @Test
    public void testGetVotingResultSessionStillOpen() {
        Long agendaId = 1L;
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(1);

        Agenda agenda = new Agenda();
        agenda.setId(agendaId);
        agenda.setVotingSessionEndTime(endTime);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                votingService.getVotingResult(agendaId));

        assertEquals("A sessão de votação ainda está aberta", exception.getMessage());
    }


    private Vote createVote(Long agendaId, String vote) {
        Vote newVote = new Vote();
        newVote.setAgendaId(agendaId);
        newVote.setMemberId(1L);
        newVote.setVote(vote);
        return newVote;
    }
}


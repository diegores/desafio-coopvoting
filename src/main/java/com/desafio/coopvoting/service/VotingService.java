package com.desafio.coopvoting.service;

import com.desafio.coopvoting.model.Agenda;
import com.desafio.coopvoting.model.Vote;
import com.desafio.coopvoting.repository.AgendaRepository;
import com.desafio.coopvoting.repository.VoteRepository;
import com.desafio.coopvoting.util.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VotingService {

    @Autowired
    AgendaRepository agendaRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    ExternalSystemService externalSystemService;

    @Autowired
    public VotingService(
            AgendaRepository agendaRepository,
            VoteRepository voteRepository,
            ExternalSystemService externalSystemService) {
        this.agendaRepository = agendaRepository;
        this.voteRepository = voteRepository;
        this.externalSystemService = externalSystemService;
    }

    @PostMapping("/createAgenda")
    public Long createAgenda(String title) {
        Agenda agenda = new Agenda();
        agenda.setTitle(title);
        agenda.setVotingSessionEndTime(LocalDateTime.now().plusMinutes(1)); // 1 minuto por padrão
        return agendaRepository.save(agenda).getId();
    }

    public void openVotingSession(Long agendaId, LocalDateTime endTime) {
        Agenda agenda = getAgendaById(agendaId);
        agenda.setVotingSessionEndTime(endTime);
        agendaRepository.save(agenda);
    }

    public boolean vote(Long agendaId, Long memberId, String cpf, String vote) {
        if(!Utils.isValidCpf(Utils.cleanCpf(cpf))){
            throw new RuntimeException("CPF inválido");
        }
        // Verificar a elegibilidade do associado antes de permitir o voto
        if (!externalSystemService.isUserAbleToVote(Utils.cleanCpf(cpf))) {
            throw new RuntimeException("Associado não elegível para votar");
        }

        Agenda agenda = getAgendaById(agendaId);

        // Verificar se a sessão de votação está aberta
        if (LocalDateTime.now().isAfter(agenda.getVotingSessionEndTime())) {
            throw new RuntimeException("A sessão de votação está fechada");
        }

        // Verificar se o associado já votou nesta pauta
        if (hasMemberVoted(agendaId, memberId)) {
            throw new RuntimeException("Associado já votou nesta pauta");
        }

        // Registrar o voto
        Vote newVote = new Vote();
        newVote.setAgendaId(agendaId);
        newVote.setMemberId(memberId);
        newVote.setVote(vote);
        voteRepository.save(newVote);

        return true;
    }

    public String getVotingResult(Long agendaId) {
        // Obtenha a agenda pelo ID
        Agenda agenda = getAgendaById(agendaId);

        // Verifique se a sessão de votação está fechada
        if (LocalDateTime.now().isBefore(agenda.getVotingSessionEndTime())) {
            throw new RuntimeException("A sessão de votação ainda está aberta");
        }
        // Obtenha o resultado da votação da agenda
        return agenda.getVotingResult();
    }

    public void closeVotingSession(Long agendaId) {
        Agenda agenda = getAgendaById(agendaId);

        // Verificar se a sessão de votação está fechada
        if (LocalDateTime.now().isBefore(agenda.getVotingSessionEndTime())) {
            throw new RuntimeException("A sessão de votação ainda está aberta");
        }

        // Obter todos os votos para esta pauta
        List<Vote> votes = voteRepository.findByAgendaId(agendaId);

        // Contagem dos votos
        Map<String, Integer> voteCount = new HashMap<>();
        for (Vote vote : votes) {
            String voteValue = vote.getVote();
            voteCount.put(voteValue, voteCount.getOrDefault(voteValue, 0) + 1);
        }

        // Dar o resultado da votação (pode variar de acordo com a lógica específica)
        String result = calculateVotingResult(voteCount);

        // Atualizar o resultado na pauta
        agenda.setVotingResult(result);
        agendaRepository.save(agenda);
    }

    public String calculateVotingResult(Map<String, Integer> voteCount) {
        int yesVotes = voteCount.getOrDefault("Sim", 0);
        int noVotes = voteCount.getOrDefault("Não", 0);

        if (yesVotes > noVotes) {
            return "Aprovado";
        } else if (noVotes > yesVotes) {
            return "Rejeitado";
        } else {
            return "Empate";
        }
    }

    private Agenda getAgendaById(Long agendaId) {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> new RuntimeException("Pauta não encontrada"));
    }

    public boolean hasMemberVoted(Long agendaId, Long memberId) {
        List<Vote> votes = voteRepository.findByAgendaIdAndMemberId(agendaId, memberId);
        return !votes.isEmpty();
    }
}

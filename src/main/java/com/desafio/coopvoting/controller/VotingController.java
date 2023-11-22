package com.desafio.coopvoting.controller;

import com.desafio.coopvoting.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/voting")
public class VotingController {
    @Autowired
    private VotingService votingService;

    @Autowired
    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @PostMapping("/createAgenda")
    public ResponseEntity<Long> createAgenda(@RequestParam String title) {
        Long createdAgendaId = votingService.createAgenda(title);
        return new ResponseEntity<>(createdAgendaId, HttpStatus.CREATED);
    }

    @PostMapping("/openVotingSession/{agendaId}")
    public ResponseEntity<Void> openVotingSession(
            @PathVariable Long agendaId,
            @RequestParam String duration) {
        LocalDateTime endTime = LocalDateTime.now().plus(Duration.parse(duration));
        votingService.openVotingSession(agendaId, endTime);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/vote/{agendaId}/{memberId}")
    public ResponseEntity<Void> vote(
            @PathVariable Long agendaId,
            @PathVariable Long memberId,
            @RequestParam String cpf,
            @RequestParam String vote) {
        votingService.vote(agendaId, memberId, cpf, vote);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/votingResult/{agendaId}")
    public ResponseEntity<String> getVotingResult(@PathVariable Long agendaId) {
        String result = votingService.getVotingResult(agendaId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
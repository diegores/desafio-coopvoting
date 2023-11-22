package com.desafio.coopvoting.controller;

import com.desafio.coopvoting.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VotingController.class)
public class VotingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @InjectMocks
    private VotingController votingController;

    @MockBean
    private VotingService votingService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateAgenda() throws Exception {
        String title = "Test Agenda";

        when(votingService.createAgenda(title)).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/createAgenda")
                        .param("title", title))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    public void testOpenVotingSession() throws Exception {
        Long agendaId = 1L;
        String duration = "PT1M";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/openVotingSession/{agendaId}", agendaId)
                        .param("duration", duration))
                .andExpect(status().isOk());

        verify(votingService, times(1)).openVotingSession(eq(agendaId), any(LocalDateTime.class));
    }

    @Test
    public void testVote() throws Exception {
        Long agendaId = 1L;
        Long memberId = 1L;
        String cpf = "64840216002";
        String vote = "Sim";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/voting/vote/{agendaId}/{memberId}", agendaId, memberId)
                        .param("cpf", cpf)
                        .param("vote", vote))
                .andExpect(status().isCreated());

        verify(votingService, times(1)).vote(eq(agendaId), eq(memberId), eq(cpf), eq(vote));
    }

    @Test
    public void testGetVotingResult() throws Exception {
        Long agendaId = 1L;
        String result = "Aprovado";

        when(votingService.getVotingResult(agendaId)).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/voting/votingResult/{agendaId}", agendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(result)));
    }
}


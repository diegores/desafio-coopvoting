package com.desafio.coopvoting.service;

import com.com.desafio.coopvoting.externalintegration.UserInfoResponse;
import com.desafio.coopvoting.model.model.UserVoteEnum.UserVoteEnum;
import com.desafio.coopvoting.util.Utils.Utils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class ExternalSystemServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalSystemService externalSystemService;

    @Test
    public void testIsUserAbleToVote() {
        // Configuração do mock para simular uma resposta válida
        UserInfoResponse validResponse = new UserInfoResponse();
        validResponse.setStatus(UserVoteEnum.VALID.getDescription());
        Mockito.when(restTemplate.getForObject(any(String.class), eq(UserInfoResponse.class)))
                .thenReturn(validResponse);

        // Teste
        boolean result = externalSystemService.isUserAbleToVote(Utils.cleanCpf("648.402.160-02"));

        // Adicione logs para ajudar a entender o que está acontecendo
        System.out.println("Result: " + result);

        // Verificação
        assertTrue(result, "Esperava-se que o usuário fosse capaz de votar");
    }
}
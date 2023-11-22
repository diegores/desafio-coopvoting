package com.desafio.coopvoting.service;

import com.com.desafio.coopvoting.externalintegration.UserInfoResponse;
import com.desafio.coopvoting.model.model.UserVoteEnum.UserVoteEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalSystemService {

    @Value("${external.system.url}")
    private String externalSystemUrl;

    private final RestTemplate restTemplate;

    public ExternalSystemService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isUserAbleToVote(String cpf) {
        String apiUrl = externalSystemUrl + "/users/" + cpf;
        try {
            UserInfoResponse response = restTemplate.getForObject(apiUrl, UserInfoResponse.class);

            // Se a resposta é nula, o usuário não existe e não pode votar
            if (response == null) {
                return false;
            }

            // Comparar com o enum UserVoteEnum.VALID diretamente
            return UserVoteEnum.VALID.equals(UserVoteEnum.fromDescription(response.getStatus()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}


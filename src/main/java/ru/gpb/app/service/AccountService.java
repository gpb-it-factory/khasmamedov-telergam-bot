package ru.gpb.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.gpb.app.dto.CreateAccountRequest;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AccountService {

    private final RestTemplate restTemplate;

    @Autowired
    public AccountService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String openAccount(CreateAccountRequest request) {
        try {
            long userId = request.userId();
            log.info("Creating account for userID: {} with accountName: {}", userId, request.accountName());
            String url = String.format("/users/%d/accounts", userId);
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            return handleResponse(response);
        } catch (HttpStatusCodeException e) {
            return handleHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    public String handleResponse(ResponseEntity<Void> response) {
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.NO_CONTENT) {
            log.info("Account is created");
            return "Счет создан";
        } else if (statusCode == HttpStatus.CONFLICT) {
            log.warn("Account already exists: " + response.getBody());
            return "Такой счет у данного пользователя уже есть: " + statusCode;
        } else {
            log.error("Cannot create account, status: " + response.getBody());
            return "Ошибка при создании счета: " + statusCode;
        }
    }

    public String handleHttpStatusCodeException(HttpStatusCodeException e) {
        String responseErrorString = new String(e.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        log.error("Cannot register account, HttpStatusCodeException: " + responseErrorString);
        return "Не могу зарегистрировать счет, ошибка: " + responseErrorString;
    }

    public String handleGeneralException(Exception e) {
        String generalErrorMessage = e.getMessage();
        log.error("Serious exception is happened: " + generalErrorMessage, e);
        return "Произошла серьезная ошибка во время создания счета: " + generalErrorMessage;
    }
}

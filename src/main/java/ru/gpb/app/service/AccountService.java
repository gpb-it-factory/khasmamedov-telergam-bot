package ru.gpb.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.gpb.app.dto.AccountListResponse;
import ru.gpb.app.dto.CreateAccountRequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    public /*ResponseEntity<AccountListResponse[]>*/ String getAccount(Long chatId) {
        log.info("Getting account details from userID: {}", chatId);
        ResponseEntity<AccountListResponse[]> accounts = restTemplate.getForEntity(
                "/users/chatId/accounts",
                AccountListResponse[].class
        );
        return "Список счетов пользователя: " + Arrays.asList(accounts.getBody());
        // подумать над:
        // 1. Список счетов пользователя внезапн может быть пуст
        // (т.е. пользователь зарегистрировался, но аккаунт еще не создал (или не захотел) - опшионал ?
        // 2. А если он пуст, я возвращаю просто что - пустой массив ?
        // 3. Плюс, ввести обработку ошибок.
    }
}

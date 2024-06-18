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
import java.util.Optional;

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
            return handleAccountRegistryResponse(response);
        } catch (HttpStatusCodeException e) {
            return handleAccountRegistryHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleAccountRegistryGeneralException(e);
        }
    }

    private String handleAccountRegistryResponse(ResponseEntity<Void> response) {
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

    private String handleAccountRegistryHttpStatusCodeException(HttpStatusCodeException e) {
        String responseErrorString = new String(e.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        log.error("Cannot register account, HttpStatusCodeException: " + responseErrorString);
        return "Не могу зарегистрировать счет, ошибка: " + responseErrorString;
    }

    private String handleAccountRegistryGeneralException(Exception e) {
        String generalErrorMessage = e.getMessage();
        log.error("Serious exception is happened: " + generalErrorMessage, e);
        return "Произошла серьезная ошибка во время создания счета: " + generalErrorMessage;
    }

    public String getAccount(Long chatId) {
        log.info("Getting account details from userID: {}", chatId);
        try {
            String url = String.format("/users/%d/accounts", chatId);
            ResponseEntity<AccountListResponse[]> accounts = restTemplate.getForEntity(
                    url,
                    AccountListResponse[].class
            );
            return handleGetAccountResponse(Optional.of(accounts));
        } catch (HttpStatusCodeException e) {
            return handleGetAccountHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGetAccountGeneralException(e);
        }
    }

    private String handleGetAccountResponse(Optional<ResponseEntity<AccountListResponse[]>> response) {
        if (response.isPresent() && response.get().getBody() != null) {
            AccountListResponse[] responses = response.get().getBody();
            if (responses.length == 0) {
                log.warn("No accounts found for user");
                return "Нет счетов у пользователя";
            }
            log.info("Users accounts found: {}", Arrays.asList(responses));
            return "Список счетов пользователя: " + Arrays.asList(responses);
        } else {
            log.error("Cannot retreive account details (empty response or no accounts were found)");
            return "Не могу получить счета (пустой ответ // не найдено счетов)";
        }
    }

    private String handleGetAccountHttpStatusCodeException(HttpStatusCodeException e) {
        String responseErrorString = new String(e.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        log.error("Cannot get accounts, HttpStatusCodeException: " + responseErrorString);
        return "Не могу получить счета, ошибка: " + responseErrorString;
    }

    private String handleGetAccountGeneralException(Exception e) {
        String generalErrorMessage = e.getMessage();
        log.error("Serious exception is happened: " + generalErrorMessage, e);
        return "Произошла серьезная ошибка во время получения счетов: " + generalErrorMessage;
    }
}

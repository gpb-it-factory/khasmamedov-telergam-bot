package ru.gpb.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.gpb.app.dto.CreateUserRequest;

@Service
@Slf4j
public class RegistrationService {

    private final RestTemplate restTemplate;
    private final String middleUrl;

    public RegistrationService(RestTemplate restTemplate, @Value("${khasmamedov-middle-service.url}") String middleUrl) {
        this.restTemplate = restTemplate;
        this.middleUrl = middleUrl;
    }

    public String register(CreateUserRequest request) {
        String url = middleUrl + "/users";
        try {
            log.info("Регистрирую пользователя по ЮзерАйди: {}", request.userId());
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            return handleResponse(response);
        } catch (HttpStatusCodeException e) {
            return handleHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    private String handleResponse(ResponseEntity<Void> response) {
        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            String success = "Пользователь создан";
            log.info(success);
            return success;
        } else {
            String error = "Не могу зарегистрировать пользователя, статус: " + response.getStatusCode();
            log.error(error);
            return error;
        }
    }

    private String handleHttpStatusCodeException(HttpStatusCodeException e) {
        String error = "Не могу зарегистрировать, ошибка: " + e.getResponseBodyAsString();
        log.error(error);
        return error;
    }

    private String handleGeneralException(Exception e) {
        String error = "Серьезная ошибка произошла: " + e.getMessage();
        log.error(error, e);
        return error;
    }
}


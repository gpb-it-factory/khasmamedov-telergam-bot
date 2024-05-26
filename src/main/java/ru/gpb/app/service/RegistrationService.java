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
            log.info("Регистрирую пользователя с Айди: {}", request.userId());
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            return handleResponse(response, request);
        } catch (HttpStatusCodeException e) {
            return handleHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    private String handleResponse(ResponseEntity<Void> response, CreateUserRequest request) {
        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            String errorMessage = "Ну удалось зарегистрировать, ошибка: " + response.getStatusCode();
            log.error(errorMessage);
            return errorMessage;
        }
        String successMessage = "Пользователь зарегистрирован с Айди: " + request.userId();
        log.info(successMessage);
        return successMessage;
    }

    private String handleHttpStatusCodeException(HttpStatusCodeException e) {
        String errorMessage = "Ну удалось зарегистрировать: " + e.getResponseBodyAsString();
        log.error(errorMessage);
        return errorMessage;
    }

    private String handleGeneralException(Exception e) {
        String errorMessage = "Произошел серьезный сбой: " + e.getMessage();
        log.error(errorMessage, e);
        return errorMessage;
    }
}


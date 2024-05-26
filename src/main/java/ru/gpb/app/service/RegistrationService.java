package ru.gpb.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.gpb.app.dto.CreateUserRequest;
import ru.gpb.app.dto.UserResponse;

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
            log.info("Registering user with id: {}", request.userId());
            ResponseEntity<UserResponse> response = restTemplate.postForEntity(url, request, UserResponse.class);
            return handleResponse(response, request);
        } catch (HttpStatusCodeException e) {
            return handleHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    private String handleResponse(ResponseEntity<UserResponse> response, CreateUserRequest request) {
        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            String error = "Cannot register user, status code is: " + response.getStatusCode();
            log.error(error);
            return error;
        }
        String success = String.format("User with %s is successfully registered!", request.userId());
        log.info(success);
        return success;
    }

    private String handleHttpStatusCodeException(HttpStatusCodeException e) {
        String error = "Cannot register: " + e.getResponseBodyAsString();
        log.error(error);
        return error;
    }

    private String handleGeneralException(Exception e) {
        String error = "Something serious is happened: " + e.getMessage();
        log.error(error, e);
        return error;
    }
}


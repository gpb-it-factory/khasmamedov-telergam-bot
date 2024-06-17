package ru.gpb.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.gpb.app.dto.CreateAccountRequest;
import ru.gpb.app.dto.Error;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AccountService service;
    private CreateAccountRequest accountRequest;

    /**
     * Specifically avoided here extras like properId, improperId, wrongId since it all can be actually tested with
     * same data
     */

    @BeforeEach
    public void setUp() {
        accountRequest = new CreateAccountRequest(
                123L,
                "Khasmamedov",
                "My first awesome account"
        );
    }

    @Test
    public void registerAccountIsSuccessful() {
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        String result = service.openAccount(accountRequest);
        assertThat("Счет создан").isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    @Test
    public void registerAccountIsAlreadyDoneBefore() {
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONFLICT));

        String result = service.openAccount(accountRequest);

        assertThat("Такой счет у данного пользователя уже есть: " + HttpStatus.CONFLICT).isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    @Test
    public void registerAccountProcessCannotBeDone() {
        @SuppressWarnings("unchecked")
        ResponseEntity<Void> response = mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenReturn(response);

        String result = service.openAccount(accountRequest);

        assertThat("Ошибка при создании счета: " + response.getStatusCode()).isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    @Test
    public void registerAccountInvokedInternalServerException() {
        Error accountCreationError = new Error(
                "Ошибка создания счета",
                "AccountCreationError",
                "500",
                UUID.randomUUID()
        );
        String jsonError = convertErrorToJson(accountCreationError);
        HttpStatusCodeException httpStatusCodeException =
                new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        null,
                        jsonError.getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenThrow(httpStatusCodeException);

        String result = service.openAccount(accountRequest);

        assertThat("Не могу зарегистрировать счет, ошибка: " + jsonError).isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    @Test
    public void registerAccountInvokedGeneralException() {
        Error accountCreationError = new Error(
                "Произошло что-то ужасное, но станет лучше, честно",
                "GeneralError",
                "123",
                UUID.randomUUID()
        );
        String jsonError = convertErrorToJson(accountCreationError);
        RuntimeException generalException =
                new RuntimeException(jsonError);
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenThrow(generalException);

        String result = service.openAccount(accountRequest);

        assertThat("Произошла серьезная ошибка во время создания счета: " + jsonError).isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    public static String convertErrorToJson(Error error) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}

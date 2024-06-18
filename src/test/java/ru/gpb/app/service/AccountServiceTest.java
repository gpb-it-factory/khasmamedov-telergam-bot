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
import ru.gpb.app.dto.AccountListResponse;
import ru.gpb.app.dto.CreateAccountRequest;
import ru.gpb.app.dto.Error;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    private String gettingAccUrl;

    private Long userId;

    @BeforeEach
    public void setUp() {
        accountRequest = new CreateAccountRequest(
                123L,
                "Khasmamedov",
                "My first awesome account"
        );
        userId = 868047670L;
        gettingAccUrl = String.format("/users/%d/accounts", userId);
    }

    @Test
    public void registerAccountWasOK() {
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        String result = service.openAccount(accountRequest);
        assertThat("Счет создан").isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    @Test
    public void gettingAccountsWasOK() {
        AccountListResponse[] accounts = new AccountListResponse[]{
                new AccountListResponse(
                        UUID.randomUUID(),
                        "Деньги на шашлык",
                        "203605.20"
                )
        };
        ResponseEntity<AccountListResponse[]> responseEntity = new ResponseEntity<>(accounts, HttpStatus.OK);
        when(restTemplate.getForEntity(gettingAccUrl, AccountListResponse[].class))
                .thenReturn(responseEntity);

        String result = service.getAccount(userId);

        String expected = "Список счетов пользователя: " + Arrays.asList(accounts);

        assertThat(expected).isEqualTo(result);
        verify(restTemplate, times(1))
                .getForEntity(gettingAccUrl, AccountListResponse[].class);
    }

    @Test
    public void registerAccountWasAlreadyDoneBefore() {
        String url = String.format("/users/%d/accounts", accountRequest.userId());
        when(restTemplate.postForEntity(url, accountRequest, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONFLICT));

        String result = service.openAccount(accountRequest);

        assertThat("Такой счет у данного пользователя уже есть: " + HttpStatus.CONFLICT).isEqualTo(result);
        verify(restTemplate, times(1))
                .postForEntity(url, accountRequest, Void.class);
    }

    @Test
    public void gettingAccountsReturnedNoData() {
        AccountListResponse[] accounts = {};
        ResponseEntity<AccountListResponse[]> responseEntity = new ResponseEntity<>(accounts, HttpStatus.OK);
        when(restTemplate.getForEntity(gettingAccUrl, AccountListResponse[].class))
                .thenReturn(responseEntity);

        String result = service.getAccount(userId);

        assertThat("Нет счетов у пользователя").isEqualTo(result);
    }

    @Test
    public void registerAccountProcessCouldNotBeDone() {
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
    public void gettingAccountProcessCouldNotBeDone() {
        ResponseEntity<AccountListResponse[]> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.getForEntity(gettingAccUrl, AccountListResponse[].class))
                .thenReturn(responseEntity);

        String result = service.getAccount(userId);

        assertThat("Не могу получить счета (пустой ответ // не найдено счетов)").isEqualTo(result);
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
    void getAccountHandledHttpStatusCodeException() {
        Error accountCreationError = new Error(
                "Ошибка получения счета",
                "AccountGettingError",
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

        when(restTemplate.getForEntity(gettingAccUrl, AccountListResponse[].class))
                .thenThrow(httpStatusCodeException);

        String result = service.getAccount(userId);

        assertThat("Не могу получить счета, ошибка: " + jsonError).isEqualTo(result);
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

    @Test
    void getAccountHandledGeneralException() {
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(restTemplate.getForEntity(gettingAccUrl, AccountListResponse[].class))
                .thenThrow(exception);

        String result = service.getAccount(userId);

        assertThat("Произошла серьезная ошибка во время получения счетов: Unexpected error").isEqualTo(result);
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


package ru.gpb.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import ru.gpb.app.dto.AccountListResponse;
import ru.gpb.app.dto.CreateAccountRequest;
import ru.gpb.app.dto.CreateTransferRequest;
import ru.gpb.app.dto.CreateTransferResponse;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    private final AccountClient accountClient;

    public AccountService(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    private String handleResponse(ResponseEntity<Void> response) {
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

    private String handleHttpStatusCodeException(HttpStatusCodeException e) {
        String responseErrorString = new String(e.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        log.error("Cannot register account, HttpStatusCodeException: " + responseErrorString);
        return "Не могу зарегистрировать счет, ошибка: " + responseErrorString;
    }

    private String handleGeneralException(Exception e) {
        String generalErrorMessage = e.getMessage();
        log.error("Serious exception is happened: " + generalErrorMessage, e);
        return "Произошла серьезная ошибка во время создания счета: " + generalErrorMessage;
    }

    public String openAccount(CreateAccountRequest request) {
        log.info("Creating account for userID: {} with accountName: {}", request.userId(), request.accountName());
        try {
            return handleResponse(accountClient.openAccount(request));
        } catch (HttpStatusCodeException e) {
            return handleHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGeneralException(e);
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

    public String getAccount(Long chatId) {
        log.info("Getting account details from userID: {}", chatId);
        try {
            return handleGetAccountResponse(Optional.of(accountClient.getAccount(chatId)));
        } catch (HttpStatusCodeException e) {
            return handleGetAccountHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleGetAccountGeneralException(e);
        }
    }

    private String handleMakeAccountTransferResponse(ResponseEntity<CreateTransferResponse> response) {
        HttpStatus currentStatus = response.getStatusCode();
        if (HttpStatus.OK == currentStatus && response.getBody() != null) {
            String transferId = response.getBody().transferId();
            log.error("Перевод успешно выполнен, ID перевода: " + transferId);
            return transferId;
        } else {
            log.error("Cannot make transfer, status: " + currentStatus);
            return "Не могу совершить денежный перевод: " + currentStatus;
        }
    }

    private String handleMakeAccountTransferHttpStatusCodeException(HttpStatusCodeException e) {
        String responseErrorString = new String(e.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        log.error("Cannot make funds transfer, HttpStatusCodeException: " + responseErrorString);
        return "Не могу выполнить денежный перевод, ошибка: " + responseErrorString;
    }

    private String handleMakeAccountTransferGeneralException(Exception e) {
        String generalErrorMessage = e.getMessage();
        log.error("Serious exception is happened while making funds transfer: " + generalErrorMessage, e);
        return "Произошла серьезная ошибка во время выполнения денежного перевода: " + generalErrorMessage;
    }

    public String makeAccountTransfer(@Valid CreateTransferRequest request) {
        try {
            log.info("Creating transfer for account1: {} to account2: {} with amount {}",
                    request.from(),
                    request.to(),
                    request.amount());
            return handleMakeAccountTransferResponse(accountClient.makeAccountTransfer(request));
        } catch (HttpStatusCodeException e) {
            return handleMakeAccountTransferHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleMakeAccountTransferGeneralException(e);
        }
    }






    //непонятная херня
    /*
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
     */

    //transfer

/*    private String handleMakeAccountTransferHttpStatusCodeException(HttpStatusCodeException e) {
        String responseErrorString = new String(e.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        log.error("Cannot make funds transfer, HttpStatusCodeException: " + responseErrorString);
        return "Не могу выполнить денежный перевод, ошибка: " + responseErrorString;
    }

    private String handleMakeAccountTransferGeneralException(Exception e) {
        String generalErrorMessage = e.getMessage();
        log.error("Serious exception is happened while making funds transfer: " + generalErrorMessage, e);
        return "Произошла серьезная ошибка во время выполнения денежного перевода: " + generalErrorMessage;
    }

    public String makeAccountTransfer(@Valid CreateTransferRequest request) {
        try {
            log.info("Creating transfer for account1: {} to account2: {} with amount {}",
                    request.from(),
                    request.to(),
                    request.amount());
            ResponseEntity<CreateTransferResponse> response =
                    restTemplate.postForEntity("/v2/transfers", request, CreateTransferResponse.class);
            return handleMakeAccountTransferResponse(response);
        } catch (HttpStatusCodeException e) {
            return handleMakeAccountTransferHttpStatusCodeException(e);
        } catch (Exception e) {
            return handleMakeAccountTransferGeneralException(e);
        }
    }*/
}

package ru.gpb.app.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.gpb.app.dto.CreateTransferRequest;
import ru.gpb.app.service.AccountService;
import ru.gpb.app.dto.Error;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferMoneyCommandTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferMoneyCommand command;

    private String from = "Khasmamedov";
    private String to = "paveldurov";
    private String sum = "203605.20";

    private String telegramMessage;

    private CreateTransferRequest request;

    @BeforeEach
    public void setUp() {
        telegramMessage = "/transfer " + to + " " + sum;
        request = new CreateTransferRequest(from, to, sum);
    }

    @Test
    public void getBotCommandSucceed() {
        String result = command.getBotCommand();
        assertThat("/transfer").isEqualTo(result);
    }

    @Test
    public void serviceInteractionServiceReturnedTrue() {
        boolean result = command.needsServiceInteraction();
        assertThat(result).isTrue();
    }

    @Test
    public void transferCommandWasNotExecutedDueToWrongParamsCount() {
        Message mockedMessage = mock(Message.class);

        when(mockedMessage.getText()).thenReturn("/transfer " + from);

        String result = command.executeCommand(mockedMessage);

        assertThat("Ввели неверную команду; \"/transfer [toTelegramUser] [amount]\" - верный ее формат!")
                .isEqualTo(result);
    }

    @Test
    public void transferCommandWasNotExecutedDueToWrongSum() {
        Message mockedMessage = mock(Message.class);
        //User mockedUser = mock(User.class);

        //when(mockedMessage.getFrom()).thenReturn(mockedUser);
        //when(mockedUser.getUserName()).thenReturn(to);
        when(mockedMessage.getText()).thenReturn("/transfer" + " " + to + " fivaproldg");

        String result = command.executeCommand(mockedMessage);

        assertThat("Неверный формат суммы - должен быть дробный формат типа 123.456")
                .isEqualTo(result);
    }

    @Test
    public void transferCommandWasExecuted() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn(from);
        when(mockedMessage.getText()).thenReturn(telegramMessage);

        when(accountService.makeAccountTransfer(request))
                .thenReturn("Перевод успешно выполнен, ID перевода: " + "52d2ef91-0b62-4d43-bb56-e7ec542ba8f8");

        String result = command.executeCommand(mockedMessage);

        assertThat("Перевод успешно выполнен, ID перевода: " + "52d2ef91-0b62-4d43-bb56-e7ec542ba8f8")
                .isEqualTo(result);
    }

    @Test
    public void transferCommandWasNotExecuted() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn(from);
        when(mockedMessage.getText()).thenReturn(telegramMessage);

        when(accountService.makeAccountTransfer(request)).thenReturn("Не могу совершить денежный перевод: " + "409");

        String result = command.executeCommand(mockedMessage);

        assertThat("Не могу совершить денежный перевод: " + "409").isEqualTo(result);
    }

    @Test
    public void transferCommandWasNotExecutedDueToHttpStatusCodeException() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn(from);
        when(mockedMessage.getText()).thenReturn(telegramMessage);

        Error userCreationError = new Error(
                "Произошло что-то ужасное, но станет лучше, честно",
                "GeneralError",
                "123",
                UUID.randomUUID()
        );
        String expectedResponse = "Не могу выполнить денежный перевод, ошибка: " + convertErrorToJson(userCreationError);

        when(accountService.makeAccountTransfer(request)).thenReturn(expectedResponse);

        String result = command.executeCommand(mockedMessage);

        assertThat(expectedResponse).isEqualTo(result);
    }

    @Test
    public void transferCommandWasNotExecutedDueToGeneralException() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn(from);
        when(mockedMessage.getText()).thenReturn(telegramMessage);

        Error userCreationError = new Error(
                "Произошло что-то ужасное, но станет лучше, честно",
                "GeneralError",
                "123",
                UUID.randomUUID()
        );
        String expectedResponse = "Произошла серьезная ошибка во время выполнения денежного перевода: Unexpected error";

        when(accountService.makeAccountTransfer(request)).thenReturn(expectedResponse);

        String result = command.executeCommand(mockedMessage);

        assertThat(expectedResponse).isEqualTo(result);
    }

    public static String convertErrorToJson(Error error) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

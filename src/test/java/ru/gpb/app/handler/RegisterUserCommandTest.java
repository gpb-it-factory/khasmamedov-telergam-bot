package ru.gpb.app.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.gpb.app.dto.CreateUserRequest;
import ru.gpb.app.dto.Error;
import ru.gpb.app.service.RegistrationService;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegisterUserCommand command;

    @Test
    public void getBotCommandSucceed() {
        String result = command.getBotCommand();
        assertThat("/register").isEqualTo(result);
    }

    @Test
    public void serviceInteractionServiceReturnedTrue() {
        boolean result = command.needsServiceInteraction();
        assertThat(result).isTrue();
    }

    @Test
    public void executeCommandCreatedUser() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getChatId()).thenReturn(123L);
        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn("Khasmamedov");

        CreateUserRequest request = new CreateUserRequest(mockedMessage.getChatId(), "Khasmamedov");
        when(registrationService.registerUser(request)).thenReturn("Пользователь создан");

        String result = command.executeCommand(mockedMessage);

        assertThat("Пользователь создан").isEqualTo(result);
    }

    @Test
    public void executeCommandDidntCreateUserDueToUserConflict() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getChatId()).thenReturn(123L);
        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn("Khasmamedov");

        CreateUserRequest request = new CreateUserRequest(mockedMessage.getChatId(), "Khasmamedov");
        String response = "Пользователь уже зарегистрирован: 409";
        when(registrationService.registerUser(request)).thenReturn(response);

        String result = command.executeCommand(mockedMessage);

        assertThat(response).isEqualTo(result);
    }

    @Test
    public void executeCommandDidntCreateUserDueToError() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getChatId()).thenReturn(123L);
        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn("Khasmamedov");

        CreateUserRequest request = new CreateUserRequest(mockedMessage.getChatId(), "Khasmamedov");
        String response = "Ошибка при регистрации пользователя: 400" ;
        when(registrationService.registerUser(request)).thenReturn(response);

        String result = command.executeCommand(mockedMessage);

        assertThat(response).isEqualTo(result);
    }

    @Test
    public void executeCommandReturnedWithHttpStatusCodeException() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getChatId()).thenReturn(123L);
        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn("Khasmamedov");

        Error userCreationError = new Error(
                "Произошло что-то ужасное, но станет лучше, честно",
                "GeneralError",
                "123",
                UUID.randomUUID()
        );
        String expectedResponse = "Не могу зарегистрировать, ошибка: " + convertErrorToJson(userCreationError);

        CreateUserRequest request = new CreateUserRequest(mockedMessage.getChatId(), "Khasmamedov");
        when(registrationService.registerUser(request)).thenReturn(expectedResponse);

        String result = command.executeCommand(mockedMessage);

        assertThat(expectedResponse).isEqualTo(result);
    }

    @Test
    public void executeCommandGotGeneralException() {
        Message mockedMessage = mock(Message.class);
        User mockedUser = mock(User.class);

        when(mockedMessage.getChatId()).thenReturn(123L);
        when(mockedMessage.getFrom()).thenReturn(mockedUser);
        when(mockedUser.getUserName()).thenReturn("Khasmamedov");

        CreateUserRequest request = new CreateUserRequest(mockedMessage.getChatId(), "Khasmamedov");
        when(registrationService.registerUser(request)).thenReturn("Success");

        String result = command.executeCommand(mockedMessage);

        assertThat(result).isEqualTo(expectedResponse);
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
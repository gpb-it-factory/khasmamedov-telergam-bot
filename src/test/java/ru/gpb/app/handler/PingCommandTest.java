package ru.gpb.app.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PingCommandTest {

    @InjectMocks
    private PingCommand command;

    @Test
    void getBotCommandSucceed() {
        String result = command.getBotCommand();
        assertThat("/ping").isEqualTo(result);
    }

    @Test
    void serviceInteractionServiceReturnedTrue() {
        boolean result = command.needsServiceInteraction();
        assertThat(result).isFalse();
    }

    @Test
    void executeCommandWorkedFine() {
        Message message = mock(Message.class);

        String result = command.executeCommand(message);

        assertThat("pong").isEqualTo(result);
    }
}
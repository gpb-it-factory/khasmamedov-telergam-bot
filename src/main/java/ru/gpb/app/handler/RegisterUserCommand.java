package ru.gpb.app.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gpb.app.dto.CreateUserRequest;
import ru.gpb.app.service.RegistrationService;

@Component
public class RegisterUserCommand implements Command {

    private final RegistrationService registrationService;

    public RegisterUserCommand(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public String getBotCommand() {
        return "/register";
    }

    @Override
    public boolean needsServiceInteraction() {
        return true;
    }

    @Override
    public String executeCommand(Message message) {
        CreateUserRequest request = new CreateUserRequest(message.getChatId());
        return registrationService.register(request);
    }
}


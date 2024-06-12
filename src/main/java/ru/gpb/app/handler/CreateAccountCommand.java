package ru.gpb.app.handler;

import org.telegram.telegrambots.meta.api.objects.Message;

public class CreateAccountCommand implements Command {
    @Override
    public String getBotCommand() {
        return "/createaccount";
    }

    @Override
    public boolean needsServiceInteraction() {
        return true;
    }

    @Override
    public String executeCommand(Message message) {
/*        CreateUserRequest request = new CreateUserRequest(message.getChatId());
        return registrationService.register(request);*/
        //new CreateAccountRequest(message.getChatId())
        return null;
    }
}

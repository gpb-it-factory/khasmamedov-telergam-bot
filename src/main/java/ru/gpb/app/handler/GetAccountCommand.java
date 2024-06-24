package ru.gpb.app.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gpb.app.service.AccountService;
@ExpectedCommandParams(0)
@Component
public class GetAccountCommand implements Command {

    private final AccountService accountService;

    public GetAccountCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getBotCommand() {
        return "/currentbalance";
    }

    @Override
    public boolean needsServiceInteraction() {
        return true;
    }

    @Override
    public String executeCommand(Message message, String... params) {
        return accountService.getAccount(message.getChatId());
    }
}

package ru.gpb.app.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gpb.app.dto.CreateTransferRequest;
import ru.gpb.app.service.AccountService;

@ExpectedCommandParams(2)

@Slf4j
@Component
public class TransferMoneyCommand implements Command {

    private final AccountService accountService;

    public TransferMoneyCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getBotCommand() {
        return "/transfer";
    }

    @Override
    public boolean needsServiceInteraction() {
        return true;
    }

    private boolean isCommandGivenProperly(String[] transferData) {
        int dataRealLength = transferData.length;
        boolean result = true;
        if (transferData.length != 2) {
            log.error("Wrong parameters quantity came from Telegram: should be 2, but were: {}", dataRealLength);
            result = false;
        }
        return result;
    }

    private boolean isMoneyFormatBad(String transferDatum) {
        boolean result;
        try {
            Double.parseDouble(transferDatum);
            result = false;
        } catch (NumberFormatException e) {
            log.error("Wrong [amount] format: was {} instead of double: ", transferDatum, e);
            result = true;
        }
        return result;
    }

    @Override
    public String executeCommand(Message message, String... params) {
        if (!isCommandGivenProperly(params)) {
            return "Ввели неверную команду; \"/transfer [toTelegramUser] [amount]\" - верный ее формат!";
        }

        String transferDatum = params[1];
        if (isMoneyFormatBad(transferDatum)) {
            return "Неверный формат суммы - должен быть дробный формат типа 123.456";
        }

        return accountService.makeAccountTransfer(
                new CreateTransferRequest(
                        message.getFrom().getUserName(),
                        params[0],
                        transferDatum
                )
        );
    }
}

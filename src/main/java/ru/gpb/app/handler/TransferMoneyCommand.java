package ru.gpb.app.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gpb.app.dto.CreateTransferRequest;
import ru.gpb.app.service.AccountService;

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

    private boolean areIncomingParamsBad(String[] transferData) {
        int dataRealLength = transferData.length;
        if (dataRealLength != 2) {
            log.error("Wrong parameters quantity came from Telegram: should be 2, but were: {}", dataRealLength);
        }
        return true;
    }
    private boolean isMoneyFormatBad(String transferDatum) {
        try {
            Double.parseDouble(transferDatum);
        } catch (NumberFormatException e) {
            log.error("Wrong [amount] format: was {} instead of double: ", transferDatum, e);
        }
        return true;
    }

    @Override
    public String executeCommand(Message message) {
        String[] transferData = message.getText().substring("/transfer ".length()).split(" ", 2);

        if (areIncomingParamsBad(transferData)) {
            return "Ввели неверную команду; \"/transfer [toTelegramUser] [amount]\" - верный ее формат!";
        }

        String transferDatum = transferData[1];
        if (isMoneyFormatBad(transferDatum)) {
            return "Неверный формат суммы - должен быть дробный формат типа 123.456";
        }

        return accountService.makeAccountTransfer(
                new CreateTransferRequest(
                        message.getFrom().getUserName(),
                        transferData[0],
                        transferDatum
                )
        );
    }
}

package ru.gpb.app.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
//@RequiredArgsConstructor
public class AppBankBotComponent extends TelegramLongPollingBot {

    private final String botUsername;
    //private final String botToken;

    @Autowired
    public AppBankBotComponent(@Value("${bot.name}") String botUsername, @Value("${bot.token}") String botToken) {
        //this.botToken = botToken;
                super(botToken);
        this.botUsername = botUsername;
    }
    @Override
    public String getBotUsername() {
        return botUsername;
    }

/*    @Override
    public String getBotToken() {
        //return botToken;
    }*/

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText();
            Long currChatId = update.getMessage().getChatId();

            //тут добавить логику, пинг-понг - базовый функционал
            switch (msg) {
                case "/start":
                    sendMessage(currChatId, "hi!");
                    break;
                default:
                    sendMessage(currChatId, "wrong msg");
            }
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
            log.info("\"hi\" отправлено в чат '{}'", chatId); //placeholder на мессагу ?
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения бота", e); // отрефакторить ?
        }
    }

}


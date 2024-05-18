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
public class AppBankBotComponent extends TelegramLongPollingBot {

    private final String botUsername;

    @Autowired
    public AppBankBotComponent(@Value("${bot.name}") String botUsername, @Value("${bot.token}") String botToken) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText();
            Long currChatId = update.getMessage().getChatId();

            switch (msg) {
                case "/ping":
                    sendMessage(currChatId, "pong");
                    break;
                default:
                    sendMessage(currChatId, "Дефолтное сообщение");
            }
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
            log.info("{} только что отправлено в чат '{}'", message, chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения бота", e);
        }
    }
}


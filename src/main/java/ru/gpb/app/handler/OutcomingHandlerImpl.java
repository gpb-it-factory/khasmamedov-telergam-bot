package ru.gpb.app.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

@Component
public class OutcomingHandlerImpl implements OutcomingHandler {

    private final Map<String, String> messageMap = new HashMap<>();

    public OutcomingHandlerImpl() {
        messageMap.put("/ping", "pong");
        messageMap.put("/help", "no help for you now, use '/ping' command instead");
    }

    @Override
    public SendMessage outputtingMessageSender(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(
                        messageMap.
                                getOrDefault(
                                        message.getText(),
                                        "no such command"
                                )
                )
                .build();
    }
}
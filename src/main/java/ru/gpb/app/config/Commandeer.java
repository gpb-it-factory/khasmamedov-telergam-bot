package ru.gpb.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gpb.app.handler.Command;
import ru.gpb.app.handler.HelpCommandImpl;
import ru.gpb.app.handler.PingCommandImpl;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Commandeer {

    @Bean
    public Map<String, Command> commandMsg() {
        final Map<String, Command> messageMap = new HashMap<>();
        messageMap.put("/ping", new PingCommandImpl());
        messageMap.put("/help", new HelpCommandImpl());
        return messageMap;
    }
}

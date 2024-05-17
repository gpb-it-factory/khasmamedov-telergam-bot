package ru.gpb.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class KhasmamedovTelegramBot {

	public static void main(String[] args) throws TelegramApiException {

		SpringApplication.run(KhasmamedovTelegramBot.class, args);
	}
}
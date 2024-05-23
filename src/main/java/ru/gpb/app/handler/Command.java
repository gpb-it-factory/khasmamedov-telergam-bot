package ru.gpb.app.handler;

public interface Command {
    String getBotCommand();

    //String sendTextAnswer();
    public String executeTextCommand();
}

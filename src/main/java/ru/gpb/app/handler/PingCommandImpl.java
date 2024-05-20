package ru.gpb.app.handler;

public class PingCommandImpl implements Command {
    @Override
    public String executeTextCommand() {
        return "pong";
    }
}

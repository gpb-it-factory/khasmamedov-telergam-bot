package ru.gpb.app.handler;

public class HelpCommandImpl implements Command {
    @Override
    public String executeTextCommand() {
        return "no help for you now, use '/ping' command instead";
    }
}

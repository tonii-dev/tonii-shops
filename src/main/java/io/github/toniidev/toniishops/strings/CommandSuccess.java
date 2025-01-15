package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

public enum CommandSuccess {
    COMMAND_EXECUTED_SUCCESSFULLY(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("Command executed successfully.").setColor('7')
            .get());

    private final String message;

    CommandSuccess(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

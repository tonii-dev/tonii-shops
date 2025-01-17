package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public enum CommandString {
    COMMAND_USAGE(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("[Command usage]").setColor('e')
            .append("Here is how to use the command:").setColor('7')
            .get()),
    COMMAND_DESCRIPTION(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("[Command description]").setColor('e')
            .append("A short description of this command:").setColor('7')
            .get());

    private final String message;

    CommandString(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }

    public String getFinalMessage(PluginCommand command){
        String value = "";
        switch (this){
            case COMMAND_USAGE -> value = new StringFactory(this.getMessage())
                    .append(command.getUsage().replace("<command>", command.getName())).setColor('f')
                    .get();
            case COMMAND_DESCRIPTION -> value = new StringFactory(this.getMessage())
                    .append(command.getDescription()).setColor('f')
                    .get();
        }

        return value;
    }
}

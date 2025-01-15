package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

/**
 * Enum used to store all Strings sent to the players as error messages.
 * I use to store all the Strings I use in an enum to make
 * editing easier.
 */
public enum CommandError {
    PLAYER_DOES_NOT_HAVE_PERMISSION(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("The specified user does not have that permission.").setColor('7')
            .get()),
    PLAYER_ALREADY_HAS_PERMISSION(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("The specified user already has that permission.").setColor('7')
            .get()),
    COMMAND_NEEDS_OP(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("You must be an operator in order to use this command!").setColor('7')
            .get()),
    /**
     * When I use this, the code is usually like this: Player#sendMessage(new StringFactory(%this_enum%).append(%command%#getUsage()).setColor('f')
     */
    INVALID_COMMAND_USAGE(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("Invalid command usage. You should use this command like this:").setColor('7')
            .get()),
    /**
     * When I use this, the code is usually like this: Player#sendMessage(%this_enum% +
     * StringUtils#formatColorCodes('&', "&r&f" + %permission%#getName"))
     */
    MISSING_PERMISSIONS(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("You are not allowed to use this command. Permission needed: ").setColor('7')
            .get());

    private final String message;

    CommandError(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

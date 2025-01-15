package io.github.toniidev.toniishops.strings;

/**
 * Enum used to store all Strings sent to the console.
 * I use to store all the Strings I use in an enum to make
 * editing easier.
 */
public enum ConsoleString {
    COMMAND_NOT_EXECUTABLE_FROM_CONSOLE("Sorry, you can't execute this command from console."),
    /**
     * When I use this, the code is usually like this: getLogger#warning(%this_enum% + stack.getType)
     */
    MISSING_ITEM_META("I tried to edit an ItemStack with no ItemMeta. Specified ItemStack: ");

    private final String message;

    ConsoleString(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

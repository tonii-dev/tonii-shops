package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

public enum GeneralMessage {
    NOT_ENOUGH_INVENTORY_SPACE(new StringFactory()
            .append("[Game]").setColor('a')
            .append("Inventory:").setColor('e')
            .append("Your inventory was full, so the Item you should have received" +
                    "was sent to stashed. You can see stashed items with this command:").setColor('7')
            .append("/stashed").setColor('f')
            .get());

    private final String message;

    GeneralMessage(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

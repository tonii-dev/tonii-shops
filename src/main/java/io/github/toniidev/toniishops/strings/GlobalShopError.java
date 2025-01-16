package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

public enum GlobalShopError {
    INVALID_ITEM(new StringFactory()
            .append("[Server]").setColor('a')
            .append("Global shop:").setColor('e')
            .append("The item you are trying to sell is invalid.").setColor('7')
            .get()),
    ITEM_CANNOT_BE_SOLD(new StringFactory()
            .append("[Server]").setColor('a')
            .append("Global shop:").setColor('e')
            .append("This item cannot be sold.").setColor('7')
            .get());

    private final String message;

    GlobalShopError(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

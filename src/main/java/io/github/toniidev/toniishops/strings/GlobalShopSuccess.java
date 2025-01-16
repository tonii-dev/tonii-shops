package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

public enum GlobalShopSuccess {
    /**
     * When I use this, the code is usually like this: Player#sendMessage(new StringFactory(%this_enum%).append(GlobalShopItem.getPrice() + "$").setColor('f').get())
     */
    ITEM_SOLD_SUCCESSFULLY(new StringFactory()
            .append("[Server]").setColor('a')
            .append("Global shop:").setColor('e')
            .append("Item sold successfully for").setColor('7')
            .get());

    private final String message;

    GlobalShopSuccess(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

public enum ShopSuccess {
    SHOP_BROKE_SUCCESSFULLY(new StringFactory()
            .append("[World]").setColor('a')
            .append("Shop:").setColor('e')
            .append("Shop broke successfully.").setColor('7')
            .get()),
    SHOP_ITEMS_SENT_TO_INVENTORY(new StringFactory()
            .append("[World]").setColor('a')
            .append("Shop:").setColor('e')
            .append("Items that were in the shop were sent to your inventory.").setColor('7')
            .get());

    private final String message;

    ShopSuccess(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

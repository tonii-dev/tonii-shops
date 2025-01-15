package io.github.toniidev.toniishops.strings;

import io.github.toniidev.toniishops.factories.StringFactory;

public enum ShopError {
    NO_SHOPS(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("You have no shops to manage.").setColor('7')
            .get()),
    NO_SHOPS_TO_BROWSE(new StringFactory()
            .append("[tonii-shops]").setColor('a')
            .append("Command:").setColor('e')
            .append("There are no shops to browse.").setColor('7')
            .get()),
    NOT_ENOUGH_MONEY(new StringFactory()
            .append("[World]").setColor('a')
            .append("Shop:").setColor('e')
            .append("You don't have enough money to buy from this shop!").setColor('7')
            .get()),
    NO_OWNER(new StringFactory()
            .append("[World]").setColor('a')
            .append("Shop:").setColor('e')
            .append("You must be the owner to break this shop!").setColor('7')
            .get());

    private final String message;

    ShopError(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
}

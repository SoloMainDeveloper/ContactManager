package org.example.state;

/**
 * Функциональная операция пользователя над ботом
 */
public enum Operation {
    MAIN_MENU(true),

    CONTACTS_MENU(true),
    ADD_CONTACT(true),
    FIND_CONTACT(true),
    GET_ALL_CONTACTS(true),

    CURRENT_CONTACT_MENU(true),
    EDIT_CONTACT(false),
    BLOCK_CONTACT(false),
    DELETE_CONTACT(false),

    GROUPS_MENU(true),
    ADD_GROUP(true),
    FIND_GROUP(true),
    GET_ALL_GROUPS(true),

    CURRENT_GROUP_MENU(true),
    EDIT_GROUP(false),
    DELETE_GROUP(false);

    private final boolean shouldClearContext;

    Operation(boolean shouldClearContext) {
        this.shouldClearContext = shouldClearContext;
    }

    public boolean shouldClearContext() {
        return shouldClearContext;
    }
}

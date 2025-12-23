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
    DELETE_GROUP(false),

    DATA_MENU(true),
    IMPORT_CONTACTS(true),
    EXPORT_CONTACTS(true);

    /**
     * Показывает необходимо ли очистить контекст перед установкой данной операции
     */
    private final boolean shouldClearContext;

    /**
     * Конструктор
     *
     * @param shouldClearContext показывает необходимо ли очистить контекст перед
     *                           установкой данной операции
     */
    Operation(boolean shouldClearContext) {
        this.shouldClearContext = shouldClearContext;
    }

    /**
     * Возвращает true, если необходимо ли очистить контекст перед установкой данной
     * операции
     */
    public boolean shouldClearContext() {
        return shouldClearContext;
    }
}

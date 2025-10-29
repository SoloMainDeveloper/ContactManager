package org.example.state;

import org.example.operations.*;

/**
 * Функциональная операция пользователя над ботом
 */
public enum Operation {
    MAIN_MENU,

    CONTACTS_MENU,
    ADD_CONTACT,
    FIND_CONTACT,
    GET_ALL_CONTACTS,
    EDIT_CONTACT,
    BLOCK_CONTACT,
    DELETE_CONTACT,
    GET_CONTACT_INFO;

    public OperationHandler getHandler() {
        return switch(this){
            case MAIN_MENU -> new MainMenuHandler();
            case CONTACTS_MENU -> new ContactsMenuHandler();
            case ADD_CONTACT -> new AddContactHandler();
            case FIND_CONTACT -> new FindContactHandler();
            case GET_ALL_CONTACTS -> null;
            case EDIT_CONTACT -> null;
            case BLOCK_CONTACT -> null;
            case DELETE_CONTACT -> null;
            case GET_CONTACT_INFO -> null;
        };
    }
}

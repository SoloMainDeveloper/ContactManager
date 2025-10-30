package org.example.state;

import org.example.operations.*;
import org.example.operations.GetAllContactsHandler;

/**
 * Функциональная операция пользователя над ботом
 */
public enum Operation {
    MAIN_MENU,

    CONTACTS_MENU,
    ADD_CONTACT,
    FIND_CONTACT,
    GET_ALL_CONTACTS,

    CURRENT_CONTACT_MENU,
    EDIT_CONTACT,
    BLOCK_CONTACT,
    DELETE_CONTACT,
    GET_CONTACT_INFO;

    /**
     * Возвращает обработчик для данного вида операции
     */
    public OperationHandler getHandler() {
        return switch(this){
            case MAIN_MENU -> new MainMenuHandler();
            case CONTACTS_MENU -> new ContactsMenuHandler();
            case ADD_CONTACT -> new AddContactHandler();
            case FIND_CONTACT -> new FindContactHandler();
            case GET_ALL_CONTACTS -> new GetAllContactsHandler();
            case CURRENT_CONTACT_MENU -> new CurrentContactMenuHandler();
            case EDIT_CONTACT -> new EditContactHandler();
            case BLOCK_CONTACT -> new BlockContactHandler();
            case DELETE_CONTACT -> new DeleteContactHandler();
            case GET_CONTACT_INFO -> new GetContactInfoHandler();
        };
    }
}

package org.example.keyboardcreator;

import org.example.entity.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, создающий клавиатуры ReplyKeyboardMarkup
 */
public class ReplyKeyboardCreator {
    /**
     * Создаёт ReplyKeyboardMarkup на основе переданного списка строк
     */
    public ReplyKeyboardMarkup createKeyboard(List<String> buttonsText) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (int i = 0; i < buttonsText.size(); i += 2) {
            KeyboardRow currentRow = new KeyboardRow();
            currentRow.add(buttonsText.get(i));
            if(i + 1 < buttonsText.size()){
                currentRow.add(buttonsText.get(i + 1));
            }
            keyboard.add(currentRow);
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    /**
     * Возвращает кнопки главного меню
     */
    public ReplyKeyboardMarkup mainMenu(){
        return createKeyboard(List.of("Контакты"));
    }

    /**
     * Возвращает кнопки меню контактов
     */
    public ReplyKeyboardMarkup contactsMenu() {
        return createKeyboard(List.of("Добавить", "Получить все", "Найти", "Назад"));
    }

    /**
     * Возвращает кнопки меню текущего контакта
     */
    public ReplyKeyboardMarkup currentContactMenu() {
        return createKeyboard(List.of("Информация", "Изменить", "Блокировать", "Удалить", "Назад"));
    }

    /**
     * Возвращает кнопки меню добавления контакта
     */
    public ReplyKeyboardMarkup addContactMenu() {
        return createKeyboard(List.of("Номер", "Возраст", "Пол", "Сохранить контакт", "Назад"));
    }

    /**
     * Возвращает кнопки меню редактирования контакта
     */
    public ReplyKeyboardMarkup editContactMenu() {
        return createKeyboard(List.of("Имя", "Номер", "Возраст", "Пол", "Изменить контакт", "Назад"));
    }

    /**
     * Возвращает кнопки меню получения всех контактов
     */
    public ReplyKeyboardMarkup getAllContactsMenu() {
        return createKeyboard(List.of("Получить сразу", "Добавить фильтр", "Добавить сортировку", "Назад"));
    }
}

package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, создающий клавиатуры ReplyKeyboardMarkup
 */
public class KeyboardCreator {
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

    public ReplyKeyboardMarkup mainMenu(){
        return createKeyboard(List.of("Контакты"));
    }

    public ReplyKeyboardMarkup contactsMenu(){
        return createKeyboard(List.of("Добавить", "Получить все", "Найти", "Назад"));
    }

    public ReplyKeyboardMarkup addContactMenu(){
        return createKeyboard(List.of("Номер", "Возраст", "Пол", "Сохранить контакт", "Назад"));
    }
}

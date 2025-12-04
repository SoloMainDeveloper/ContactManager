package org.example.keyboardcreator;

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
            if (i + 1 < buttonsText.size()) {
                currentRow.add(buttonsText.get(i + 1));
            }
            keyboard.add(currentRow);
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}

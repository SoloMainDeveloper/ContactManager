package org.example.keyboardcreator;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс, создающий клавиатуры InlineKeyboardMarkup
 */
public class InlineKeyboardCreator {
    /**
     * Создаёт InlineKeyboardMarkup на основе переданного списка текста buttonText. Также
     * добавляет в callBackData название операции operationName.
     */
    public InlineKeyboardMarkup createKeyboard(List<String> buttonText, String operationName) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String label : buttonText) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(label);
            button.setCallbackData(operationName + "_" + label);
            rows.add(Collections.singletonList(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}

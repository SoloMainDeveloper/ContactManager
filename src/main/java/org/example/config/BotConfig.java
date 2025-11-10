package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс для взаимодействия с конфигурационными файлами
 */
@Component
public class BotConfig {
    /**
     * Токен от телеграм-бота
     */
    @Value("${api.telegram.bot.token}")
    private String botToken;

    /**
     * Имя телеграм-бота
     */
    @Value("${api.telegram.bot.username}")
    private String botUsername;

    /**
     * Возвращает токен от телеграм-бота
     */
    public String getBotToken() {
        return botToken;
    }

    /**
     * Возвращает имя телеграм-бота
     */
    public String getBotUsername() {
        return botUsername;
    }
}

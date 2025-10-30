package org.example;

import org.example.state.State;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Телеграм-бот менеджера контактов
 */
public class ContactManagerBot extends TelegramLongPollingBot {
    /**
     * API-token для бота
     */
    private final String token;
    /**
     * Обработчик сообщений
     */
    private final MessageHandler messageHandler = new MessageHandler();
    private final HashMap<Long, State> states = new LinkedHashMap<>();

    /**
     * Конструктор. Инициализируем API-token
     */
    public ContactManagerBot(){
        this.token = readTokenFromConfig();
    }

    /**
     * Возвращает название телеграм-бота
     */
    @Override
    public String getBotUsername() {
        return "YourContactManagerBot";
    }

    /**
     * Возвращает API-token для телеграм-бота
     */
    @Override
    public String getBotToken() {
        return token;
    }

    /**
     * Ловит сообщения пользователей и пишет им ответ в чате телеграм-бота.
     * Если у пользователя не было состояния диалога - state, создаёт его.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            if(!states.containsKey(chatId)){
                states.put(chatId, new State());
            }
            SendMessage response = messageHandler.handleMessage(states.get(chatId), message);
            sendMessage(response);
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Message message = (Message) callbackQuery.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            SendMessage response = messageHandler.handleCallbackData(callbackData, states.get(chatId), message);
            sendMessage(response);
        }
    }

    /**
     * Отправляет сообщение пользователю
     */
    private void sendMessage(SendMessage response) {
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Чтение из config.properties API-токена для бота
     */
    private String readTokenFromConfig() {
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
            return properties.getProperty("api.telegram.bot.token");
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать config.properties", e);
        }
    }
}

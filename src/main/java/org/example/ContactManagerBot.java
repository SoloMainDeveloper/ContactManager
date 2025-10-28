package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.Properties;

/**
 * Телеграм-бот менеджера контактов
 */
public class ContactManagerBot extends TelegramLongPollingBot {
    private final String token;

    /**
     * Конструктор. Инициализируем API-token
     */
    public ContactManagerBot(){
        this.token = readTokenFromConfig();
    }

    /**
     * Возращает название телеграм-бота
     */
    @Override
    public String getBotUsername() {
        return "YourContactManagerBot";
    }

    /**
     * Возращает API-token для телеграм-бота
     */
    @Override
    public String getBotToken() {
        return token;
    }

    /**
     * Ловит сообщения пользователей и пишет им ответ в чате телеграм-бота
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            SendMessage response = getResponse(message);
            sendMessage(response);
        }
    }

    /**
     * Создаёт ответ, содержащий текст и/или кнопки, а также id получателя ответа
     */
    private SendMessage getResponse(Message message){
        long chatId = message.getChatId();
        String messageText = message.getText();

        SendMessage response = new SendMessage();
        switch (messageText) {
            case "/start":
                response.setText("Привет! Я бот для управления контактами.");
                break;
            default:
                response.setText("Я не понимаю эту команду.");
                break;
        }
        response.setChatId(String.valueOf(chatId));
        return response;
    }

    /**
     * Возвращает кнопки для ответа
     */
    private ReplyKeyboardMarkup createKeyboard() {
        //TODO
        return null;
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

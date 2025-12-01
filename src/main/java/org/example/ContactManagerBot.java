package org.example;

import org.example.config.BotConfig;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Телеграм-бот менеджера контактов
 */
@Component
public class ContactManagerBot extends TelegramLongPollingBot {
    /**
     * Имя телеграм-бота
     */
    private final String botUsername;

    /**
     * Обработчик сообщений
     */
    private final MessageHandler messageHandler;

    /**
     * Конструктор. Инициализируем API-token, инжектим MessageHandler
     */
    @Autowired
    public ContactManagerBot(BotConfig config, MessageHandler messageHandler){
        super(config.getBotToken());
        this.botUsername = config.getBotUsername();
        this.messageHandler = messageHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            BotResponse response = messageHandler.handleMessage(message.getChatId(), message.getText());
            SendMessage sendMessage = adaptBotResponseToTelegram(response);
            sendMessage(message.getChatId(), sendMessage);
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Message message = (Message) callbackQuery.getMessage();
            BotResponse response = messageHandler.handleInlineButtonActivated(
                    message.getChatId(), callbackData);
            SendMessage sendMessage = adaptBotResponseToTelegram(response);
            sendMessage(message.getChatId(), sendMessage);
        }
    }

    /**
     * Создаёт специализированный под телеграм SendMessage из botResponse
     */
    private SendMessage adaptBotResponseToTelegram(BotResponse response) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(response.getText());
        if(response.getKeyboardText() != null){
            sendMessage.setReplyMarkup(new ReplyKeyboardCreator()
                    .createKeyboard(response.getKeyboardText()));
        }
        InlineKeyboardText inlineKeyboardText = response.getInlineKeyboardText();
        if(inlineKeyboardText != null && inlineKeyboardText.isNeeded()) {
            sendMessage.setReplyMarkup(new InlineKeyboardCreator()
                    .createKeyboard(inlineKeyboardText));
        }
        return sendMessage;
    }

    /**
     * Отправляет сообщение пользователю
     */
    private void sendMessage(Long chatId, SendMessage response) {
        try {
            response.setChatId(chatId);
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("Сообщение не было отправлено: " + e);
        }
    }
}

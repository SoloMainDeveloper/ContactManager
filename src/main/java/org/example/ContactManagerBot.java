package org.example;

import org.example.config.BotConfig;
import org.example.entity.AppDocument;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.utils.telegram.TelegramDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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
    private MessageHandler messageHandler;

    /**
     * Читает документы, отправленные через телеграм
     */
    private final TelegramDocumentReader telegramDocumentReader;

    /**
     * Конструктор. Инициализируем API-token
     */
    @Autowired
    public ContactManagerBot(BotConfig config, MessageHandler messageHandler,
                             TelegramDocumentReader telegramDocumentReader){
        super(config.getBotToken());
        this.botUsername = config.getBotUsername();
        this.messageHandler = messageHandler;
        this.telegramDocumentReader = telegramDocumentReader;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    /**
     * Обработать входящее сообщение
     */
    public void handleMessage(Message message) {
        Long chatId = message.getChatId();
        if(message.hasDocument()) {
            Document document = message.getDocument();
            String content = telegramDocumentReader.read(document.getFileId());
            BotResponse response = messageHandler.handleMessageWithDocument(
                    chatId, document.getFileName(), content);
            SendMessage sendMessage = adaptBotResponseToTelegram(response);
            sendMessage(chatId, sendMessage);
        } else if(message.hasText()) {
            BotResponse response = messageHandler
                    .handleMessage(chatId, message.getText());
            SendMessage sendMessage = adaptBotResponseToTelegram(response);
            sendMessage(chatId, sendMessage);

            if(response.hasDocument()) {
                sendDocument(chatId, response.getDocument());
            }
        }
    }

    /**
     * Обработать нажатие inline-кнопки
     */
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        Message message = (Message) callbackQuery.getMessage();
        BotResponse response = messageHandler.handleInlineButtonActivated(
                message.getChatId(), callbackData);
        SendMessage sendMessage = adaptBotResponseToTelegram(response);
        sendMessage(message.getChatId(), sendMessage);
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

    /**
     * Отправляет документ пользователю
     */
    private void sendDocument(Long chatId, AppDocument appDocument) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                    appDocument.content().getBytes(StandardCharsets.UTF_8)
            );
            InputFile inputFile = new InputFile(inputStream, appDocument.fileName());

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId.toString());
            sendDocument.setDocument(inputFile);

            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("Документ не был отправлен: " + e);
        }
    }
}

package org.example.data;

import org.example.MessageHandler;
import org.example.contact.FakeContactRepository;
import org.example.response.AppDocument;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.operations.MainMenuHandler;
import org.example.operations.data.DataMenuHandler;
import org.example.operations.data.ExportHandler;
import org.example.operations.data.ImportHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.export.ExportService;
import org.example.service.importation.ImportService;
import org.example.service.StateService;
import org.example.service.export.exporters.ExporterCSV;
import org.example.service.export.exporters.ExporterJSON;
import org.example.service.export.exporters.ExporterTXT;
import org.example.service.importation.importers.ImporterCSV;
import org.example.service.importation.importers.ImporterJSON;
import org.example.service.importation.importers.ImporterTXT;
import org.example.utils.ContactFilter;
import org.example.utils.ContactOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Тестируем обработчики сообщений, связанные с обработкой файлов
 */
public class DataMessageHandlerTest {
    /**
     * Фейковый контакт-репозиторий для тестов
     */
    private FakeContactRepository fakeRepository;

    /**
     * Контакт-сервис для тестов
     */
    private ContactService contactService;

    /**
     * Обработчик сообщений
     */
    private MessageHandler handler;

    /**
     * ChatId пользователя
     */
    public final Long chatId = 123245663L;

    /**
     * Инициализируем фейковый репозиторий, чтобы не работать напрямую с БД.
     * Инициализируем сервис контактов для более детального тестирования внутренностей.
     * Инициализируем MessageHandler, ExportService и ImportService.
     */
    @BeforeEach
    public void setup() {
        fakeRepository = new FakeContactRepository();
        contactService = new ContactService(fakeRepository);
        StateService stateService = new StateService();
        ImportService importService = new ImportService(List.of(
                new ImporterCSV(), new ImporterTXT(), new ImporterJSON()));
        ExportService exportService = new ExportService(List.of(
                new ExporterCSV(), new ExporterTXT(), new ExporterJSON()));
        handler = new MessageHandler(
                List.of(new MainMenuHandler(stateService),
                        new DataMenuHandler(stateService, exportService),
                        new ExportHandler(stateService, exportService, contactService),
                        new ImportHandler(stateService, importService, contactService)),
                stateService);
    }

    /**
     * Протестировать импорт контактов из текстового формата
     */
    @Test
    public void importContactsFromTxtTest() {
        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Импорт контактов");
        BotResponse response = handler
                .handleMessageWithDocument(chatId, "test.txt", """
                Имя контакта: Владимир
                Номер телефона: 89034252365
                Возраст: 32
                Пол: Мужской
                Блокировка: Не заблокирован
                """);

        Assertions.assertEquals(
                "Импорт контактов закончен, добавилось 1 из 1",
                response.getText()
        );
        List<Contact> contacts = contactService.findContactsByChatId(
                chatId, new ContactFilter(), new ContactOrder());
        Contact contact = contacts.getFirst();
        Assertions.assertEquals(1, contacts.size());
        Assertions.assertEquals("Владимир", contact.getName());
        Assertions.assertEquals("89034252365", contact.getPhoneNumber());
        Assertions.assertEquals(32, contact.getAge());
        Assertions.assertEquals(Gender.MALE, contact.getGender());
        Assertions.assertFalse(contact.isBlocked());
    }

    /**
     * Протестировать импорт контактов из csv формата
     */
    @Test
    public void importContactsFromCsvTest()  {
        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Импорт контактов");
        BotResponse response = handler
                .handleMessageWithDocument(chatId, "test.csv", """
                name,phone,age,gender,isBlocked
                Владимир,89034252365,32,Мужской,Не заблокирован
                Юлия,95436475,34,Женский,Заблокирован
                """);

        Assertions.assertEquals(
                "Импорт контактов закончен, добавилось 2 из 2",
                response.getText()
        );
        List<Contact> contacts = contactService.findContactsByChatId(
                chatId, new ContactFilter(), new ContactOrder());
        Assertions.assertEquals(2, contacts.size());
        Contact contactVladimir =
                contactService.findContactByName(chatId, "Владимир").orElseThrow();
        Assertions.assertEquals("89034252365", contactVladimir.getPhoneNumber());
        Assertions.assertEquals(32, contactVladimir.getAge());
        Assertions.assertEquals(Gender.MALE, contactVladimir.getGender());
        Assertions.assertFalse(contactVladimir.isBlocked());

        Contact contactJulia =
                contactService.findContactByName(chatId, "Юлия").orElseThrow();
        Assertions.assertEquals("95436475", contactJulia.getPhoneNumber());
        Assertions.assertEquals(34, contactJulia.getAge());
        Assertions.assertEquals(Gender.FEMALE, contactJulia.getGender());
        Assertions.assertTrue(contactJulia.isBlocked());
    }

    /**
     * Протестировать импорт контактов из json формата
     */
    @Test
    public void importContactsFromJsonTest() {
        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Импорт контактов");
        BotResponse response = handler.handleMessageWithDocument(chatId, "test.json",
                "[{\"phoneNumber\":\"+78943230685\",\"gender\":\"Не выбрано\",\"name\":" +
                        "\"Дарья\",\"isBlocked\":\"Заблокирован\",\"age\":\"13\"}]");

        Assertions.assertEquals(
                "Импорт контактов закончен, добавилось 1 из 1",
                response.getText()
        );
        List<Contact> contacts = contactService.findContactsByChatId(
                chatId, new ContactFilter(), new ContactOrder());
        Assertions.assertEquals(1, contacts.size());
        Contact contact = contacts.getFirst();
        Assertions.assertEquals("Дарья", contact.getName());
        Assertions.assertEquals("+78943230685", contact.getPhoneNumber());
        Assertions.assertEquals(13, contact.getAge());
        Assertions.assertEquals(Gender.NOT_SPECIFIED, contact.getGender());
        Assertions.assertTrue(contact.isBlocked());
    }

    /**
     * Протестировать импорт файла с неподдерживаемым форматом
     */
    @Test
    public void importContactsToUnsupportedFormatTest() {
        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Импорт контактов");
        BotResponse response = handler
                .handleMessageWithDocument(chatId, "test.pdf", """
                name,phone,age,gender,isBlocked
                Владимир,89034252365,32,Мужской,Не заблокирован
                Юлия,95436475,34,Женский,Заблокирован
                """);

        Assertions.assertEquals("Произошла ошибка при импорте: Данный формат файла " +
                        "не поддерживается. Используйте txt/csv/json",
                response.getText());
        List<Contact> contacts = contactService.findContactsByChatId(
                chatId, new ContactFilter(), new ContactOrder());
        Assertions.assertEquals(0, contacts.size());
    }

    /**
     * Протестировать импорт контактов с их корректным представлением в файле
     */
    @Test
    public void importContactsWithIncorrectDataTest() {
        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Импорт контактов");
        BotResponse response = handler
                .handleMessageWithDocument(chatId, "test.csv", """
                name,phone,age,gender,isBlocked
                Владимир,89034252365,Мой возраст,Мужской,Не заблокирован
                Юлия,95436475,34,Женский,Заблокирован
                """);

        Assertions.assertEquals("Произошла ошибка при импорте: Некорректные данные",
                response.getText());
        List<Contact> contacts = contactService.findContactsByChatId(
                chatId, new ContactFilter(), new ContactOrder());
        Assertions.assertEquals(0, contacts.size());
    }

    /**
     * Протестировать экспорт контактов в текстовый формат
     */
    @Test
    public void exportContactsToTxtTest() {
        fakeRepository.add(
                new Contact(chatId, "Дарья", "+78943230685", 13, Gender.FEMALE, true));

        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Экспорт контактов");
        handler.handleMessage(chatId, "txt");
        BotResponse response = handler.handleMessage(chatId, "test");

        Assertions.assertEquals(
                "Контакты были успешно экспортированы в файл",
                response.getText()
        );
        AppDocument document = response.getDocument();
        Assertions.assertEquals("test.txt", document.fileName());
        Assertions.assertEquals("""
                            Имя контакта: Дарья
                            Номер телефона: +78943230685
                            Возраст: 13
                            Пол: Женский
                            Блокировка: Заблокирован
                            
                            """, document.content());
    }

    /**
     * Протестировать экспорт контактов в csv формат
     */
    @Test
    public void exportContactsToCsvTest() {
        fakeRepository.add(
                new Contact(chatId, "Дарья", "+78943230685", 13, Gender.FEMALE, true));

        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Экспорт контактов");
        handler.handleMessage(chatId, "csv");
        BotResponse response = handler.handleMessage(chatId, "test");

        Assertions.assertEquals(
                "Контакты были успешно экспортированы в файл",
                response.getText()
        );
        AppDocument document = response.getDocument();
        Assertions.assertEquals("test.csv", document.fileName());
        Assertions.assertEquals("""
                            \uFEFFname,phone,age,gender,isBlocked
                            Дарья,+78943230685,13,Женский,Заблокирован
                            """, document.content());
    }

    /**
     * Протестировать экспорт контактов в json формат
     */
    @Test
    public void exportContactsToJsonTest() {
        fakeRepository.add(
                new Contact(chatId, "Дарья", "+78943230685", 13, Gender.FEMALE, true));

        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Экспорт контактов");
        handler.handleMessage(chatId, "json");
        BotResponse response = handler.handleMessage(chatId, "test");

        Assertions.assertEquals(
                "Контакты были успешно экспортированы в файл",
                response.getText()
        );
        AppDocument document = response.getDocument();
        Assertions.assertEquals("test.json", document.fileName());
        Assertions.assertEquals("[{\"phoneNumber\":\"+78943230685\"," +
                        "\"gender\":\"Женский\",\"name\":\"Дарья\",\"" +
                        "isBlocked\":\"Заблокирован\",\"age\":\"13\"}]",
                document.content());
    }

    /**
     * Протестировать экспорт файла с неподдерживаемым форматом
     */
    @Test
    public void exportContactsToUnsupportedFormatTest() {
        fakeRepository.add(
                new Contact(chatId, "Дарья", "+78943230685", 13, Gender.FEMALE, true));

        handler.handleMessage(chatId, "Данные");
        handler.handleMessage(chatId, "Экспорт контактов");
        handler.handleMessage(chatId, "pdf");
        BotResponse response = handler.handleMessage(chatId, "test");

        Assertions.assertEquals("Произошла ошибка при экспорте: Данный формат файла " +
                        "не поддерживается. Используйте txt/csv/json",
                response.getText());
    }
}

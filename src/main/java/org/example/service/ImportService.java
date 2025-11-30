package org.example.service;

import org.example.entity.Contact;
import org.example.exceptions.ImportException;
import org.example.exceptions.UnsupportedFormatException;
import org.example.utils.importers.Importer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис импорта контактов
 */
@Service
public class ImportService {
    /**
     * Импортёры файлов различных форматов
     */
    private final Map<String, Importer> importers;

    /**
     * Конструктор
     */
    @Autowired
    public ImportService(List<Importer> importers) {
        this.importers = importers.stream().collect(
                Collectors.toMap(
                        Importer::getSupportableFormat,
                        Function.identity()
                ));
    }

    /**
     * Импортирует контакты из файла
     * @return список контактов из файла
     * @throws UnsupportedFormatException ошибка импорта
     */
    public List<Contact> importContacts() throws UnsupportedFormatException {
        String key = "Нужно откуда-то взять";
        if(!importers.containsKey(key)){
            throw new UnsupportedFormatException("Данный формат файла не " +
                    "поддерживается");
        }
        Importer importer = importers.get(key);
        try {
            return importer.importContacts();
        } catch (ImportException exp) {
            //TODO
        }
        return null; //TODO удалить это
    }
}

package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.exceptions.ImportException;

import java.util.List;

/**
 * Импортёр контактов
 */
public interface Importer {
    /**
     * Импортирует контакты из файла
     * @param content содержимое файла
     */
    List<Contact> importContacts(String content) throws ImportException;

    /**
     * Возвращает поддерживаемый формат импорта. Формат задаётся в нижнем регистре
     */
    String getSupportedFormat();
}

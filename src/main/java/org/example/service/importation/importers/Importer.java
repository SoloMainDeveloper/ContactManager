package org.example.service.importation.importers;

import org.example.entity.Contact;
import org.example.exceptions.IncorrectImportDataException;

import java.util.List;

/**
 * Импортёр контактов
 */
public interface Importer {
    /**
     * Импортирует контакты из файла
     *
     * @param content содержимое файла
     * @throws IncorrectImportDataException если содержимое
     * не соответствует виду запрашиваемого dto
     */
    List<Contact> importContacts(String content) throws IncorrectImportDataException;

    /**
     * Возвращает поддерживаемый формат импорта. Формат задаётся в нижнем регистре
     */
    String getSupportedFormat();
}

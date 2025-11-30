package org.example.utils.exporters;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;

import java.util.List;

/**
 * Экспортёр контактов
 */
public interface Exporter {
    /**
     * Экспортирует контакты в файл
     * @return экспортируемый документ
     * @throws ExportException ошибка экспорта
     */
    AppDocument exportContacts(String fileName, List<Contact> contacts)
            throws ExportException;

    /**
     * Возвращает поддерживаемый формат экспорта. Формат задаётся в нижнем регистре
     */
    String getSupportedFormat();
}

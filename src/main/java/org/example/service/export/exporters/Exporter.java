package org.example.service.export.exporters;

import org.example.response.AppDocument;
import org.example.entity.Contact;

import java.util.List;

/**
 * Экспортёр контактов
 */
public interface Exporter {
    /**
     * Экспортирует контакты в {@link AppDocument}
     *
     * @return экспортируемый документ
     */
    AppDocument exportContacts(String fileName, List<Contact> contacts);

    /**
     * Возвращает поддерживаемый формат экспорта. Формат задаётся в нижнем регистре
     */
    String getSupportedFormat();
}

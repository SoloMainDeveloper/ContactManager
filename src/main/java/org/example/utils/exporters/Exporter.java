package org.example.utils.exporters;

/**
 * Экспортёр контактов
 */
public interface Exporter {
    /**
     * Экспортирует контакты в файл
     */
    void exportContacts();

    /**
     * Возвращает поддерживаемый формат экспорта. Формат задаётся в нижнем регистре
     */
    String getSupportableFormat();
}

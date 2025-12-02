package org.example.service;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;
import org.example.exceptions.UnsupportedFormatException;
import org.example.utils.exporters.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис экспорта контактов
 */
@Service
public class ExportService {
    /**
     * Экспортёры файлов различных форматов
     */
    private final Map<String, Exporter> exporters;

    /**
     * Конструктор
     */
    @Autowired
    public ExportService(List<Exporter> exporters) {
        this.exporters = exporters.stream().collect(
                Collectors.toMap(
                        Exporter::getSupportedFormat,
                        Function.identity()
                ));
    }

    /**
     * Экспортирует контакты в файл
     */
    public AppDocument exportContacts(
            String fileName, String format, List<Contact> contacts)
            throws UnsupportedFormatException {
        if(!exporters.containsKey(format)) {
            String supportedFormats = String.join("/", getSupportedFormats());
            throw new UnsupportedFormatException("Данный формат файла не " +
                    "поддерживается. Используйте " + supportedFormats);
        }
        Exporter exporter = exporters.get(format);
        try {
            return exporter.exportContacts(fileName, contacts);
        } catch (ExportException e) {
            throw new UnsupportedFormatException(e.getMessage());
        }
    }

    /**
     * Возвращает список поддерживаемых форматов экспорта
     */
    public Set<String> getSupportedFormats() {
        return exporters.keySet();
    }
}

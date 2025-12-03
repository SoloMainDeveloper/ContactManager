package org.example.service;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;
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
     * @throws ExportException если формат не поддерживается
     */
    public AppDocument exportContacts(
            String fileName, String format, List<Contact> contacts)
            throws ExportException {
        if(!exporters.containsKey(format)) {
            String supportedFormats = String.join("/", getSupportedFormats());
            throw new ExportException("Данный формат файла не " +
                    "поддерживается. Используйте " + supportedFormats);
        }
        Exporter exporter = exporters.get(format);
        return exporter.exportContacts(fileName, contacts);
    }

    /**
     * Возвращает список поддерживаемых форматов экспорта
     */
    public Set<String> getSupportedFormats() {
        return exporters.keySet();
    }
}

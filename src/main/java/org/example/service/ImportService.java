package org.example.service;

import org.apache.commons.io.FilenameUtils;
import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ImportException;
import org.example.exceptions.IncorrectImportDataException;
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
                        Importer::getSupportedFormat,
                        Function.identity()
                ));
    }

    /**
     * Импортирует контакты из файла
     * @param document импортируемый документ
     * @return список контактов из файла
     * @throws ImportException если формат не
     * поддерживается или некорректное содержимое файла
     */
    public List<Contact> importContacts(AppDocument document)
            throws ImportException {
        String format = FilenameUtils.getExtension(document.fileName());
        if(!importers.containsKey(format)) {
            String supportedFormats = String.join("/", importers.keySet()) ;
            throw new ImportException("Данный формат файла не " +
                    "поддерживается. Используйте " + supportedFormats);
        }
        Importer importer = importers.get(format);
        try {
            return importer.importContacts(document.content());
        } catch (IncorrectImportDataException e) {
            throw new ImportException(e);
        }
    }
}

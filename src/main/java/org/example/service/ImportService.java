package org.example.service;

import org.apache.commons.io.FilenameUtils;
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
                        Importer::getSupportedFormat,
                        Function.identity()
                ));
    }

    /**
     * Импортирует контакты из файла
     * @param content содержимое импортируемого файла
     * @return список контактов из файла
     * @throws UnsupportedFormatException неподдерживаемый формат импорта
     */
    public List<Contact> importContacts(String fileName, String content)
            throws UnsupportedFormatException {
        String format = FilenameUtils.getExtension(fileName);
        if(!importers.containsKey(format)) {
            String supportedFormats = String.join("/", importers.keySet()) ;
            throw new UnsupportedFormatException("Данный формат файла не " +
                    "поддерживается. Используйте " + supportedFormats);
        }
        Importer importer = importers.get(format);
        try {
            return importer.importContacts(content);
        } catch (ImportException ex) {
            throw new UnsupportedFormatException(ex.getMessage());
        }
    }
}

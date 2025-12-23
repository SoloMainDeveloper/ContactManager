package org.example.service.importation;

import org.apache.commons.io.FilenameUtils;
import org.example.response.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ImportException;
import org.example.exceptions.IncorrectImportDataException;
import org.example.service.importation.importers.Importer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public ImportService(List<Importer> importers) {
        this.importers = importers.stream().collect(
            Collectors.toMap(
                Importer::getSupportedFormat,
                Function.identity()
            ));
    }

    /**
     * Импортирует контакты из файла
     *
     * @param document импортируемый документ
     * @return список контактов из файла
     * @throws ImportException если формат не
     *                         поддерживается или некорректное содержимое файла
     */
    public List<Contact> importContacts(AppDocument document)
        throws ImportException {
        String format = FilenameUtils.getExtension(document.fileName());
        if (!importers.containsKey(format)) {
            String supportedFormats = String.join("/", getSupportedFormats());
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

    /**
     * Возвращает список поддерживаемых форматов импорта
     */
    public Set<String> getSupportedFormats() {
        return importers.keySet();
    }
}

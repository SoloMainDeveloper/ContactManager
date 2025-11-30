package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.exceptions.ImportException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Импортёр контактов из CSV
 */
@Component
public class ImporterCSV implements Importer {
    @Override
    public List<Contact> importContacts(String content) throws ImportException {
        return List.of();
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}

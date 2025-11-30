package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.exceptions.ImportException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Импортёр контактов из TXT
 */
@Component
public class ImporterTXT implements Importer {
    @Override
    public List<Contact> importContacts() throws ImportException {
        return List.of();
    }

    @Override
    public String getSupportableFormat() {
        return "txt";
    }
}

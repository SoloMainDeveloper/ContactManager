package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ImportException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Импортёр контактов из CSV
 */
@Component
public class ImporterCSV implements Importer {
    @Override
    public List<Contact> importContacts(String content) throws ImportException {
        try {
            List<Contact> contacts = new ArrayList<>();
            List<String> contactLines = List.of(content.split("\n"));
            for(String line : contactLines) {
                List<String> splitLine = List.of(line.split(";"));
                contacts.add(new Contact(
                        Long.valueOf(splitLine.get(0)),
                        splitLine.get(1),
                        splitLine.get(2),
                        Integer.parseInt(splitLine.get(3)),
                        Gender.valueOf(splitLine.get(4)),
                        Boolean.valueOf(splitLine.get(5))
                ));
            }
            return contacts;
        } catch (Exception ex) {
            throw new ImportException(ex.getMessage());
        }
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}

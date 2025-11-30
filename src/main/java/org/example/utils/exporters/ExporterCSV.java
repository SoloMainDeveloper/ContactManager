package org.example.utils.exporters;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Экспортёр контактов в CSV
 */
@Component
public class ExporterCSV implements Exporter {
    @Override
    public AppDocument exportContacts(String fileName, List<Contact> contacts)
            throws ExportException {
        String content = "";
        return new AppDocument(fileName, content);
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}

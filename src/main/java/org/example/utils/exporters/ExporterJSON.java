package org.example.utils.exporters;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;
import org.example.utils.converter.ContactConverter;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Экспортёр контактов в JSON
 */
@Component
public class ExporterJSON implements Exporter {
    public AppDocument exportContacts(String fileName, List<Contact> contacts)
            throws ExportException {
        try {
            JSONArray content = new JSONArray();
            ContactConverter converter = new ContactConverter();
            for (Contact contact : contacts) {
                content.put(converter.contactToJsonFormat(contact));
            }
            String fileNameWithFormat = String.format("%s.%s",
                    fileName, getSupportedFormat());
            return new AppDocument(fileNameWithFormat, content.toString());
        } catch (ExportException e) {
            throw new ExportException("Произошла ошибка экспорта в формате "
                    + getSupportedFormat());
        }
    }

    @Override
    public String getSupportedFormat() {
        return "json";
    }
}

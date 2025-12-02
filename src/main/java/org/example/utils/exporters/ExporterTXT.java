package org.example.utils.exporters;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ExportException;
import org.example.utils.converter.ContactConverter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Экспортёр контактов в TXT
 */
@Component
public class ExporterTXT implements Exporter {
    public AppDocument exportContacts(String fileName, List<Contact> contacts)
            throws ExportException {
        try {
            StringBuilder content = new StringBuilder();
            ContactConverter converter = new ContactConverter();
            for (Contact contact : contacts) {
                content.append(converter.contactToTxtFormat(contact)).append("\n");
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
        return "txt";
    }
}

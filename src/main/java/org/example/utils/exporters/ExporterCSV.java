package org.example.utils.exporters;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.entity.ContactDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Экспортёр контактов в CSV
 */
@Component
public class ExporterCSV implements Exporter {
    @Override
    public AppDocument exportContacts(String fileName, List<Contact> contacts) {
        StringBuilder content = new StringBuilder();
        content.append("\uFEFF");
        content.append("name,phone,age,gender,isBlocked\n");
        for(Contact contact : contacts) {
            content.append(convertToCsvFormat(contact));
        }
        String fileNameWithFormat = String.format("%s.%s",
                fileName, getSupportedFormat());
        return new AppDocument(fileNameWithFormat, content.toString());
    }

    /**
     * Преобразовать контакт в csv формат
     */
    private String convertToCsvFormat(Contact contact) {
        ContactDto dto = new ContactDto(contact);

        return String.format("%s,%s,%s,%s,%s\n", dto.getName(), dto.getPhoneNumber(),
                dto.getAge(), dto.getGender(), dto.getIsBlocked());
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}

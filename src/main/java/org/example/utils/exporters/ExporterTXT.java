package org.example.utils.exporters;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.entity.ContactDto;
import org.example.exceptions.ExportException;
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
            for (Contact contact : contacts) {
                content.append(convertToTxtFormat(contact));
            }
            String fileNameWithFormat = String.format("%s.%s",
                    fileName, getSupportedFormat());
            return new AppDocument(fileNameWithFormat, content.toString());
        } catch (ExportException e) {
            throw new ExportException("Произошла ошибка экспорта в формате "
                    + getSupportedFormat());
        }
    }

    /**
     * Преобразовать контакт в текстовый формат
     */
    private String convertToTxtFormat(Contact contact) {
        ContactDto dto = new ContactDto(contact);

        return String.format("""
                    Имя контакта: %s
                    Номер телефона: %s
                    Возраст: %s
                    Пол: %s
                    Блокировка: %s
                    
                    """, dto.getName(), dto.getPhoneNumber(),
                dto.getAge(), dto.getGender(), dto.getIsBlocked());
    }

    @Override
    public String getSupportedFormat() {
        return "txt";
    }
}

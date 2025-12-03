package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ImportException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            for(int i = 1; i < contactLines.size(); i++) {
                contacts.add(convertFromCsvFormat(contactLines.get(i)));
            }
            return contacts;
        } catch (RuntimeException ex) {
            throw new ImportException("Произошла ошибка при импорте файла в формате "
                    + getSupportedFormat());
        }
    }

    /**
     * Преобразовать csv-строку в контакт
     */
    private Contact convertFromCsvFormat(String csvContact) throws RuntimeException {
        try {
            List<String> contactFields = List.of(csvContact.split(","));

            String phoneNumber = contactFields.get(1).equals("Не указан")
                    ? ""
                    : contactFields.get(1);
            int age = contactFields.get(2).equals("Не указан")
                    ? -1
                    : Integer.parseInt(contactFields.get(2));

            Contact contact = new Contact();
            contact.setName(contactFields.get(0));
            contact.setPhoneNumber(phoneNumber);
            contact.setAge(age);
            contact.setGender(Gender.fromDisplayName(
                    contactFields.get(3)));
            contact.setBlocked(Objects.equals(
                    contactFields.get(4), "Заблокирован"));
            return contact;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}

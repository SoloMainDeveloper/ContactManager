package org.example.service.importation.importers;

import org.example.constants.ReplyConstants;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.IncorrectImportDataException;
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
    public List<Contact> importContacts(String content)
            throws IncorrectImportDataException {
        try {
            List<Contact> contacts = new ArrayList<>();
            List<String> contactLines = List.of(content.split("\n"));
            for(int i = 1; i < contactLines.size(); i++) {
                contacts.add(convertFromCsvFormat(contactLines.get(i)));
            }
            return contacts;
        } catch (Exception ex) {
            throw new IncorrectImportDataException(ReplyConstants.INCORRECT_DATA);
        }
    }

    /**
     * Преобразовать csv-строку в контакт
     */
    private Contact convertFromCsvFormat(String csvContact) {
        List<String> contactFields = List.of(csvContact.split(","));

        String phoneNumber = contactFields.get(1).equals(ReplyConstants.NOT_SPECIFIED)
                ? ""
                : contactFields.get(1);
        int age = contactFields.get(2).equals(ReplyConstants.NOT_SPECIFIED)
                ? -1
                : Integer.parseInt(contactFields.get(2));

        Contact contact = new Contact();
        contact.setName(contactFields.get(0));
        contact.setPhoneNumber(phoneNumber);
        contact.setAge(age);
        contact.setGender(Gender.fromDisplayName(
                contactFields.get(3)));
        contact.setBlocked(Objects.equals(
                contactFields.get(4), ReplyConstants.BLOCKED));
        return contact;
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}

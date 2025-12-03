package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ImportException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Импортёр контактов из TXT
 */
@Component
public class ImporterTXT implements Importer {
    @Override
    public List<Contact> importContacts(String content) throws ImportException {
        try {
            List<Contact> contacts = new ArrayList<>();
            String[] blocks = content.split("\\n\\s*\\n+");
            List<String> txtContacts = Arrays.stream(blocks)
                    .filter(block -> !block.trim().isEmpty())
                    .toList();
            for(String contact : txtContacts) {
                contacts.add(convertFromTxtFormat(contact));
            }
            return contacts;
        } catch (RuntimeException e) {
            throw new ImportException("Произошла ошибка при импорте файла в формате "
                    + getSupportedFormat());
        }
    }

    /**
     * Преобразовать текст в контакт
     */
    public Contact convertFromTxtFormat(String txtContact) throws RuntimeException {
        try {
            List<String> contactFields = List.of(txtContact.split("\n"));

            List<String> nameLine = List.of(contactFields.get(0).split(":"));
            List<String> phoneNumberLine = List.of(contactFields.get(1).split(":"));
            List<String> ageLine = List.of(contactFields.get(2).split(":"));
            List<String> genderLine = List.of(contactFields.get(3).split(":"));
            List<String> isBlockedLine = List.of(contactFields.get(4).split(":"));

            String phoneNumber = phoneNumberLine.getLast().trim().equals("Не указан")
                    ? ""
                    : phoneNumberLine.getLast().trim();
            int age = ageLine.getLast().trim().equals("Не указан")
                    ? -1
                    : Integer.parseInt(ageLine.getLast().trim());

            Contact contact = new Contact();
            contact.setName(nameLine.getLast().trim());
            contact.setPhoneNumber(phoneNumber);
            contact.setAge(age);
            contact.setGender(Gender.fromDisplayName(
                    genderLine.getLast().trim()));
            contact.setBlocked(Objects.equals(
                    isBlockedLine.getLast().trim(), "Заблокирован"));
            return contact;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getSupportedFormat() {
        return "txt";
    }
}

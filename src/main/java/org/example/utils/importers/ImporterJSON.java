package org.example.utils.importers;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.IncorrectImportDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Импортёр контактов из JSON
 */
@Component
public class ImporterJSON implements Importer {
    @Override
    public List<Contact> importContacts(String content)
            throws IncorrectImportDataException {
        try {
            JSONArray jsonArray = new JSONArray(content);
            List<Contact> contacts = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonContact = jsonArray.getJSONObject(i);
                Contact contact = convertFromJsonFormat(jsonContact);
                contacts.add(contact);
            }
            return contacts;
        } catch (Exception e) {
            throw new IncorrectImportDataException("Некорректные данные");
        }
    }

    /**
     * Преобразовать json-объект в контакт
     */
    public Contact convertFromJsonFormat(JSONObject jsonContact) {
        String phoneNumber = jsonContact.getString("phoneNumber").equals("Не указан")
                ? ""
                : jsonContact.getString("phoneNumber");
        int age = jsonContact.getString("age").equals("Не указан")
                ? -1
                : Integer.parseInt(jsonContact.getString("age"));

        Contact contact = new Contact();
        contact.setName(jsonContact.getString("name"));
        contact.setPhoneNumber(phoneNumber);
        contact.setAge(age);
        contact.setGender(Gender.fromDisplayName(
                jsonContact.getString("gender")));
        contact.setBlocked(Objects.equals(
                jsonContact.getString("isBlocked"), "Заблокирован"));
        return contact;
    }

    @Override
    public String getSupportedFormat() {
        return "json";
    }
}

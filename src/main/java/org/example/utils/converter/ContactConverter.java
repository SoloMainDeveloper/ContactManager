package org.example.utils.converter;

import org.example.entity.Contact;
import org.example.entity.ContactInfo;
import org.json.JSONObject;

/**
 * Преобразователь контакта в различные представления
 */
public class ContactConverter {
    /**
     * Преобразовать контакт в текстовый формат
     */
    public String contactToTxtFormat(Contact contact) {
        ContactInfo info = new ContactInfo(contact);

        return String.format("""
                    Имя контакта: %s
                    Номер телефона: %s
                    Возраст: %s
                    Пол: %s
                    %s
                    """, info.getName(), info.getPhoneNumber(),
                info.getAge(), info.getGender(), info.getIsBlocked());
    }

    /**
     * Преобразовать контакт в csv формат
     */
    public String contactToCsvFormat(Contact contact) {
        ContactInfo info = new ContactInfo(contact);

        return String.format("%s;%s;%s;%s;%s", info.getName(), info.getPhoneNumber(),
                info.getAge(), info.getGender(), info.getIsBlocked());
    }

    public JSONObject contactToJsonFormat(Contact contact) {
        JSONObject jsonContact = new JSONObject();
        ContactInfo info = new ContactInfo(contact);

        jsonContact.put("name", info.getName());
        jsonContact.put("phoneNumber", info.getPhoneNumber());
        jsonContact.put("age", info.getAge());
        jsonContact.put("gender", info.getGender());
        jsonContact.put("isBlocked", info.getIsBlocked());
        return jsonContact;
    }
}

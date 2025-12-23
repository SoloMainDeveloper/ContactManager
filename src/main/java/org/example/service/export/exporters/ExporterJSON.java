package org.example.service.export.exporters;

import org.example.response.AppDocument;
import org.example.entity.Contact;
import org.example.entity.ContactDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Экспортёр контактов в JSON
 */
@Component
public class ExporterJSON implements Exporter {
    @Override
    public AppDocument exportContacts(String fileName, List<Contact> contacts) {
        JSONArray content = new JSONArray();
        for (Contact contact : contacts) {
            content.put(convertToJsonFormat(contact));
        }
        String fileNameWithFormat = String.format("%s.%s",
            fileName, getSupportedFormat());
        return new AppDocument(fileNameWithFormat, content.toString());
    }

    /**
     * Преобразовать контакт в json формат
     */
    private JSONObject convertToJsonFormat(Contact contact) {
        ContactDto dto = new ContactDto(contact);

        JSONObject jsonContact = new JSONObject();
        jsonContact.put("name", dto.getName());
        jsonContact.put("phoneNumber", dto.getPhoneNumber());
        jsonContact.put("age", dto.getAge());
        jsonContact.put("gender", dto.getGender());
        jsonContact.put("isBlocked", dto.getIsBlocked());
        return jsonContact;
    }

    @Override
    public String getSupportedFormat() {
        return "json";
    }
}

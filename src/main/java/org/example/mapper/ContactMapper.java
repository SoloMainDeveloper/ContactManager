package org.example.mapper;

import org.example.entity.Contact;
import org.example.entity.Gender;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер данных из БД в сущность Контакта
 */
public class ContactMapper {
    /**
     * Создаёт контакт на основе ответа от БД
     */
    public Contact resultSetToContactEntity(ResultSet resultSet) {
        try {
            return new Contact(
                    resultSet.getLong("chat_id"),
                    resultSet.getString("name"),
                    resultSet.getString("phone_number"),
                    resultSet.getInt("age"),
                    Gender.valueOf(resultSet.getString("gender")),
                    resultSet.getBoolean("is_blocked")
            );
        } catch (Exception exception) {
            return null;
        }
    }
}

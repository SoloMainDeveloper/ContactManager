package org.example.utils.mapper;

import org.example.entity.Group;
import org.example.utils.converter.GroupConverter;

import java.sql.ResultSet;

/**
 * Маппер данных из БД в сущность Группы
 */
public class GroupMapper {
    /**
     * Создаёт группу на основе ответа от БД
     */
    public Group resultSetToGroupEntity(ResultSet resultSet) {
        GroupConverter converter = new GroupConverter();
        try {
            return new Group(
                    resultSet.getLong("chat_id"),
                    resultSet.getString("name"),
                    converter.stringToContactIds(resultSet.getString("contact_ids"))
            );
        } catch (Exception exception) {
            return null;
        }
    }
}

package org.example.repository;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Репозиторий групп
 */
@Repository
public class GroupRepository implements IGroupRepository {
    /**
     * Объект по управлению обработки событий и соединений с БД
     */
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Конструктор для инициализации NamedParameterJdbcTemplate
     */
    public GroupRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void add(Group group) {
        String addGroupSql = "INSERT INTO public.groups " +
                "(chat_id, name) VALUES (:chatId, :name) RETURNING id";

        MapSqlParameterSource groupParams = new MapSqlParameterSource()
                .addValue("chatId", group.getChatId())
                .addValue("name", group.getName());

        Long groupId = jdbcTemplate.queryForObject(
                addGroupSql,
                groupParams,
                Long.class
        );
        addRelationsBetweenGroupAndContacts(groupId, group.getContactIds());
    }

    /**
     * Добавить связи между группой и контактами.
     * Иными словами добавляет контакты в группу.
     */
    private void addRelationsBetweenGroupAndContacts(
            Long groupId, Set<Long> contactIds) {
        if(contactIds != null && !contactIds.isEmpty()) {
            String addRelationSql = "INSERT INTO public.contact_groups " +
                    "(contact_id, group_id) VALUES (:contactId, :groupId)";

            for (Long contactId : contactIds) {
                MapSqlParameterSource relationParams = new MapSqlParameterSource()
                        .addValue("contactId", contactId)
                        .addValue("groupId", groupId);

                jdbcTemplate.update(addRelationSql, relationParams);
            }
        }
    }

    @Override
    public Optional<Group> findGroupByName(String name, Long chatId) {
        String sql = "SELECT * FROM public.groups " +
                "WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        try {
            Group group = jdbcTemplate.queryForObject(sql, params,
                    (resultSet, rowNum) -> resultSetToGroupEntity(resultSet));
            if(group != null) {
                loadContactsIntoGroup(group);
            }
            return Optional.ofNullable(group);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске группы по имени:" + e);
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при загрузке контактов в группу:" + e);
            return Optional.empty();
        }
    }

    @Override
    public List<Group> findGroupsByChatId(Long chatId, String sorter) {
        String sql = """
        SELECT groups.id, groups.chat_id, groups.name,
            COUNT(contact_groups.contact_id) as participants_count
        FROM public.groups AS groups
        LEFT JOIN public.contact_groups AS contact_groups
            ON groups.id = contact_groups.group_id
        WHERE groups.chat_id = :chatId
        GROUP BY groups.id, groups.chat_id, groups.name
        """ + sorter;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        try {
            List<Group> groups = jdbcTemplate.query(sql, params,
                    (resultSet, rowNum) -> resultSetToGroupEntity(resultSet));
            for(Group group : groups) {
                loadContactsIntoGroup(group);
            }
            return groups;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске групп по chatId:" + e);
            return List.of();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при загрузке контактов в группу:" + e);
            return List.of();
        }
    }

    /**
     * Загрузить уже добавленные контакты из БД в группу.
     * Вносит изменения в передаваемую группу.
     */
    private void loadContactsIntoGroup(Group group) throws SQLException {
        String contactsSql = "SELECT contact.* FROM public.contacts AS contact " +
                "INNER JOIN public.contact_groups AS contact_group " +
                "ON contact.id = contact_group.contact_id " +
                "WHERE contact_group.group_id = :groupId " +
                "ORDER BY contact.name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", group.getId());

        ContactMapper mapper = new ContactMapper();
        List<Contact> contacts = jdbcTemplate.query(contactsSql, params,
                (resultSet, rowNum) -> mapper.resultSetToContactEntity(resultSet));

        for (Contact contact : contacts) {
            group.addContact(contact);
        }
    }

    @Override
    public void update(String currentName, Group group) {
        String updateGroupSql = "UPDATE public.groups SET name = :newName " +
                "WHERE chat_id = :chatId and name = :oldName RETURNING id";

        MapSqlParameterSource groupParams = new MapSqlParameterSource()
                .addValue("chatId", group.getChatId())
                .addValue("oldName", currentName)
                .addValue("newName", group.getName());

        Long groupId = jdbcTemplate.queryForObject(
                updateGroupSql,
                groupParams,
                Long.class
        );

        updateRelationsBetweenGroupAndContacts(groupId, group.getContactIds());
    }

    /**
     * Обновить связи между группой и контактами.
     * Иными словами обновляет контакты группы.
     */
    private void updateRelationsBetweenGroupAndContacts(
            Long groupId, Set<Long> contactIds) {
        String deleteRelationsSql = "DELETE FROM public.contact_groups " +
                "WHERE group_id = :groupId";

        jdbcTemplate.update(deleteRelationsSql,
                new MapSqlParameterSource("groupId", groupId));

        if (contactIds != null && !contactIds.isEmpty()) {
            String addRelationSql = "INSERT INTO public.contact_groups " +
                    "(contact_id, group_id) VALUES (:contactId, :groupId)";

            for (Long contactId : contactIds) {
                MapSqlParameterSource relationParams = new MapSqlParameterSource()
                        .addValue("contactId", contactId)
                        .addValue("groupId", groupId);

                jdbcTemplate.update(addRelationSql, relationParams);
            }
        }
    }

    @Override
    public void deleteByName(String name, Long chatId) {
        String sql = "DELETE FROM public.groups " +
                "WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        jdbcTemplate.update(sql, params);
    }

    /**
     * Создаёт группу на основе ответа от БД
     */
    private Group resultSetToGroupEntity(ResultSet resultSet) throws SQLException {
        return new Group(
                resultSet.getLong("id"),
                resultSet.getLong("chat_id"),
                resultSet.getString("name")
        );
    }
}

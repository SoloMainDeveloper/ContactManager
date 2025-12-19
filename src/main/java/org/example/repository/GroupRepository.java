package org.example.repository;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.utils.GroupOrder;
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
     * Репозиторий контактов
     */
    private final IContactRepository contactRepository;

    public GroupRepository(DataSource dataSource, IContactRepository contactRepository) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.contactRepository = contactRepository;
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
        group.setId(groupId);

        addRelationsBetweenGroupAndContacts(group);
    }

    /**
     * Добавить связи между группой и контактами.
     * Иными словами добавляет контакты в группу.
     */
    private void addRelationsBetweenGroupAndContacts(Group group) {
        List<Contact> contacts = group.getContacts();
        if(!contacts.isEmpty()) {
            String addRelationSql = "INSERT INTO public.contact_groups " +
                    "(contact_id, group_id) VALUES (:contactId, :groupId)";

            for (Contact contact : contacts) {
                MapSqlParameterSource relationParams = new MapSqlParameterSource()
                        .addValue("contactId", contact.getId())
                        .addValue("groupId", group.getId());

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
    public List<Group> findGroupsByChatId(Long chatId, GroupOrder order) {
        String orderBy = getSqlForOrder(order);
        String sql = """
        SELECT groups.id, groups.chat_id, groups.name,
            COUNT(contact_groups.contact_id) as count
        FROM public.groups AS groups
        LEFT JOIN public.contact_groups AS contact_groups
            ON groups.id = contact_groups.group_id
        WHERE groups.chat_id = :chatId
        GROUP BY groups.id, groups.chat_id, groups.name
        """ + orderBy;

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
     * Получить sql-запрос на сортировку
     */
    private String getSqlForOrder(GroupOrder order) {
        return order.isEmpty()
            ? ""
            : " ORDER BY %s %s".formatted(
            order.property().name().toLowerCase(), order.direction().name());
    }

    /**
     * Загрузить уже добавленные контакты из БД в группу.
     * Вносит изменения в передаваемую группу.
     */
    private void loadContactsIntoGroup(Group group) throws SQLException {
        List<Contact> contacts = contactRepository.findContactsByGroupId(group.getId());
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

        jdbcTemplate.update(updateGroupSql, groupParams);

        updateRelationsBetweenGroupAndContacts(group);
    }

    /**
     * Обновить связи между группой и контактами.
     * Иными словами обновляет контакты группы.
     */
    private void updateRelationsBetweenGroupAndContacts(Group group) {
        String deleteRelationsSql = "DELETE FROM public.contact_groups " +
                "WHERE group_id = :groupId";

        jdbcTemplate.update(deleteRelationsSql,
                new MapSqlParameterSource("groupId", group.getId()));

        addRelationsBetweenGroupAndContacts(group);
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

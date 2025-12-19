package org.example.repository;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.utils.ContactFilter;
import org.example.utils.ContactOrder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище контактов
 */
@Repository
public class ContactRepository implements IContactRepository {
    /**
     * Объект по управлению обработки событий и соединений с БД
     */
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Конструктор для инициализации NamedParameterJdbcTemplate
     */
    public ContactRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void add(Contact contact) {
        String sql = "INSERT INTO public.contacts (chat_id, name, phone_number, age, gender, is_blocked) " +
                "VALUES (:chatId, :name, :phoneNumber, :age, :gender, :isBlocked)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", contact.getChatId())
                .addValue("name", contact.getName())
                .addValue("phoneNumber", contact.getPhoneNumber())
                .addValue("age", contact.getAge())
                .addValue("gender", contact.getGender().name())
                .addValue("isBlocked", contact.isBlocked());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<Contact> findContactById(Long contactId, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE id = :id and chat_id = :chatId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", contactId)
                .addValue("chatId", chatId);

        try {
            Contact contact = jdbcTemplate.queryForObject(sql, params,
                    (resultSet, rowNum) -> resultSetToContactEntity(resultSet));
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске контакта по id:" + e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Contact> findContactByName(String name, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        try {
            Contact contact = jdbcTemplate.queryForObject(sql, params,
                    (resultSet, rowNum) -> resultSetToContactEntity(resultSet));
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске контакта по имени:" + e);
            return Optional.empty();
        }
    }

    @Override
    public List<Contact> findContactsByNumber(String number, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE " +
                "chat_id = :chatId and phone_number = :phoneNumber";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("phoneNumber", number);
        try {
            return jdbcTemplate.query(sql, params,
                    (resultSet, rowNum) -> resultSetToContactEntity(resultSet));
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске контактов по номеру:" + e);
            return List.of();
        }
    }

    @Override
    public List<Contact> findContactsByChatId(
            Long chatId, ContactFilter filter, ContactOrder order) {
        String where = getSqlForFilter(filter);
        String orderBy = getSqlForOrder(order);
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId"
            + where
            + orderBy;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("chatId", chatId);

        try {
            return jdbcTemplate.query(sql, params,
                (resultSet, rowNum) -> resultSetToContactEntity(resultSet));
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске контактов по chatId:" + e);
            return List.of();
        }
    }

    /**
     * Получить sql-запрос для фильтрации
     */
    private String getSqlForFilter(ContactFilter filter) {
        if(filter.isEmpty()) {
            return "";
        }
        String condition = switch (filter.condition()) {
            case LESS_THAN -> "<";
            case GREATER_THAN -> ">";
            case EQUALS -> "=";
            case null -> "";
        };
        return " AND %s %s '%s'".formatted(
            filter.property().name().toLowerCase(), condition, filter.value());
    }

    /**
     * Получить sql-запрос для порядка сортировки
     */
    private String getSqlForOrder(ContactOrder order) {
        return order.isEmpty()
            ? ""
            : " ORDER BY %s %s".formatted(
                order.property().name().toLowerCase(), order.direction().name());
    }

    @Override
    public List<Contact> findContactsByGroupId(Long groupId) {
        String sql = "SELECT contact.* FROM public.contacts AS contact " +
            "INNER JOIN public.contact_groups AS contact_group " +
            "ON contact.id = contact_group.contact_id " +
            "WHERE contact_group.group_id = :groupId " +
            "ORDER BY contact.name";

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("groupId", groupId);

        try {
            return jdbcTemplate.query(sql, params,
                (resultSet, rowNum) -> resultSetToContactEntity(resultSet));
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске контактов по id группы:" + e);
            return List.of();
        }
    }

    @Override
    public void update(String currentName, Contact contact) {
        String sql = "UPDATE public.contacts SET name = :name, phone_number = "
                + ":phoneNumber, age = :age, gender = :gender, is_blocked = :isBlocked "
                + "WHERE chat_id = :chatId and name = :currentName";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", contact.getChatId())
                .addValue("currentName", currentName)
                .addValue("name", contact.getName())
                .addValue("phoneNumber", contact.getPhoneNumber())
                .addValue("age", contact.getAge())
                .addValue("gender", contact.getGender().name())
                .addValue("isBlocked", contact.isBlocked());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteByName(String name, Long chatId) {
        String sql = "DELETE FROM public.contacts WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        jdbcTemplate.update(sql, params);
    }

    /**
     * Создаёт контакт на основе ответа от БД
     */
    private Contact resultSetToContactEntity(ResultSet resultSet) throws SQLException {
        return new Contact(
            resultSet.getLong("id"),
            resultSet.getLong("chat_id"),
            resultSet.getString("name"),
            resultSet.getString("phone_number"),
            resultSet.getInt("age"),
            Gender.valueOf(resultSet.getString("gender")),
            resultSet.getBoolean("is_blocked")
        );
    }
}

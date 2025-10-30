package org.example.repository;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.mapper.ContactMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище контактов
 */
public class ContactRepository {
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

    /**
     * Добавить контакт в БД
     */
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

    /**
     * Найти контакт по имени в БД, соответствующий конкретному пользователю по chatId
     */
    public Optional<Contact> findContactByName(String name, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        try {
            Contact contact = jdbcTemplate.queryForObject(sql, params, (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }

    }

    /**
     * Найти контакт по номеру в БД, соответствующий конкретному пользователю по chatId
     */
    public Optional<Contact> findContactByNumber(String number, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and phone_number = :phoneNumber";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("phoneNumber", number);

        try {
            Contact contact = jdbcTemplate.queryForObject(sql, params, (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    /**
     * Найти контакты по id пользователя
     */
    public List<Contact> findContactsByChatId(Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        return getResponseFromFindContactsQuery(sql, params);
    }

    /**
     * Найти контакты по id пользователя c использованием сортировки по полу
     */
    public List<Contact> findContactsByChatIdAndGender(Long chatId, Gender gender) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and gender = :gender";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("gender", gender.name());

        return getResponseFromFindContactsQuery(sql, params);
    }

    /**
     * Найти контакты по id пользователя c использованием сортировки по возрасту
     */
    public List<Contact> findContactsByChatIdAndAge(Long chatId, String ageCondition) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and age" + ageCondition;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        return getResponseFromFindContactsQuery(sql, params);
    }

    public List<Contact> getResponseFromFindContactsQuery(String sql, MapSqlParameterSource params) {
        try {
            return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

<<<<<<< HEAD
=======
    /**
     * Обновляет поле, отвечающее за блокировку
     */
>>>>>>> origin/task-1
    public void updateBlockField(Contact contact) {
        String sql = "UPDATE public.contacts SET " +
                "is_blocked = :isBlocked WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", contact.getChatId())
                .addValue("name", contact.getName())
                .addValue("isBlocked", contact.isBlocked());

        jdbcTemplate.update(sql, params);
    }

    /**
     * Обновить контакт в БД
     */
    public void update(Contact contact) {
        String sql = "UPDATE public.contacts SET name = :name, phone_number = :phoneNumber, " +
                "age = :age, gender = :gender WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", contact.getChatId())
                .addValue("name", contact.getName())
                .addValue("phoneNumber", contact.getPhoneNumber())
                .addValue("age", contact.getAge())
                .addValue("gender", contact.getGender().name());

        jdbcTemplate.update(sql, params);
    }

    /**
     * Удалить контакт из БД по имени, соответствующий конкретному пользователю
     */
    public void deleteByName(String name, Long chatId) {
        String sql = "DELETE FROM public.contacts WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        jdbcTemplate.update(sql, params);
    }
}

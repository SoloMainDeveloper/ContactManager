package org.example.repository;

import org.example.entity.Contact;
import org.example.utils.ContactMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
                    (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException exception) {
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
            Contact contact = jdbcTemplate.queryForObject(sql, params, (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Contact> findContactsByNumber(String number, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and phone_number = :phoneNumber";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("phoneNumber", number);
        try {
            return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
        } catch (EmptyResultDataAccessException exception) {
            return List.of();
        }
    }

    @Override
    public List<Contact> findContactsByChatId(Long chatId, String filter, String sorter) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId" + filter + sorter;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        try {
            return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
                ContactMapper mapper = new ContactMapper();
                return mapper.resultSetToContactEntity(resultSet);
            });
        } catch (EmptyResultDataAccessException exception) {
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
}

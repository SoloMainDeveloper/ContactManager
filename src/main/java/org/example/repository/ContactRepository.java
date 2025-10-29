package org.example.repository;

import org.example.entity.Contact;
import org.example.entity.Gender;
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
        String sql = "INSERT INTO public.contacts (chat_id, name, phone_number, age, gender) " +
                "VALUES (:chatId, :name, :phoneNumber, :age, :gender)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", contact.getChatId())
                .addValue("name", contact.getName())
                .addValue("phoneNumber", contact.getPhoneNumber())
                .addValue("age", contact.getAge())
                .addValue("gender", contact.getGender().name());

        jdbcTemplate.update(sql, params);
    }

    /**
     * Найти контакт по имени в БД, соответствующий конкретному пользователю по chatId
     */
    public Optional<Contact> findByName(String name, Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId and name = :name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId)
                .addValue("name", name);

        Contact contact = jdbcTemplate.queryForObject(sql, params, (set, rowNum) -> {
            Contact result = new Contact();
            result.setChatId(set.getLong("chat_id"));
            result.setName(set.getString("name"));
            result.setPhoneNumber(set.getString("phone_number"));
            result.setAge(set.getInt("age"));
            result.setGender(Gender.valueOf(set.getString("gender")));
            return result;
        });
        return Optional.ofNullable(contact);
    }

    /**
     * Найти контакты по id пользователя в БД
     */
    public List<Contact> findContactsByChatId(Long chatId) {
        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        return jdbcTemplate.query(sql, params, (set, rowNum) -> {
            Contact contact = new Contact();
            contact.setChatId(set.getLong("chat_id"));
            contact.setName(set.getString("name"));
            contact.setPhoneNumber(set.getString("phone_number"));
            contact.setAge(set.getInt("age"));
            contact.setGender(Gender.valueOf(set.getString("gender")));
            return contact;
        });
    }

    /**
     * Обновить контакт в БД
     */
    public void update(Contact contact) {
        String sql = "UPDATE public.contacts SET phone_number = :phoneNumber, " +
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

    /**
     * Найти контакт по номеру в БД, соответствующий конкретному пользователю по chatId
     */
    public Optional<Contact> findByNumber(String number, long chatId) {
        //TODO
        return null;
    }
}

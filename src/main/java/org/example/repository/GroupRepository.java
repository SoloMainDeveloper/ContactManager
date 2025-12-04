package org.example.repository;

import org.example.entity.Group;
import org.example.utils.GroupConverter;
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
        String sql = "INSERT INTO public.groups (chat_id, name, contact_ids) " +
                "VALUES (:chatId, :name, :contactIds)";

        GroupConverter converter = new GroupConverter();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", group.getChatId())
                .addValue("name", group.getName())
                .addValue("contactIds", converter
                        .contactIdsToString(group.getContactIds()));

        jdbcTemplate.update(sql, params);
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
            return Optional.ofNullable(group);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске группы по имени:" + e);
            return Optional.empty();
        }
    }

    @Override
    public List<Group> findGroupsByChatId(Long chatId, String sorter) {
        String sql = "SELECT * FROM public.groups WHERE chat_id = :chatId" + sorter;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        try {
            return jdbcTemplate.query(sql, params,
                    (resultSet, rowNum) -> resultSetToGroupEntity(resultSet));
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("Ошибка запроса к БД при поиске групп по chatId:" + e);
            return List.of();
        }
    }

    @Override
    public void update(String currentName, Group group) {
        String sql = "UPDATE public.groups " +
                "SET name = :newName, contact_ids = :contactIds " +
                "WHERE chat_id = :chatId and name = :oldName";

        GroupConverter converter = new GroupConverter();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", group.getChatId())
                .addValue("oldName", currentName)
                .addValue("newName", group.getName())
                .addValue("contactIds", converter
                        .contactIdsToString(group.getContactIds()));

        jdbcTemplate.update(sql, params);
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
        GroupConverter converter = new GroupConverter();
        return new Group(
                resultSet.getLong("chat_id"),
                resultSet.getString("name"),
                converter.stringToContactIds(resultSet.getString("contact_ids"))
        );
    }
}

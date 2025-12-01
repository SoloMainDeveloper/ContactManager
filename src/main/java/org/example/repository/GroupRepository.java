package org.example.repository;

import org.example.entity.Group;
import org.example.utils.GroupConverter;
import org.example.utils.GroupMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
                    (resultSet, rowNum) -> {
                GroupMapper mapper = new GroupMapper();
                return mapper.resultSetToGroupEntity(resultSet);
            });
            return Optional.ofNullable(group);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Group> findGroupsByChatId(Long chatId, String sorter) {
        String sql = "SELECT * FROM public.groups WHERE chat_id = :chatId" + sorter;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatId", chatId);

        try {
            return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
                GroupMapper mapper = new GroupMapper();
                return mapper.resultSetToGroupEntity(resultSet);
            });
        } catch (EmptyResultDataAccessException exception) {
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
}

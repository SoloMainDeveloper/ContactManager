package org.example;

import org.example.entity.Group;
import org.example.repository.GroupRepository;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.*;

/**
 * Фейковое хранилище групп. Необходимо для тестов
 */
public class FakeGroupRepository extends GroupRepository {
    /**
     * Объект хранения групп вместо БД
     */
    private final Map<Long, Map<String, Group>> groups;

    /**
     * Конструктор
     */
    public FakeGroupRepository() {
        super(new PGSimpleDataSource());
        groups = new LinkedHashMap<>();
    }

    /**
     * Возвращает количество групп у пользователя с данным chatId
     */
    public int getCurrentGroupsSize(Long chatId) {
        if (groups.containsKey(chatId)) {
            return groups.get(chatId).size();
        }
        return 0;
    }

    @Override
    public void add(Group group) {
        Long chatId = group.getChatId();
        if (!groups.containsKey(chatId)) {
            groups.put(chatId, new LinkedHashMap<>());
        }
        if (groups.get(chatId).containsKey(group.getName())) {
            throw new RuntimeException();
        }
        groups.get(chatId).put(group.getName(), group);
    }

    @Override
    public Optional<Group> findGroupByName(String name, Long chatId) {
        Map<String, Group> currentGroups = groups.get(chatId);
        return currentGroups == null || currentGroups.isEmpty() || currentGroups.get(name) == null
                ? Optional.empty()
                : Optional.of(currentGroups.get(name));
    }

    @Override
    public List<Group> findGroupsByChatId(Long chatId, String sorter) {
        Map<String, Group> userGroups = groups.getOrDefault(chatId, new HashMap<>());
        List<Group> groupList = new ArrayList<>(userGroups.values());

        return applySorter(groupList, sorter);
    }

    /**
     * Применить сортировку к списку групп
     */
    private List<Group> applySorter(List<Group> groups, String sorter) {
        if (sorter == null || sorter.isEmpty()) {
            return groups;
        }

        List<Group> sortedGroups = new ArrayList<>(groups);
        if (sorter.contains("ORDER BY name ASC")) {
            sortedGroups.sort(Comparator.comparing(Group::getName));
        }
        else if (sorter.contains("ORDER BY name DESC")) {
            sortedGroups.sort(Comparator.comparing(Group::getName).reversed());
        }
        return sortedGroups;
    }

    @Override
    public void update(String oldName, Group group) {
        Map<String, Group> userGroups = groups.get(group.getChatId());
        if (userGroups != null) {
            if (!oldName.equals(group.getName())) {
                userGroups.remove(oldName);
            }
            userGroups.put(group.getName(), group);
        }
    }

    @Override
    public void deleteByName(String name, Long chatId) {
        Map<String, Group> userGroups = groups.get(chatId);
        if (userGroups != null) {
            userGroups.remove(name);
        }
    }
}

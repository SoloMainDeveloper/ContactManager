package org.example;

import org.example.entity.Group;
import org.example.repository.IGroupRepository;

import java.util.*;

/**
 * Фейковое хранилище групп. Необходимо для тестов
 */
public class FakeGroupRepository implements IGroupRepository {
    /**
     * Хранилище групп
     */
    private final Map<Long, Map<String, Group>> groups = new LinkedHashMap<>();

    @Override
    public void add(Group group) {
        Long chatId = group.getChatId();
        getOrCreateGroupsByChatId(chatId).put(group.getName(), group);
    }

    @Override
    public Optional<Group> findGroupByName(String name, Long chatId) {
        return Optional.ofNullable(getOrCreateGroupsByChatId(chatId).get(name));
    }

    @Override
    public List<Group> findGroupsByChatId(Long chatId, String sorter) {
        Map<String, Group> userGroups = getOrCreateGroupsByChatId(chatId);
        return applySorter(userGroups.values().stream().toList(), sorter);
    }

    /**
     * Применить сортировку к списку групп
     */
    private List<Group> applySorter(List<Group> groups, String sorter) {
        List<Group> sortedGroups = new ArrayList<>(groups);
        switch (sorter) {
            case " ORDER BY groups.name ASC" ->
                    sortedGroups.sort(Comparator.comparing(Group::getName));
            case " ORDER BY groups.name DESC" ->
                    sortedGroups.sort(Comparator.comparing(Group::getName).reversed());
            case " ORDER BY participants_count ASC, groups.name ASC" ->
                    sortedGroups.sort(Comparator
                            .comparingInt((Group g) -> g.getContacts().size()));
            case " ORDER BY participants_count DESC, groups.name ASC" ->
                    sortedGroups.sort(Comparator
                            .comparingInt((Group g) -> g.getContacts().size())
                            .reversed());
            default -> {}
        }
        return sortedGroups;
    }

    @Override
    public void update(String currentName, Group group) {
        Map<String, Group> userGroups = groups.get(group.getChatId());
        userGroups.remove(currentName);
        userGroups.put(group.getName(), group);
    }

    @Override
    public void deleteByName(String name, Long chatId) {
        groups.get(chatId).remove(name);
    }

    /**
     * Получить группу по chatId пользователя. Если ещё не существует, предварительно
     * её создаёт.
     */
    private Map<String, Group> getOrCreateGroupsByChatId(Long chatId) {
        if(!groups.containsKey(chatId)) {
            groups.put(chatId, new LinkedHashMap<>());
        }
        return groups.get(chatId);
    }
}

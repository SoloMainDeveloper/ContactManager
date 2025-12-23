package org.example.group;

import org.example.entity.Group;
import org.example.repository.IGroupRepository;
import org.example.utils.GroupOrder;

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
    public List<Group> findGroupsByChatId(Long chatId, GroupOrder order) {
        Map<String, Group> userGroups = getOrCreateGroupsByChatId(chatId);
        return getContactsAfterSorting(userGroups.values().stream().toList(), order);
    }

    /**
     * Получить группы после примененной сортировки
     */
    private List<Group> getContactsAfterSorting(List<Group> groups, GroupOrder order) {
        List<Group> sortedGroups = new ArrayList<>(groups);
        if (order.getProperty() == GroupOrder.OrderProperty.COUNT) {
            switch (order.getDirection()) {
                case ASC -> sortedGroups
                    .sort(Comparator.comparingInt((Group g) -> g.getContacts().size()));
                case DESC -> sortedGroups
                    .sort(Comparator.comparingInt((Group g) -> g.getContacts().size()).reversed());
            }
        } else if (order.getProperty() == GroupOrder.OrderProperty.NAME) {
            switch (order.getDirection()) {
                case ASC -> sortedGroups
                    .sort(Comparator.comparing(Group::getName));
                case DESC -> sortedGroups
                    .sort(Comparator.comparing(Group::getName).reversed());
            }
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
        if (!groups.containsKey(chatId)) {
            groups.put(chatId, new LinkedHashMap<>());
        }
        return groups.get(chatId);
    }
}

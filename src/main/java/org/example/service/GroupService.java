package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.repository.GroupRepository;
import org.example.utils.converter.GroupConverter;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Сервис групп
 */
@Service
public class GroupService {
    /**
     * Репозиторий для групп
     */
    private final GroupRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    /**
     * Попытаться добавить группу, при успешном выполнении возвращается true
     */
    public Boolean tryAddGroup(Long chatId, Map<String, String> params) {
        try {
            Set<Long> contactIds = new GroupConverter()
                    .stringToContactIds(params.getOrDefault("contactIds", ""));
            Group group = new Group(chatId, params.get("groupName"), contactIds);
            repository.add(group);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка при попытке добавить группу: " + e);
            return false;
        }
    }

    /**
     * Найти контакт по имени у данного пользователя
     */
    public Optional<Group> findGroupByName(Long chatId, String groupName) {
        return repository.findGroupByName(groupName, chatId);
    }

    /**
     * Найти все контакты, имеющееся у данного пользователя
     */
    public List<Group> findGroupsByChatId(Long chatId) {
        return repository.findGroupsByChatId(chatId, "");
    }

    /**
     * Найти все контакты, имеющееся у данного пользователя с применением сортировки
     */
    public List<Group> findGroupsByChatIdWithSorter(
            Long chatId, Map<String, String> params) {
        if(!params.containsKey("sorter")) {
            return repository.findGroupsByChatId(chatId, "");
        }

        String sorter = "";
        String sorterValue = params.get("sorter");

        if(Objects.equals(sorterValue, "В алфавитном порядке имени")) {
            sorter = " ORDER BY name ASC";
        }
        if(Objects.equals(sorterValue, "В обратном алфавитному порядке имени")) {
            sorter = " ORDER BY name DESC";
        }
        List<Group> groups = repository.findGroupsByChatId(chatId, sorter);

        if(Objects.equals(sorterValue, "В порядке возрастания кол-ва участников")) {
            groups = groups.stream()
                    .sorted(Comparator.comparingInt(group ->
                            group.getContactIds().size()))
                    .toList();
        }
        if(Objects.equals(sorterValue, "В порядке убывания кол-ва участников")) {
            groups = groups.stream()
                    .sorted(Comparator.comparingInt((Group group) ->
                            group.getContactIds().size()).reversed())
                    .toList();
        }
        return groups;
    }

    /**
     * Попытаться обновить группу
     */
    public Boolean tryUpdateGroupWithNewName(Long chatId, String oldName, String newName) {
        try {
            Group group = findGroupByName(chatId, oldName).orElseThrow();
            group.setName(newName);
            repository.update(oldName, group);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Произошла ошибка при попытке изменить группу: " + e);
            return false;
        }
    }

    /**
     * Попытаться добавить контакт в группу
     */
    public Boolean tryAddContactToGroup(Long chatId, String groupName, Contact contact) {
        try {
            Group group = findGroupByName(chatId, groupName).orElseThrow();
            Long contactId = contact.getId();
            group.addContactId(contactId);
            repository.update(groupName, group);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка: при добавлении контакта в группу. "
                    + "Группа не была найдена. Ошибка: " + e);
            return false;
        }
    }

    /**
     * Попытаться удалить контакт из группы
     */
    public Boolean tryRemoveContactFromGroup(
            Long chatId, String groupName, Contact contact) {
        try {
            Group group = findGroupByName(chatId, groupName).orElseThrow();
            group.removeContactId(contact.getId());
            repository.update(groupName, group);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка: при добавлении контакта в группу. "
                    + "Группа не была найдена. Ошибка: " + e);
            return false;
        }
    }

    /**
     * Удалить группу по имени
     */
    public void deleteGroupByName(Long chatId, String groupName) {
        repository.deleteByName(groupName, chatId);
    }
}

package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.utils.converter.GroupConverter;
import org.example.exceptions.GroupAlreadyExistsException;
import org.example.exceptions.GroupDoesNotExistException;
import org.example.repository.IGroupRepository;
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
    private final IGroupRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public GroupService(IGroupRepository repository) {
        this.repository = repository;
    }

    /**
     * Попытаться добавить группу
     * @param chatId идентификатор чата
     * @param params параметры добавляемой группы
     * @throws GroupAlreadyExistsException если группа уже существует
     */
    public void tryAddGroup(Long chatId, Map<String, String> params)
            throws GroupAlreadyExistsException {
        Set<Long> contactIds = new GroupConverter()
                .stringToContactIds(params.getOrDefault("contactIds", ""));
        String groupName = params.get("groupName");
        Group group = new Group(chatId, groupName, contactIds);
        if(findGroupByName(chatId, groupName).isEmpty()){
            repository.add(group);
        } else {
            throw new GroupAlreadyExistsException(
                    "Группа %s уже существует".formatted(groupName));
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
     * Попытаться обновить имя группы
     * @param chatId идентификатор чата
     * @param oldName старое имя группы
     * @param newName новое имя группы
     * @throws GroupDoesNotExistException если группа не существует
     */
    public void tryUpdateGroupWithNewName(Long chatId, String oldName, String newName)
            throws GroupDoesNotExistException {
        Group group = findGroupByName(chatId, oldName)
                .orElseThrow(() -> new GroupDoesNotExistException(
                        "Группа %s не существует".formatted(oldName)));
        group.setName(newName);
        repository.update(oldName, group);
    }

    /**
     * Попытаться добавить контакт в группу
     * @param chatId идентификатор чата
     * @param groupName имя группы
     * @param contact добавляемый контакт
     * @throws GroupDoesNotExistException если группа не существует
     */
    public void tryAddContactToGroup(Long chatId, String groupName, Contact contact)
            throws GroupDoesNotExistException {
        Group group = findGroupByName(chatId, groupName)
                .orElseThrow(() -> new GroupDoesNotExistException(
                        "Группа %s не существует".formatted(groupName)));
        Long contactId = contact.getId();
        group.addContactId(contactId);
        repository.update(groupName, group);
    }

    /**
     * Попытаться удалить контакт из группы
     * @param chatId идентификатор чата
     * @param groupName имя группы
     * @param contact удаляемый контакт
     * @throws GroupDoesNotExistException если группа не существует
     */
    public void tryRemoveContactFromGroup(Long chatId, String groupName, Contact contact)
            throws GroupDoesNotExistException {
        Group group = findGroupByName(chatId, groupName)
                .orElseThrow(() -> new GroupDoesNotExistException(
                        "Группа %s не существует".formatted(groupName)));
        group.removeContactId(contact.getId());
        repository.update(groupName, group);
    }

    /**
     * Удалить группу по имени
     */
    public void deleteGroupByName(Long chatId, String groupName) {
        repository.deleteByName(groupName, chatId);
    }
}

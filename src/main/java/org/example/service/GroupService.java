package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.repository.GroupRepository;
import org.example.utils.GroupConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public GroupService(GroupRepository repository){
        this.repository = repository;
    }

    /**
     * Попытаться добавить группу, при успешном выполнении возвращается true
     */
    public Boolean tryAdd(Long chatId, Map<String, String> params){
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
    public Optional<Group> findGroupByName(Long chatId, String groupName){
        return repository.findGroupByName(groupName, chatId);
    }

    /**
     * Возвращает все контакты, имеющееся у данного пользователя
     */
    public List<Group> findGroupsByChatId(Long chatId){
        return repository.findGroupsByChatId(chatId);
    }

    /**
     * Попытаться обновить группу
     */
    public Boolean tryUpdateGroupWithNewName(Long chatId, String name, String newName) {
        try {
            Group oldGroup = findGroupByName(chatId, name).orElseThrow();
            oldGroup.setName(newName);
            repository.update(oldGroup);
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
    public Boolean tryAddContactToGroup(Long chatId, String groupName, Contact contact){
        try {
            Group oldGroup = findGroupByName(chatId, groupName).orElseThrow();
            oldGroup.addContactId(contact.getId());
            repository.update(oldGroup);
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
            Long chatId, String groupName, Contact contact){
        try {
            Group oldGroup = findGroupByName(chatId, groupName).orElseThrow();
            oldGroup.removeContactId(contact.getId());
            repository.update(oldGroup);
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
    public void deleteGroupByName(Long chatId, String groupName){
        repository.deleteByName(groupName, chatId);
    }
}

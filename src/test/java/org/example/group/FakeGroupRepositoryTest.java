package org.example.group;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.entity.Group;
import org.example.utils.GroupOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Тестируем {@link FakeGroupRepository#findGroupsByChatId} (потому что он сложный)
 */
class FakeGroupRepositoryTest {
    /**
     * Фейк-групп репозиторий
     */
    private FakeGroupRepository fakeGroupRepository;

    /**
     * ChatId пользователя
     */
    private static final Long chatId = 426L;

    /**
     * Список групп для тестов
     */
    private static final List<Group> groups = new ArrayList<>();

    /**
     * Создаём группы для дальнейшего использования
     */
    @BeforeAll
    static void setupGroups(){
        Contact contact1 = new Contact(
                1L, chatId, "Юлия", "9543", 34, Gender.FEMALE, false);
        Contact contact2 = new Contact(
                2L, chatId, "Олег", "12345", 45, Gender.MALE, false);
        Group group1 = new Group(chatId, "Августы");
        Group group2 = new Group(chatId, "Боевые");
        Group group3 = new Group(chatId, "Весельчаки");
        group1.addContact(contact1);
        group2.addContact(contact1);
        group2.addContact(contact2);
        groups.add(group2);
        groups.add(group1);
        groups.add(group3);
    }

    /**
     * Инициализируем фейк-групп репозиторий
     */
    @BeforeEach
    void setupFakeGroupRepository() {
        fakeGroupRepository = new FakeGroupRepository();
    }

    /**
     * Тестируем поиск групп без сортировки
     */
    @Test
    void findGroupsByChatId() {
        for(Group group : groups){
            fakeGroupRepository.add(group);
        }
        List<Group> groupsFound = fakeGroupRepository.findGroupsByChatId(chatId,
                GroupOrder.none());
        Assertions.assertEquals(3, groupsFound.size());
        Assertions.assertEquals("Боевые", groupsFound.get(0).getName());
        Assertions.assertEquals("Августы", groupsFound.get(1).getName());
        Assertions.assertEquals("Весельчаки", groupsFound.get(2).getName());
    }

    /**
     * Тестируем поиск групп с сортировкой по имени
     */
    @Test
    void findGroupsByChatIdWithSortByName() {
        for(Group group : groups){
            fakeGroupRepository.add(group);
        }
        List<Group> groupsFound = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.NAME, GroupOrder.Direction.ASC));
        Assertions.assertEquals("Августы", groupsFound.get(0).getName());
        Assertions.assertEquals("Боевые", groupsFound.get(1).getName());
        Assertions.assertEquals("Весельчаки", groupsFound.get(2).getName());

        List<Group> groupsFound2 = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.NAME, GroupOrder.Direction.DESC));
        Assertions.assertEquals("Весельчаки", groupsFound2.get(0).getName());
        Assertions.assertEquals("Боевые", groupsFound2.get(1).getName());
        Assertions.assertEquals("Августы", groupsFound2.get(2).getName());
    }

    /**
     * Тестируем поиск групп с сортировкой по количеству участников
     */
    @Test
    void findGroupsByChatIdWithSortByParticipantsCount() {
        for(Group group : groups) {
            fakeGroupRepository.add(group);
        }
        List<Group> groupsFound = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.COUNT, GroupOrder.Direction.ASC));
        Assertions.assertEquals("Весельчаки", groupsFound.get(0).getName());
        Assertions.assertEquals("Августы", groupsFound.get(1).getName());
        Assertions.assertEquals("Боевые", groupsFound.get(2).getName());

        List<Group> groupsFound2 = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.COUNT, GroupOrder.Direction.DESC));
        Assertions.assertEquals("Боевые", groupsFound2.get(0).getName());
        Assertions.assertEquals("Августы", groupsFound2.get(1).getName());
        Assertions.assertEquals("Весельчаки", groupsFound2.get(2).getName());
    }
}
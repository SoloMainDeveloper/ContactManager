package org.example.group;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.entity.Group;
import org.example.utils.GroupOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
     * Инициализируем фейк-групп репозиторий
     */
    @BeforeEach
    void setupFakeGroupRepository() {
        fakeGroupRepository = new FakeGroupRepository();
    }

    /**
     * Тестируем поиск групп с сортировкой по имени
     */
    @Test
    void findGroupsByChatIdWithSortByName() {
        fakeGroupRepository.add(new Group(chatId, "Августы"));
        fakeGroupRepository.add(new Group(chatId, "Боевые"));

        List<Group> groupsFound = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.NAME, GroupOrder.Direction.ASC));
        Assertions.assertEquals("Августы", groupsFound.get(0).getName());
        Assertions.assertEquals("Боевые", groupsFound.get(1).getName());

        List<Group> groupsFound2 = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.NAME, GroupOrder.Direction.DESC));
        Assertions.assertEquals("Боевые", groupsFound2.get(0).getName());
        Assertions.assertEquals("Августы", groupsFound2.get(1).getName());
    }

    /**
     * Тестируем поиск групп с сортировкой по количеству участников
     */
    @Test
    void findGroupsByChatIdWithSortByParticipantsCount() {
        Contact contact1 = new Contact(1L, chatId, "Мая", "95", 34, Gender.FEMALE, false);
        Contact contact2 = new Contact(2L, chatId, "Ян", "12345", 45, Gender.MALE, false);

        Group group1 = new Group(chatId, "Одноклассники");
        Group group2 = new Group(chatId, "Английский");
        Group group3 = new Group(chatId, "Двор");

        group1.addContact(contact1);
        group2.addContact(contact1);
        group2.addContact(contact2);

        fakeGroupRepository.add(group1);
        fakeGroupRepository.add(group2);
        fakeGroupRepository.add(group3);

        List<Group> groupsFound = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.COUNT, GroupOrder.Direction.ASC));
        Assertions.assertEquals("Двор", groupsFound.get(0).getName());
        Assertions.assertEquals("Одноклассники", groupsFound.get(1).getName());
        Assertions.assertEquals("Английский", groupsFound.get(2).getName());

        List<Group> groupsFound2 = fakeGroupRepository.findGroupsByChatId(chatId,
                new GroupOrder(GroupOrder.OrderProperty.COUNT, GroupOrder.Direction.DESC));
        Assertions.assertEquals("Английский", groupsFound2.get(0).getName());
        Assertions.assertEquals("Одноклассники", groupsFound2.get(1).getName());
        Assertions.assertEquals("Двор", groupsFound2.get(2).getName());
    }
}
package org.example.keyboardcreator;

import java.util.List;

/**
 * Класс, содержащий часто используемые наборы клавиатур
 */
public class ReplyKeyboardConstants {
    /**
     * Возвращает кнопки главного меню
     */
    public final List<String> mainMenu() {
        return List.of("Контакты", "Группы");
    }

    /**
     * Возвращает кнопки меню контактов
     */
    public List<String> contactsMenu() {
        return List.of("Добавить", "Получить все", "Найти", "Назад");
    }

    /**
     * Возвращает кнопки меню текущего контакта
     */
    public List<String> currentContactMenu() {
        return List.of("Информация", "Изменить", "Блокировать", "Удалить", "Назад");
    }

    /**
     * Возвращает кнопки меню добавления контакта
     */
    public List<String> addContactMenu() {
        return List.of("Номер", "Возраст", "Пол", "Сохранить контакт", "Назад");
    }

    /**
     * Возвращает кнопки меню поиска
     */
    public List<String> findContactMenu() {
        return List.of("Поиск по имени", "Поиск по номеру", "Назад");
    }

    /**
     * Возвращает кнопки меню редактирования контакта
     */
    public List<String> editContactMenu() {
        return List.of("Имя", "Номер", "Возраст", "Пол", "Изменить контакт", "Назад");
    }

    /**
     * Возвращает кнопки меню получения всех контактов
     */
    public List<String> getAllContactsMenu() {
        return List.of("Получить", "Добавить фильтр", "Добавить сортировку", "Назад");
    }

    /**
     * Возвращает кнопки меню фильтрации
     */
    public List<String> addFilterMenu() {
        return List.of("По полу", "По возрасту", "Назад к выбору");
    }

    /**
     * Возвращает кнопки меню фильтрации по полу
     */
    public List<String> addFilterByGenderMenu() {
        return List.of("Мужской", "Женский", "Не выбрано", "Назад к выбору");
    }

    /**
     * Возвращает кнопки меню сортировки
     */
    public List<String> addSorterMenu() {
        return List.of("В порядке убывания возраста", "В порядке возрастания возраста",
                "В алфавитном порядке имени", "В обратном алфавитному порядку имени", "Назад к выбору");
    }

    /**
     * Возвращает кнопки меню групп
     */
    public List<String> groupsMenu() {
        return List.of("Добавить", "Получить все", "Найти", "Назад");
    }
}

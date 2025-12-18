package org.example.keyboardcreator;

import java.util.List;

/**
 * Класс, содержащий часто используемые наборы клавиатур.
 * Вячеслав Юрьевич, константы Вы нам тут разрешили
 */
public class ReplyKeyboardConstants {
    /**
     * Приватный конструктор, чтобы не создавали экземпляр класса
     */
    private ReplyKeyboardConstants() {
    }

    /**
     * Кнопки главного меню
     */
    public static final List<String> MAIN_MENU = List.of("Контакты", "Группы", "Данные");

    /**
     * Кнопки меню контактов
     */
    public static final List<String> CONTACTS_MENU = List.of(
            "Добавить", "Получить все", "Найти", "Назад");

    /**
     * Кнопки меню текущего контакта
     */
    public static final List<String> CURRENT_CONTACT_MENU = List.of(
            "Информация", "Изменить", "Блокировать", "Удалить", "Назад");

    /**
     * Кнопки меню добавления контакта
     */
    public static final List<String> ADD_CONTACT_MENU = List.of(
            "Номер", "Возраст", "Пол", "Сохранить контакт", "Назад");

    /**
     * Кнопки меню поиска
     */
    public static final List<String> FIND_CONTACT_MENU = List.of(
            "Поиск по имени", "Поиск по номеру", "Назад");

    /**
     * Кнопки меню редактирования контакта
     */
    public static final List<String> EDIT_CONTACT_MENU = List.of(
            "Имя", "Номер", "Возраст", "Пол", "Изменить контакт", "Назад");

    /**
     * Кнопки меню получения всех контактов
     */
    public static final List<String> GET_ALL_CONTACTS_MENU = List.of(
            "Получить", "Добавить фильтр", "Добавить сортировку", "Назад");

    /**
     * Кнопки меню фильтрации
     */
    public static final List<String> ADD_FILTER_MENU = List.of(
            "По полу", "По возрасту", "Назад к выбору");

    /**
     * Кнопки меню фильтрации по полу
     */
    public static final List<String> ADD_FILTER_BY_GENDER_MENU = List.of(
            "Мужской", "Женский", "Не выбрано", "Назад к выбору");

    /**
     * Кнопки меню сортировки у контакта
     */
    public static final List<String> ADD_SORTER_CONTACT_MENU = List.of(
            "В порядке убывания возраста", "В порядке возрастания возраста",
            "В алфавитном порядке имени", "В обратном алфавитному порядку имени",
            "Назад к выбору");

    /**
     * Кнопки меню групп
     */
    public static final List<String> GROUPS_MENU = List.of(
            "Добавить", "Получить все", "Найти", "Назад");

    /**
     * Кнопки меню получения всех групп
     */
    public static final List<String> GET_ALL_GROUPS_MENU = List.of(
            "Получить", "Сортировать", "Назад");

    /**
     * Кнопки меню для создания группы
     */
    public static final List<String> ADD_GROUP_MENU = List.of(
            "Добавить контакт", "Сохранить группу", "Назад");

    /**
     * Кнопки меню текущего группы
     */
    public static final List<String> CURRENT_GROUP_MENU = List.of(
            "Вывести все контакты группы", "Изменить", "Удалить", "Назад");

    /**
     * Кнопки меню редактирования группы
     */
    public static final List<String> EDIT_GROUP_MENU = List.of(
            "Изменить имя группы", "Удалить контакт из группы",
            "Добавить контакт в группу", "Сохранить группу", "Назад");

    /**
     * Кнопки меню сортировки у группы
     */
    public static final List<String> ADD_SORTER_GROUP_MENU = List.of(
            "В алфавитном порядке имени",
            "В обратном алфавитному порядке имени",
            "В порядке убывания кол-ва участников",
            "В порядке возрастания кол-ва участников",
            "Назад к выбору");

    /**
     * Возвращает кнопки меню Данные
     */
    public static final List<String> DATA_MENU = List.of(
        "Импорт контактов", "Экспорт контактов", "Назад");
}

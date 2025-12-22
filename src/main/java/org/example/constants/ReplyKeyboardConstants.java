package org.example.constants;

import org.example.entity.Gender;

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
    public static final List<String> MAIN_MENU = List.of(UserCommandConstants.CONTACTS, UserCommandConstants.GROUPS);

    /**
     * Кнопки меню контактов
     */
    public static final List<String> CONTACTS_MENU = List.of(
            UserCommandConstants.ADD, UserCommandConstants.GET_ALL, UserCommandConstants.FIND, UserCommandConstants.BACK);

    /**
     * Кнопки меню текущего контакта
     */
    public static final List<String> CURRENT_CONTACT_MENU = List.of(
            UserCommandConstants.INFO, UserCommandConstants.EDIT, UserCommandConstants.BLOCK, UserCommandConstants.DELETE, UserCommandConstants.BACK);

    /**
     * Кнопки меню добавления контакта
     */
    public static final List<String> ADD_CONTACT_MENU = List.of(
            UserCommandConstants.NUMBER, UserCommandConstants.AGE, UserCommandConstants.GENDER, UserCommandConstants.SAVE_CONTACT, UserCommandConstants.BACK);

    /**
     * Кнопки меню поиска
     */
    public static final List<String> FIND_CONTACT_MENU = List.of(
            UserCommandConstants.FIND_BY_NAME, UserCommandConstants.FIND_BY_NUMBER, UserCommandConstants.BACK);

    /**
     * Кнопки меню редактирования контакта
     */
    public static final List<String> EDIT_CONTACT_MENU = List.of(
            UserCommandConstants.NAME, UserCommandConstants.NUMBER, UserCommandConstants.AGE, UserCommandConstants.GENDER, UserCommandConstants.EDIT_CONTACT, UserCommandConstants.BACK);

    /**
     * Кнопки меню получения всех контактов
     */
    public static final List<String> GET_ALL_CONTACTS_MENU = List.of(
            UserCommandConstants.GET, UserCommandConstants.ADD_FILTER, UserCommandConstants.ADD_ORDER, UserCommandConstants.BACK);

    /**
     * Кнопки меню фильтрации
     */
    public static final List<String> ADD_FILTER_MENU = List.of(
            UserCommandConstants.FILTER_BY_GENDER, UserCommandConstants.FILTER_BY_AGE, UserCommandConstants.BACK_TO_CHOICE);

    /**
     * Кнопки меню фильтрации по полу
     */
    public static final List<String> ADD_FILTER_BY_GENDER_MENU = List.of(
        Gender.MALE.getDisplayName(), Gender.FEMALE.getDisplayName(),
        Gender.NOT_SPECIFIED.getDisplayName(), UserCommandConstants.BACK_TO_CHOICE);

    /**
     * Кнопки меню сортировки у контакта
     */
    public static final List<String> ADD_SORTER_CONTACT_MENU = List.of(
            UserCommandConstants.ORDER_BY_AGE_DESC, UserCommandConstants.ORDER_BY_AGE_ASC,
            UserCommandConstants.ORDER_BY_NAME_ASC, UserCommandConstants.ORDER_BY_NAME_DESC,
            UserCommandConstants.BACK_TO_CHOICE);

    /**
     * Кнопки меню групп
     */
    public static final List<String> GROUPS_MENU = List.of(
            UserCommandConstants.ADD, UserCommandConstants.GET_ALL, UserCommandConstants.FIND, UserCommandConstants.BACK);

    /**
     * Кнопки меню получения всех групп
     */
    public static final List<String> GET_ALL_GROUPS_MENU = List.of(
            UserCommandConstants.GET, UserCommandConstants.SORT, UserCommandConstants.BACK);

    /**
     * Кнопки меню для создания группы
     */
    public static final List<String> ADD_GROUP_MENU = List.of(
            UserCommandConstants.ADD_CONTACT_TO_GROUP, UserCommandConstants.SAVE_GROUP, UserCommandConstants.BACK);

    /**
     * Кнопки меню текущего группы
     */
    public static final List<String> CURRENT_GROUP_MENU = List.of(
            UserCommandConstants.GET_GROUP_CONTACTS, UserCommandConstants.EDIT, UserCommandConstants.DELETE, UserCommandConstants.BACK);

    /**
     * Кнопки меню редактирования группы
     */
    public static final List<String> EDIT_GROUP_MENU = List.of(
            UserCommandConstants.CHANGE_GROUP_NAME, UserCommandConstants.DELETE_CONTACT_FROM_GROUP,
            UserCommandConstants.ADD_CONTACT_TO_GROUP, UserCommandConstants.SAVE_GROUP, UserCommandConstants.BACK);

    /**
     * Кнопки меню сортировки у группы
     */
    public static final List<String> ADD_SORTER_GROUP_MENU = List.of(
            UserCommandConstants.ORDER_BY_NAME_ASC,
            UserCommandConstants.ORDER_BY_NAME_DESC,
            UserCommandConstants.ORDER_BY_PARTICIPANTS_COUNT_DESC,
            UserCommandConstants.ORDER_BY_PARTICIPANTS_COUNT_ASC,
            UserCommandConstants.BACK_TO_CHOICE);

    /**
     * Кнопки выбора пола
     */
    public static final List<String> GENDERS = List.of(
        Gender.MALE.getDisplayName(), Gender.FEMALE.getDisplayName());

    /**
     * Кнопки выбора Да/Нет
     */
    public static final List<String> YES_NO = List.of(
        UserCommandConstants.YES, UserCommandConstants.NO);

    /**
     * Кнопки выбора Да/Нет/Назад к выбору
     */
    public static final List<String> YES_NO_BACK_TO_CHOICE = List.of(
        UserCommandConstants.YES, UserCommandConstants.NO,
        UserCommandConstants.BACK_TO_CHOICE);
}

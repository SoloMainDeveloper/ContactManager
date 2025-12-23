package org.example.entity;

import org.example.constants.ReplyConstants;

/**
 * Dto класса {@link Contact}
 */
public class ContactDto {
    /**
     * Имя контакта
     */
    String name;

    /**
     * Номер телефона
     */
    String phoneNumber;

    /**
     * Возраст
     */
    String age;

    /**
     * Пол
     */
    String gender;

    /**
     * Блокировка
     */
    String isBlocked;

    /**
     * Конструктор, где поля контакта преобразуются в строки
     */
    public ContactDto(Contact contact) {
        name = contact.getName();
        phoneNumber = contact.getPhoneNumber().isEmpty()
                ? ReplyConstants.NOT_SPECIFIED
                : contact.getPhoneNumber();
        gender = contact.getGender().getDisplayName();
        age = contact.getAge() == -1
                ? ReplyConstants.NOT_SPECIFIED
                : String.valueOf(contact.getAge());
        isBlocked = contact.isBlocked()
                ? ReplyConstants.BLOCKED
                : ReplyConstants.NOT_BLOCKED;
    }

    /**
     * Получить имя
     */
    public String getName() {
        return name;
    }

    /**
     * Получить номер телефона
     *
     * @return номер, если его нет, вернется "Не указан"
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Получить возраст
     *
     * @return возраст, если его нет, то вернется "Не указан"
     */
    public String getAge() {
        return age;
    }

    /**
     * Получить пол
     *
     * @return пол, если его нет, то вернется "Не указан"
     */
    public String getGender() {
        return gender;
    }

    /**
     * Получить состояние блокировки
     *
     * @return блокировка, если да, то "Заблокирован", иначе "Не заблокирован"
     */
    public String getIsBlocked() {
        return isBlocked;
    }
}

package org.example.entity;

import java.sql.ResultSet;

/**
 * Контакт
 */
public class Contact {
    /**
     * id чата общения пользователя с ботом
     */
    private Long chatId;

    /**
     * Имя контакта
     */
    private String name;

    /**
     * Номер телефона контакта
     */
    private String phoneNumber;

    /**
     * Возраст контакта
     */
    private int age;

    /**
     * Пол контакта
     */
    private Gender gender;

    /**
     * Заблокирован/не заблокирован контакт
     */
    private Boolean isBlocked;

    /**
     * Конструктор по умолчанию
     */
    public Contact() {}

    /**
     * Конструктор с заполнением всех полей контакта
     */
    public Contact(Long chatId, String name, String phoneNumber, int age, Gender gender, Boolean isBlocked) {
        this.chatId = chatId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.isBlocked = isBlocked;
    }

    public Contact(ResultSet resultSet) {

    }

    /**
     * Возвращает уникальный идентификатор пользователя
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Устанавливает уникальный идентификатор пользователя
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    /**
     * Возвращает имя контакта
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает новое имя контакту
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает номер телефона контакта
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Устанавливает новый номер телефона контакту
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Возвращает возраст контакта
     */
    public int getAge() {
        return age;
    }

    /**
     * Устанавливает возраст контакту
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Возвращает пол контакта
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Устанавливает пол контакту
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }
}

package org.example.entity;

public class ContactInfo {
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
    public ContactInfo(Contact contact) {
        name = contact.getName();
        phoneNumber = contact.getPhoneNumber().isEmpty()
                ? "Не указан"
                : contact.getPhoneNumber();
        gender = "";
        switch (contact.getGender()){
            case Gender.MALE -> gender = "Мужской";
            case Gender.FEMALE -> gender = "Женский";
            case Gender.NOT_SPECIFIED -> gender = "Не указан";
        }
        age = contact.getAge() == -1
                ? "Не указан"
                : String.valueOf(contact.getAge());
        isBlocked = contact.isBlocked()
                ? "Заблокирован"
                : "Не заблокирован";
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

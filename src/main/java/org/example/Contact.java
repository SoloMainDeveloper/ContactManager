package org.example;

/**
 * Контакт
 */
public class Contact {
    private String name;
    private String phoneNumber;
    private int age;
    private Gender gender = Gender.NOT_SPECIFIED;

    /**
     * Вовзращает имя контакта
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
     * Вовзращает номер телефона контакта
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
     * Вовзращает возраст контакта
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
     * Вовзращает пол контакта
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
}

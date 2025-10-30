package org.example.entity;

/**
 * Пол
 */
public enum Gender {
    NOT_SPECIFIED("Не выбрано"),
    MALE("Мужской"),
    FEMALE("Женский");

    /**
     * Строковое значение Gender
     */
    private final String displayName;

    /**
     * Конструктор
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Создаёт Gender на по displayName
     */
    public static Gender fromDisplayName(String displayName) {
        for (Gender gender : values()) {
            if (gender.displayName.equals(displayName)) {
                return gender;
            }
        }
        return NOT_SPECIFIED;
    }
}

package org.example.entity;

/**
 * Пол
 */
public enum Gender {
    NOT_SPECIFIED("Не выбрано"),
    MALE("Мужской"),
    FEMALE("Женский");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromDisplayName(String displayName) {
        for (Gender gender : values()) {
            if (gender.displayName.equals(displayName)) {
                return gender;
            }
        }
        return NOT_SPECIFIED;
    }
}

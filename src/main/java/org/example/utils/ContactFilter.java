package org.example.utils;

/**
 * Фильтр контактов
 *
 * @param property  Свойство фильтра
 * @param condition Условие сравнение фильтра
 * @param value     Значение фильтрации
 */
public record ContactFilter(FilterProperty property, Condition condition, String value) {
    /**
     * Свойство фильтра
     */
    public enum FilterProperty {
        GENDER,
        AGE,
    }

    /**
     * Условие сравнения
     */
    public enum Condition {
        LESS_THAN,
        GREATER_THAN,
        EQUALS,
    }

    @Override
    public FilterProperty property() {
        return property;
    }

    @Override
    public Condition condition() {
        return condition;
    }

    @Override
    public String value() {
        return value;
    }

    /**
     * Получить фильтр контактов, если не нужно применять фильтрацию
     */
    public static ContactFilter none() {
        return new ContactFilter(null, null, null);
    }

    /**
     * Проверить, нужна ли фильтрация контактов
     */
    public boolean isEmpty() {
        return property == null || condition == null || value == null;
    }
}

package org.example.utils;

/**
 * Фильтр контактов
 */
public class ContactFilter {
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

    /**
     * Свойство фильтра
     */
    private final FilterProperty property;

    /**
     * Условие сравнение фильтра
     */
    private final Condition condition;

    /**
     * Значение фильтрации
     */
    private final String value;

    /**
     * Конструктор фильтра контактов, если не нужно применять фильтрацию
     */
    public ContactFilter() {
        this(null, null, null);
    }

    public ContactFilter(FilterProperty property, Condition condition, String value) {
        this.property = property;
        this.condition = condition;
        this.value = value;
    }

    /**
     * Получить свойство фильтра
     */
    public FilterProperty getProperty() {
        return property;
    }

    /**
     * Получить условие сравнение фильтра
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Получить значение фильтрации
     */
    public String getValue() {
        return value;
    }

    /**
     * Проверить, нужна ли фильтрация контактов
     */
    public boolean isEmpty() {
        return property == null || condition == null || value == null;
    }
}
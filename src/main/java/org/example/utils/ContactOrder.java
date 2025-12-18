package org.example.utils;

/**
 * Порядок сортировки контактов
 */
public class ContactOrder {
    /**
     * Свойства порядка сортировки
     */
    public enum OrderProperty {
        AGE,
        NAME,
    }

    /**
     * Направление порядка сортировки
     */
    public enum Direction {
        ASC,
        DESC,
    }

    /**
     * Свойство порядка сортировки
     */
    private final OrderProperty property;

    /**
     * Направление порядка сортировки
     */
    private final Direction direction;

    public ContactOrder(OrderProperty property, Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    /**
     * Получить свойство порядка сортировки
     */
    public OrderProperty getProperty() {
        return property;
    }

    /**
     * Получить направление порядка сортировки
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Получить порядок сортировки контактов, если не нужно применять сортировку
     */
    public static ContactOrder none() {
        return new ContactOrder(null, null);
    }

    /**
     * Проверить, нужна ли сортировка контактов
     */
    public boolean isEmpty() {
        return property == null || direction == null;
    }
}

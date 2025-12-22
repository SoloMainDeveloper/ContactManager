package org.example.utils;

/**
 * Порядок сортировки групп
 */
public class GroupOrder {
    /**
     * Свойства порядка групп
     */
    public enum OrderProperty {
        COUNT,
        NAME,
    }

    /**
     * Направление порядка групп
     */
    public enum Direction {
        ASC,
        DESC,
    }

    /**
     * Свойство порядка групп
     */
    private final OrderProperty property;

    /**
     * Направление порядка сортировки
     */
    private final Direction direction;

    /**
     * Конструктор порядка сортировки групп, если не нужно применять сортировку
     */
    public GroupOrder() {
        this(null, null);
    }

    public GroupOrder(OrderProperty property, Direction direction) {
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
     * Проверить, нужна ли сортировка группам
     */
    public boolean isEmpty() {
        return property == null || direction == null;
    }
}

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
     * Получить порядок сортировки групп, если не нужно применять сортировку
     */
    public static GroupOrder none() {
        return new GroupOrder(null, null);
    }

    /**
     * Проверить, нужна ли сортировка группам
     */
    public boolean isEmpty() {
        return property == null || direction == null;
    }
}

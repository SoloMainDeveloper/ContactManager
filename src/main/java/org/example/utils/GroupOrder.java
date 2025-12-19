package org.example.utils;

/**
 * Порядок сортировки групп
 *
 * @param property  Свойство порядка групп
 * @param direction Направление порядка сортировки
 */
public record GroupOrder(OrderProperty property, Direction direction) {
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

    @Override
    public OrderProperty property() {
        return property;
    }

    @Override
    public Direction direction() {
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

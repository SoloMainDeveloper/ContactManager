package org.example.utils;

/**
 * Порядок сортировки контактов
 *
 * @param property  Свойство порядка сортировки
 * @param direction Направление порядка сортировки
 */
public record ContactOrder(OrderProperty property, Direction direction) {
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

    @Override
    public OrderProperty property() {
        return property;
    }

    @Override
    public Direction direction() {
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

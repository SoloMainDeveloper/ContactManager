package org.example.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Преобразователь поля id-шников контактов группы
 */
public class GroupConverter {
    private static final String DELIMITER = ";";

    /**
     * Преобразовать сет id-шников контактов группы в строку
     */
    public String contactIdsToString(Set<Long> contactIds) {
        return contactIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(DELIMITER));
    }

    /**
     * Преобразовать строку в сет id-шников контактов группы
     */
    public Set<Long> stringToContactIds(String idsString) {
        if (idsString == null || idsString.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(idsString.split(DELIMITER))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }
}

package org.example.response;

/**
 * Документ
 * @param fileName название файла
 * @param content содержимое файла
 */
public record AppDocument(String fileName, String content) {
}
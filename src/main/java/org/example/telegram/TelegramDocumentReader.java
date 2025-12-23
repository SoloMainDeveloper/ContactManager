package org.example.telegram;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Читает документы, отправленные через телеграм
 */
@Component
public class TelegramDocumentReader {
    /**
     * Токен от телеграм-бота
     */
    @Value("${api.telegram.bot.token}")
    private String botToken;

    /**
     * URL информации о файле
     */
    @Value("${api.telegram.bot.file_info.uri}")
    private String fileInfoUri;

    /**
     * URL загрузки файла
     */
    @Value("${api.telegram.bot.file_storage.uri}")
    private String fileStorageUri;

    /**
     * Прочитать документ
     * @param fileId идентификатор файла
     * @return содержимое файла
     */
    public String read(String fileId) throws TelegramApiException {
        ResponseEntity<String> response = getResponse(fileId);
        if(response.getStatusCode() == HttpStatus.OK) {
            try {
                String filePath = getFilePathFromResponse(response);
                byte[] bytes = downloadFile(filePath);
                return new String(bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new TelegramApiException("Ошибка при чтении файла: ", e);
            }
        }
        throw new TelegramApiException(
                "Ошибка при чтении файла. Статус-код = " + response.getStatusCode());
    }

    /**
     * Возвращает {@link ResponseEntity} по полученному fileId
     */
    private ResponseEntity<String> getResponse(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(new HttpHeaders());

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                botToken,
                fileId
        );
    }

    /**
     * По заданному URL считывает содержимое
     * @throws Exception если не удалось прочитать содержимое
     */
    private byte[] downloadFile(String filePath) throws Exception {
        String fullUri = fileStorageUri.replace("{token}", botToken)
                .replace("{filePath}", filePath);
        URL urlObject = new URI(fullUri).toURL();

        try (InputStream is = urlObject.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Не удалось прочитать содержимое файла", e);
        }
    }

    /**
     * Получить путь к файлу из ответа
     * @throws IllegalArgumentException если тело ответа пустое
     */
    private String getFilePathFromResponse(ResponseEntity<String> response)
            throws IllegalArgumentException {
        String body = response.getBody();
        if(body == null) {
            throw new IllegalArgumentException("Пустое тело ответа");
        }
        JSONObject jsonObject = new JSONObject(body);
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }
}

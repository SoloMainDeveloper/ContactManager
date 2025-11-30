package org.example.utils.telegram;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
    public String read(String fileId) {
        ResponseEntity<String> resp = getFilePath(fileId);
        if(resp.getStatusCode() == HttpStatus.OK) {
            String filePath = getFilePath(resp);
            try {
                byte[] bytes = downloadFile(filePath);
                return new String(bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null; //TODO поменять на Exception наверное
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                botToken,
                fileId
        );
    }

    private byte[] downloadFile(String filePath) throws Exception {
        var fullUri = fileStorageUri.replace("{token}", botToken)
                .replace("{filePath}", filePath);
        URL urlObject;
        try {
            urlObject = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new Exception(e);
        }

        try (InputStream is = urlObject.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new Exception(urlObject.toExternalForm(), e);
        }
    }

    private String getFilePath(ResponseEntity<String> response) {
        var jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }
}

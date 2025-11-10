package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Конфигурация для подключения к базе данных
 */
@Component
public class DataBaseConfig {
    /**
     * Ссылка на базу данных
     */
    @Value("${datasource.url}")
    private String dataSourceUrl;

    /**
     * Имя пользователя базы данных
     */
    @Value("${datasource.username}")
    private String dataSourceUsername;

    /**
     * Пароль от базы данных
     */
    @Value("${datasource.password}")
    private String dataSourcePassword;

    /**
     * Возвращает ссылку на базу данных
     */
    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    /**
     * Возвращает имя пользователя базы данных
     */
    public String getDataSourceUsername() {
        return dataSourceUsername;
    }

    /**
     * Возвращает пароль от базы данных
     */
    public String getDataSourcePassword() {
        return dataSourcePassword;
    }
}

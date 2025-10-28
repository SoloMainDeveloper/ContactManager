package org.example;


import org.postgresql.ds.PGSimpleDataSource;

import java.io.InputStream;
import java.util.Properties;

/**
 * База данных для работы с PostgreSQL
 */
public class DataBase {
    /**
     * Набор свойств, состоящий из пар ключ-значение
     */
    private final Properties properties;

    /**
     * Конструктор по умолчанию, заполняет свойства БД из config.properties
     */
    public DataBase() {
        properties = readPropertiesFromConfig();
    }

    /**
     * Создает и настраивает источник данных для подключения к БД
     */
    public PGSimpleDataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("datasource.url"));
        dataSource.setUser(properties.getProperty("datasource.username"));
        dataSource.setPassword(properties.getProperty("datasource.password"));

        dataSource.setSsl(true);
        dataSource.setSslMode("verify-full");
        dataSource.setSslRootCert("root.crt");

        return dataSource;
    }

    /**
     * Читает свойства из config.properties для подключения к БД
     */
    private Properties readPropertiesFromConfig() {
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать config.properties", e);
        }
    }
}

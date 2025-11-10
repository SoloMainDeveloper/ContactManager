package org.example;

import org.example.config.DataBaseConfig;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * База данных для работы с PostgreSQL
 */
@Component
public class DataBase {
    /**
     * Конфигурация для подключения к базе данных
     */
    private final DataBaseConfig config;

    /**
     * Конструктор
     * @param config содержит данные для подключения
     */
    public DataBase(DataBaseConfig config){
        this.config = config;
    }

    /**
     * Создает и настраивает источник данных для подключения к БД
     */
    @Bean
    public PGSimpleDataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(config.getDataSourceUrl());
        dataSource.setUser(config.getDataSourceUsername());
        dataSource.setPassword(config.getDataSourcePassword());

        dataSource.setSsl(true);
        dataSource.setSslMode("verify-full");
        dataSource.setSslRootCert("root.crt");

        return dataSource;
    }
}

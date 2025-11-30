package org.example.service;

import org.example.utils.exporters.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис экспорта контактов
 */
@Service
public class ExportService {
    /**
     * Экспортёры файлов различных форматов
     */
    private final Map<String, Exporter> exporters;

    /**
     * Конструктор
     */
    @Autowired
    public ExportService(List<Exporter> exporters) {
        this.exporters = exporters.stream().collect(
                Collectors.toMap(
                        Exporter::getSupportableFormat,
                        Function.identity()
                ));
    }

    /**
     * Экспортирует контакты из файла
     */
    public void exportContacts() {

    }
}

package org.example.utils.exporters;

import org.springframework.stereotype.Component;

/**
 * Экспортёр контактов в CSV
 */
@Component
public class ExporterCSV implements Exporter {
    @Override
    public void exportContacts() {

    }

    @Override
    public String getSupportableFormat() {
        return "csv";
    }
}

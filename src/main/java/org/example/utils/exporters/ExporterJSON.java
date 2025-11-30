package org.example.utils.exporters;

import org.springframework.stereotype.Component;

/**
 * Экспортёр контактов в JSON
 */
@Component
public class ExporterJSON implements Exporter {
    @Override
    public void exportContacts() {

    }

    @Override
    public String getSupportableFormat() {
        return "json";
    }
}

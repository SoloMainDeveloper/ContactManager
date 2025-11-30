package org.example.utils.exporters;

import org.springframework.stereotype.Component;

/**
 * Экспортёр контактов в TXT
 */
@Component
public class ExporterTXT implements Exporter {
    @Override
    public void exportContacts() {

    }

    @Override
    public String getSupportableFormat() {
        return "txt";
    }
}

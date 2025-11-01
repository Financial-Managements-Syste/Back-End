package com.example.savving_service.entity.sqlite;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return attribute != null ? attribute.format(formatter) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return dbData != null ? LocalDate.parse(dbData, formatter) : null;
    }
}

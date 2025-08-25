package br.dev.rodrigopinheiro.B3DataManager.domain.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {

    private static final String PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static LocalDate parse(String dateStr) {
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data no formato inválido: " + dateStr, e);
        }
    }

    public static String format(LocalDate date) {
        return date != null ? date.format(FORMATTER) : "";
    }
}

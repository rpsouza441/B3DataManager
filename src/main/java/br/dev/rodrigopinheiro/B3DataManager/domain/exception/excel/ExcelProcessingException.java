package br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel;

public class ExcelProcessingException extends RuntimeException {
    public ExcelProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

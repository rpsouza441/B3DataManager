package br.dev.rodrigopinheiro.B3DataManager.infrastructure.excel.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.InvalidDataException;
import br.dev.rodrigopinheiro.B3DataManager.domain.util.DateFormatter;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

public class ExcelRowToOperacaoMapper {

    private final MessageSource messageSource;
    private final DataFormatter dataFormatter;

    public ExcelRowToOperacaoMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.dataFormatter = new DataFormatter();
    }

    public Operacao map(Row row, Locale locale) {
        // Valida os campos obrigatórios
        validarCampoObrigatorio(row.getCell(0), "entrada_saida", row.getRowNum(), locale);
        validarCampoObrigatorio(row.getCell(1), "data", row.getRowNum(), locale);
        validarCampoObrigatorio(row.getCell(2), "movimentacao", row.getRowNum(), locale);
        validarCampoObrigatorio(row.getCell(3), "produto", row.getRowNum(), locale);
        validarCampoObrigatorio(row.getCell(4), "instituicao", row.getRowNum(), locale);

        Operacao operacao = new Operacao();

        // Processamento e normalização da coluna "entrada_saida"
        String entradaSaida = getStringValue(row.getCell(0));
        if (entradaSaida.equalsIgnoreCase("credito") || entradaSaida.equalsIgnoreCase("crédito")) {
            operacao.setEntradaSaida("Entrada");
        } else if (entradaSaida.equalsIgnoreCase("debito") || entradaSaida.equalsIgnoreCase("débito")) {
            operacao.setEntradaSaida("Saída");
        } else {
            operacao.setEntradaSaida(entradaSaida);
        }

        operacao.setData(getLocalDateValue(row.getCell(1)));
        operacao.setMovimentacao(getStringValue(row.getCell(2)));
        operacao.setProduto(getStringValue(row.getCell(3)));
        operacao.setInstituicao(getStringValue(row.getCell(4)));

        operacao.setQuantidade(getNullableDoubleValue(row.getCell(5)));
        operacao.setPrecoUnitario(getNullableBigDecimalValue(row.getCell(6)));
        operacao.setValorOperacao(getNullableBigDecimalValue(row.getCell(7)));

        return operacao;
    }

    private void validarCampoObrigatorio(Cell cell, String campo, int linha, Locale locale) {
        if (cell == null || dataFormatter.formatCellValue(cell).trim().isEmpty()) {
            throw new InvalidDataException("excel.field.required", messageSource, new Object[]{campo, linha});
        }
    }

    private String getStringValue(Cell cell) {
        return cell != null ? dataFormatter.formatCellValue(cell).trim() : "";
    }

    private Double getNullableDoubleValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        return getDoubleValue(cell);
    }

    private Double getDoubleValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else {
            try {
                String value = getStringValue(cell);
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private BigDecimal getNullableBigDecimalValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        return getBigDecimalValue(cell);
    }

    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } else {
            try {
                String value = getStringValue(cell);
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
    }

    private LocalDate getLocalDateValue(Cell cell) {
        if (cell == null) {
            throw new InvalidDataException("Data é obrigatória", messageSource, new Object[]{"data", cell.getRowIndex()});
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            // Se a célula é numérica e formatada como data
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else {
            // Converte a String utilizando o DateFormatter
            String dataStr = dataFormatter.formatCellValue(cell).trim();
            return DateFormatter.parse(dataStr);
        }
    }

}

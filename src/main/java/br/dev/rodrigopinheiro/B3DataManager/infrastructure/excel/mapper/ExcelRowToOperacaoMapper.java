package br.dev.rodrigopinheiro.B3DataManager.infrastructure.excel.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.InvalidDataException;
import br.dev.rodrigopinheiro.B3DataManager.domain.util.DateFormatter;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapper responsável por converter linhas do Excel em entidades Operacao.
 * 
 * <p>Atualizado para não depender de MessageSource, seguindo princípios
 * da arquitetura hexagonal e simplificando o tratamento de exceções.</p>
 * 
 * @deprecated Esta classe será removida quando a migração para ImportExcelUseCase for completa
 */
@Deprecated
public class ExcelRowToOperacaoMapper {

    private final DataFormatter dataFormatter;

    public ExcelRowToOperacaoMapper() {
        this.dataFormatter = new DataFormatter();
    }
    
    /**
     * Construtor mantido para compatibilidade (ignora MessageSource).
     * 
     * @param messageSource Ignorado - mantido apenas para compatibilidade
     * @deprecated Use o construtor sem parâmetros
     */
    @Deprecated
    public ExcelRowToOperacaoMapper(Object messageSource) {
        this();
    }

    /**
     * Converte uma linha do Excel em entidade Operacao.
     * 
     * @param row Linha do Excel
     * @param locale Locale (mantido para compatibilidade, mas não usado)
     * @return Entidade Operacao mapeada
     * @deprecated Use ImportExcelUseCase para processamento completo
     */
    @Deprecated
    public Operacao map(Row row, java.util.Locale locale) {
        return map(row);
    }
    
    /**
     * Converte uma linha do Excel em entidade Operacao.
     * 
     * @param row Linha do Excel
     * @return Entidade Operacao mapeada
     */
    public Operacao map(Row row) {
        // Valida os campos obrigatórios
        validarCampoObrigatorio(row.getCell(0), "Entrada/Saída", row.getRowNum());
        validarCampoObrigatorio(row.getCell(1), "Data", row.getRowNum());
        validarCampoObrigatorio(row.getCell(2), "Movimentação", row.getRowNum());
        validarCampoObrigatorio(row.getCell(3), "Produto", row.getRowNum());
        validarCampoObrigatorio(row.getCell(4), "Instituição", row.getRowNum());

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

    /**
     * Valida se um campo obrigatório está preenchido.
     * 
     * @param cell Célula do Excel
     * @param campo Nome do campo para mensagem de erro
     * @param linha Número da linha para mensagem de erro
     */
    private void validarCampoObrigatorio(Cell cell, String campo, int linha) {
        if (cell == null || dataFormatter.formatCellValue(cell).trim().isEmpty()) {
            throw new InvalidDataException(
                String.format("Campo '%s' é obrigatório na linha %d", campo, linha + 1)
            );
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

    /**
     * Extrai valor de data de uma célula do Excel.
     * 
     * @param cell Célula contendo a data
     * @return LocalDate extraído da célula
     */
    private LocalDate getLocalDateValue(Cell cell) {
        if (cell == null) {
            throw new InvalidDataException(
                String.format("Data é obrigatória na linha %d", cell != null ? cell.getRowIndex() + 1 : 0)
            );
        }
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                // Se a célula é numérica e formatada como data
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else {
                // Converte a String utilizando o DateFormatter
                String dataStr = dataFormatter.formatCellValue(cell).trim();
                return DateFormatter.parse(dataStr);
            }
        } catch (Exception e) {
            throw new InvalidDataException(
                String.format("Data inválida na linha %d: %s", 
                    cell.getRowIndex() + 1, 
                    dataFormatter.formatCellValue(cell))
            );
        }
    }

}

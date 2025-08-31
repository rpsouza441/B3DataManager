package br.dev.rodrigopinheiro.B3DataManager.infrastructure.excel;

import br.dev.rodrigopinheiro.B3DataManager.application.service.OperacaoService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.InvalidDataException;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.TransacaoFactory;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.excel.mapper.ExcelRowToOperacaoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class ExcelFileImporter {

    private final OperacaoService operacaoService;
    private final MessageSource messageSource;
    private final ExcelRowToOperacaoMapper mapper;


    public ExcelFileImporter(OperacaoService operacaoService, MessageSource messageSource, TransacaoFactory transacaoFactory) {
        this.operacaoService = operacaoService;
        this.messageSource = messageSource;
        this.mapper = new ExcelRowToOperacaoMapper(messageSource);
    }

    /**
     * Processa o arquivo Excel, mapeando e validando os dados, delegando a persistência ao serviço de operações.
     *
     * @param inputStream Fluxo de entrada do arquivo Excel.
     * @param userId      ID do usuário logado.
     * @param locale      Localização para mensagens.
     * @return Lista de mensagens de erro por linha.
     */
    public List<String> processFile(InputStream inputStream, Long userId, Locale locale) {
        log.info(messageSource.getMessage("excel.processing.start", null, locale));

        List<String> errorRows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorar cabeçalho

                try {
                    // Mapeia a linha para a entidade Operacao
                    Operacao operacao = mapper.map(row, locale);

                    // Inicializa campos padrão
                    operacao.setDuplicado(false);
                    operacao.setDeletado(false);

                    // Aplica regras de negócio: verificação de duplicidade e associação do usuário
                    operacaoService.verificarDuplicidade(operacao);
                    operacao.setUsuario(operacaoService.buscarUsuarioPorId(userId));

                    // Processa a operação em uma transação separada
                   processOperacao(operacao);


                } catch (InvalidDataException e) {
                    log.warn(messageSource.getMessage("excel.processing.row.error",
                            new Object[]{row.getRowNum(), e.getMessage()}, locale));
                    errorRows.add(formatErrorRow(row, e.getMessage()));
                } catch (Exception e) {
                    log.error(messageSource.getMessage("excel.processing.error", null, locale), e);
                    errorRows.add(formatErrorRow(row, e.getMessage()));
                }
            }
        } catch (Exception e) {
            log.error(messageSource.getMessage("excel.processing.error", null, locale), e);
            throw new ExcelProcessingException(messageSource.getMessage("excel.processing.error", null, locale), e);
        }

        return errorRows;
    }

    /**
     * Processa a operação com isolamento transacional.
     * Pode ser anotado com REQUIRES_NEW para garantir que cada linha seja processada em sua própria transação.
     */
    @Transactional
    public Operacao processOperacao(Operacao operacao) {
        return operacaoService.processarOperacao(operacao);
    }

    /**
     * Gera um arquivo Excel com as linhas de erro.
     *
     * @param errors Lista de mensagens de erro.
     * @param locale Localização para mensagens.
     * @return Fluxo de entrada do arquivo Excel gerado.
     */
    public ByteArrayInputStream generateErrorFile(List<String> errors, Locale locale) {
        log.info(messageSource.getMessage("excel.error.file.start", null, locale));
        try (Workbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Erros");
            int rowIndex = 0;
            for (String error : errors) {
                var row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(error);
            }

            ByteArrayInputStream inputStream;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                log.info(messageSource.getMessage("excel.error.file.finish", null, locale));
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            }
            return inputStream;
        } catch (Exception e) {
            log.error(messageSource.getMessage("excel.error.file.error", null, locale), e);
            throw new ExcelProcessingException(messageSource.getMessage("excel.error.file.error", null, locale), e);
        }
    }

    private String formatErrorRow(Row row, String errorMessage) {
        return "Erro na linha " + row.getRowNum() + ": " + errorMessage;
    }
}

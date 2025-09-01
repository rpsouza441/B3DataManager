package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GetOperacoesForBatchCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.GetOperacoesForBatchResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use Case responsável por buscar operações para processamento em batch.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Buscar operações com critérios específicos para batch</li>
 *   <li>Implementar paginação para processamento eficiente</li>
 *   <li>Fornecer informações de controle para o Spring Batch</li>
 *   <li>Garantir performance adequada para grandes volumes</li>
 * </ul>
 * 
 * <h3>Casos de Uso Típicos:</h3>
 * <ul>
 *   <li>Spring Batch ItemReader para processamento de operações</li>
 *   <li>Relatórios em lote de operações não processadas</li>
 *   <li>Sincronização de dados com sistemas externos</li>
 *   <li>Processamento de dimensionamento em massa</li>
 * </ul>
 * 
 * <h3>Performance:</h3>
 * <ul>
 *   <li>Transação read-only para otimização</li>
 *   <li>Paginação configurável para controle de memória</li>
 *   <li>Índices otimizados para campos dimensionado e duplicado</li>
 *   <li>Timeout configurado para evitar travamentos</li>
 * </ul>
 * 
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Buscar primeira página de operações para batch
 * GetOperacoesForBatchCommand command = GetOperacoesForBatchCommand.forDefaultBatch(100, 0);
 * GetOperacoesForBatchResult result = getOperacoesForBatchUseCase.execute(command);
 * 
 * while (result.hasOperacoes()) {
 *     // Processar operações da página atual
 *     processOperacoes(result.operacoes());
 *     
 *     // Buscar próxima página se disponível
 *     if (result.hasNext()) {
 *         command = GetOperacoesForBatchCommand.forDefaultBatch(100, 
 *                   (result.currentPage() + 1) * 100);
 *         result = getOperacoesForBatchUseCase.execute(command);
 *     } else {
 *         break;
 *     }
 * }
 * }</pre>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
@Component
public class GetOperacoesForBatchUseCase {
    
    private final OperacaoRepository operacaoRepository;
    
    public GetOperacoesForBatchUseCase(OperacaoRepository operacaoRepository) {
        this.operacaoRepository = operacaoRepository;
    }
    
    /**
     * Executa a busca de operações para processamento em batch.
     * 
     * @param command Comando com critérios de busca e paginação
     * @return Resultado com operações encontradas e informações de paginação
     * @throws IllegalArgumentException se os parâmetros do comando forem inválidos
     */
    @Transactional(readOnly = true, timeout = 60) // 1 minuto para buscas grandes
    public GetOperacoesForBatchResult execute(GetOperacoesForBatchCommand command) {
        log.debug("Buscando operações para batch: dimensionado={}, duplicado={}, pageSize={}, offset={}",
                command.dimensionado(), command.duplicado(), command.pageSize(), command.offset());
        
        try {
            // Buscar operações com paginação
            List<Operacao> operacoes = operacaoRepository.findByDimensionadoAndDuplicadoWithPagination(
                command.dimensionado(),
                command.duplicado(),
                command.pageSize(),
                command.offset()
            );
            
            // Contar total de elementos (para controle de paginação)
            long totalElements = operacaoRepository.countByDimensionadoAndDuplicado(
                command.dimensionado(),
                command.duplicado()
            );
            
            // Calcular se há próxima página
            boolean hasNext = (command.offset() + command.pageSize()) < totalElements;
            
            // Calcular página atual
            int currentPage = command.offset() / command.pageSize();
            
            log.debug("Busca concluída: {} operações encontradas, total={}, hasNext={}, page={}",
                    operacoes.size(), totalElements, hasNext, currentPage);
            
            return new GetOperacoesForBatchResult(
                operacoes,
                totalElements,
                hasNext,
                currentPage
            );
            
        } catch (Exception e) {
            log.error("Erro ao buscar operações para batch: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao buscar operações para batch", e);
        }
    }
}
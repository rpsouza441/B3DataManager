package br.dev.rodrigopinheiro.B3DataManager.infrastructure.batch;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GetOperacoesForBatchCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.GetOperacoesForBatchResult;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.GetOperacoesForBatchUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;

/**
 * ItemReader customizado que usa arquitetura hexagonal para buscar operações.
 * 
 * <p>Este reader implementa paginação eficiente usando Use Cases ao invés de
 * acessar diretamente repositórios JPA, mantendo a arquitetura limpa.</p>
 * 
 * <h3>Funcionalidades:</h3>
 * <ul>
 *   <li>Paginação automática com tamanho configurável</li>
 *   <li>Busca operações não dimensionadas e não duplicadas</li>
 *   <li>Integração com arquitetura hexagonal</li>
 *   <li>Logging detalhado para monitoramento</li>
 * </ul>
 * 
 * <h3>Performance:</h3>
 * <ul>
 *   <li>Carregamento sob demanda (lazy loading)</li>
 *   <li>Controle de memória via paginação</li>
 *   <li>Reutilização de conexões de banco</li>
 * </ul>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
public class CustomOperacaoItemReader implements ItemReader<Operacao> {
    
    private final GetOperacoesForBatchUseCase getOperacoesForBatchUseCase;
    private final int pageSize;
    private final boolean dimensionado;
    private final boolean duplicado;
    
    private Iterator<Operacao> currentPageIterator;
    private int currentOffset = 0;
    private boolean hasMorePages = true;
    
    /**
     * Construtor do ItemReader customizado.
     * 
     * @param getOperacoesForBatchUseCase Use Case para buscar operações
     * @param pageSize Tamanho da página para paginação
     * @param dimensionado Filtro para operações dimensionadas
     * @param duplicado Filtro para operações duplicadas
     */
    public CustomOperacaoItemReader(
            GetOperacoesForBatchUseCase getOperacoesForBatchUseCase,
            int pageSize,
            boolean dimensionado,
            boolean duplicado) {
        this.getOperacoesForBatchUseCase = getOperacoesForBatchUseCase;
        this.pageSize = pageSize;
        this.dimensionado = dimensionado;
        this.duplicado = duplicado;
        
        log.info("CustomOperacaoItemReader inicializado: pageSize={}, dimensionado={}, duplicado={}",
                pageSize, dimensionado, duplicado);
    }
    
    /**
     * Construtor padrão para operações não dimensionadas e não duplicadas.
     */
    public CustomOperacaoItemReader(GetOperacoesForBatchUseCase getOperacoesForBatchUseCase, int pageSize) {
        this(getOperacoesForBatchUseCase, pageSize, false, false);
    }
    
    @Override
    public Operacao read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        
        // Se não há iterator atual ou está vazio, carrega próxima página
        if (currentPageIterator == null || !currentPageIterator.hasNext()) {
            if (!hasMorePages) {
                log.info("Leitura concluída. Total de registros processados: {}", currentOffset);
                return null; // Fim da leitura
            }
            
            loadNextPage();
        }
        
        // Retorna próximo item da página atual
        if (currentPageIterator != null && currentPageIterator.hasNext()) {
            Operacao operacao = currentPageIterator.next();
            log.debug("Lendo operação ID: {}", operacao.getId());
            return operacao;
        }
        
        return null;
    }
    
    /**
     * Carrega a próxima página de operações.
     */
    private void loadNextPage() {
        log.debug("Carregando página: offset={}, pageSize={}", currentOffset, pageSize);
        
        try {
            GetOperacoesForBatchCommand command = new GetOperacoesForBatchCommand(
                dimensionado, duplicado, pageSize, currentOffset
            );
            
            GetOperacoesForBatchResult result = getOperacoesForBatchUseCase.execute(command);
            
            List<Operacao> operacoes = result.operacoes();
            
            if (operacoes.isEmpty()) {
                log.info("Nenhuma operação encontrada na página atual. Finalizando leitura.");
                hasMorePages = false;
                currentPageIterator = null;
                return;
            }
            
            currentPageIterator = operacoes.iterator();
            currentOffset += pageSize;
            hasMorePages = result.hasNext();
            
            log.debug("Página carregada: {} operações, hasNext={}", operacoes.size(), hasMorePages);
            
        } catch (Exception e) {
            log.error("Erro ao carregar página de operações: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao carregar dados para processamento batch", e);
        }
    }
}
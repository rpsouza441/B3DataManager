package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CheckDuplicateCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.CheckDuplicateResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario.UsuarioNaoAutorizadoException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use Case responsável por verificar duplicidade de operações durante importação.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Verificar se uma operação já existe no sistema</li>
 *   <li>Comparar por: data, movimentação, produto, instituição, quantidade, preço e valor</li>
 *   <li>Retornar informações sobre duplicidade e ID da operação original</li>
 *   <li>Garantir que a verificação seja feita apenas para o usuário proprietário</li>
 * </ul>
 * 
 * <h3>Regras de Negócio:</h3>
 * <ul>
 *   <li>Operações duplicadas devem ser identificadas com precisão</li>
 *   <li>Verificação deve considerar apenas operações do mesmo usuário</li>
 *   <li>Operações já marcadas como duplicadas não devem ser consideradas como originais</li>
 *   <li>Comparação de valores monetários deve considerar precisão decimal</li>
 * </ul>
 * 
 * <h3>Tratamento de Erros:</h3>
 * <ul>
 *   <li>Dados inválidos: IllegalArgumentException</li>
 *   <li>Usuário não autorizado: UsuarioNaoAutorizadoException</li>
 *   <li>Erro de banco: RuntimeException com causa específica</li>
 *   <li>Timeout de consulta: RuntimeException com timeout</li>
 * </ul>
 * 
 * <h3>Performance:</h3>
 * <ul>
 *   <li>Utiliza índices otimizados no banco de dados</li>
 *   <li>Query limitada por usuário para melhor performance</li>
 *   <li>Timeout configurado para evitar travamentos (30 segundos)</li>
 *   <li>Transação read-only para otimização</li>
 * </ul>
 * 
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * CheckDuplicateCommand command = new CheckDuplicateCommand(
 *     LocalDate.of(2025, 8, 15),
 *     "Compra",
 *     "PETR4 - PETROBRAS",
 *     "XP Investimentos",
 *     new BigDecimal("100"),
 *     new BigDecimal("25.50"),
 *     new BigDecimal("2550.00"),
 *     new UsuarioId(1L)
 * );
 * 
 * CheckDuplicateResult result = checkDuplicateUseCase.execute(command);
 * 
 * if (result.isDuplicate()) {
 *     log.info("Operação duplicada encontrada. ID original: {}", result.originalId());
 * }
 * }</pre>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
@Component
public class CheckDuplicateOperacaoUseCase {
    
    private final OperacaoRepository operacaoRepository;
    
    public CheckDuplicateOperacaoUseCase(OperacaoRepository operacaoRepository) {
        this.operacaoRepository = operacaoRepository;
    }
    
    /**
     * Executa a verificação de duplicidade de uma operação.
     * 
     * @param command Comando contendo os dados da operação a ser verificada
     * @return Resultado indicando se a operação é duplicada e o ID da operação original
     * @throws IllegalArgumentException se os dados do comando forem inválidos
     * @throws UsuarioNaoAutorizadoException se o usuário não for válido
     * @throws RuntimeException se houver erro de acesso aos dados
     */
    @Transactional(readOnly = true, timeout = 30)
    public CheckDuplicateResult execute(CheckDuplicateCommand command) {
        log.debug("Iniciando verificação de duplicidade para operação: data={}, produto={}, usuário={}", 
                 command.data(), command.produto(), command.usuarioId().value());
        
        try {
            // Validação adicional dos dados
            validateCommand(command);
            
            // Busca por operação duplicada
            Optional<Operacao> duplicateOperacao = findDuplicateOperacao(command);
            
            if (duplicateOperacao.isPresent()) {
                Long originalId = duplicateOperacao.get().getId();
                log.info("Operação duplicada encontrada. ID original: {}, Produto: {}, Data: {}", 
                        originalId, command.produto(), command.data());
                return CheckDuplicateResult.duplicate(originalId);
            } else {
                log.debug("Nenhuma operação duplicada encontrada para: produto={}, data={}", 
                         command.produto(), command.data());
                return CheckDuplicateResult.notDuplicate();
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos para verificação de duplicidade: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Erro de acesso aos dados durante verificação de duplicidade: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao verificar duplicidade da operação", e);
        } catch (Exception e) {
            log.error("Erro inesperado durante verificação de duplicidade: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao verificar duplicidade da operação", e);
        }
    }
    
    /**
     * Valida os dados do comando.
     */
    private void validateCommand(CheckDuplicateCommand command) {
        if (command.usuarioId().value() <= 0) {
            throw new UsuarioNaoAutorizadoException("ID do usuário deve ser positivo");
        }
        
        if (command.produto() == null || command.produto().trim().isEmpty()) {
            throw new IllegalArgumentException("Produto é obrigatório para verificação de duplicidade");
        }
        
        if (command.instituicao() == null || command.instituicao().trim().isEmpty()) {
            throw new IllegalArgumentException("Instituição é obrigatória para verificação de duplicidade");
        }
        
        if (command.movimentacao() == null || command.movimentacao().trim().isEmpty()) {
            throw new IllegalArgumentException("Movimentação é obrigatória para verificação de duplicidade");
        }
    }
    
    /**
     * Busca por operação duplicada no repositório.
     */
    private Optional<Operacao> findDuplicateOperacao(CheckDuplicateCommand command) {
        try {
            return operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                command.data(),
                command.movimentacao(),
                command.produto(),
                command.instituicao(),
                command.quantidade(),
                command.precoUnitario(),
                command.valorOperacao(),
                false, // Não considerar operações já marcadas como duplicadas
                command.usuarioId()
            );
        } catch (DataAccessException e) {
            log.error("Erro ao consultar operações duplicadas no banco de dados: {}", e.getMessage(), e);
            throw e;
        }
    }
}
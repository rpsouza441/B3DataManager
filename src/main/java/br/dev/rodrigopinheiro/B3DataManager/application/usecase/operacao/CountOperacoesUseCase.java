package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CountOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case para contar operações com filtros.
 * Garante ownership obrigatório para segurança.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CountOperacoesUseCase {
    
    private final OperacaoRepository operacaoRepository;
    
    public CountOperacoesUseCase(OperacaoRepository operacaoRepository) {
        this.operacaoRepository = operacaoRepository;
    }
    
    /**
     * Executa a contagem de operações com filtros.
     * 
     * @param command Comando contendo filtros e usuário
     * @return Quantidade de operações que atendem aos critérios
     * @throws IllegalArgumentException se os dados do comando forem inválidos
     * @throws RuntimeException se houver erro de acesso aos dados
     */
    public long execute(CountOperacoesCommand command) {
        log.debug("Iniciando contagem de operações para usuário: {}", command.usuarioId());
        
        try {
            // Validação básica
            validateCommand(command);
            
            UsuarioId usuarioId = new UsuarioId(command.usuarioId());
            
            FilterCriteria criteria = new FilterCriteria(
                command.entradaSaida(),
                command.startDate(),
                command.endDate(),
                command.movimentacao(),
                command.produto(),
                command.instituicao(),
                command.duplicado(),
                command.dimensionado()
            );
            
            // Contagem com ownership obrigatório
            long count = operacaoRepository.countByFiltersAndUsuarioId(criteria, usuarioId);
            
            log.debug("Contagem concluída. Total de operações: {}", count);
            return count;
            
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos para contagem de operações: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Erro de acesso aos dados durante contagem de operações: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao contar operações", e);
        } catch (Exception e) {
            log.error("Erro inesperado durante contagem de operações: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao contar operações", e);
        }
    }
    
    /**
     * Valida os dados do comando.
     */
    private void validateCommand(CountOperacoesCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        if (command.usuarioId() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
    }
}
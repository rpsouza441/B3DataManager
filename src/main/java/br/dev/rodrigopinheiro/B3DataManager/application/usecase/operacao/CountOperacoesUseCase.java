package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CountOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case para contar operações com filtros.
 * Garante ownership obrigatório para segurança.
 */
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
     */
    public long execute(CountOperacoesCommand command) {
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
        return operacaoRepository.countByFiltersAndUsuarioId(criteria, usuarioId);
    }
}
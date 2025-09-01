package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ListOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ListOperacoesResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.OperacaoDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.mapper.OperacaoDTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use Case para listar operações com filtros e paginação.
 * Garante ownership obrigatório para segurança.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ListOperacoesUseCase {
    
    private final OperacaoRepository operacaoRepository;
    private final OperacaoDTOMapper mapper;
    
    public ListOperacoesUseCase(OperacaoRepository operacaoRepository, OperacaoDTOMapper mapper) {
        this.operacaoRepository = operacaoRepository;
        this.mapper = mapper;
    }
    
    /**
     * Executa a listagem de operações com filtros e paginação.
     * 
     * @param command Comando contendo filtros, paginação e usuário
     * @return Resultado com operações e informações de paginação
     * @throws IllegalArgumentException se os dados do comando forem inválidos
     * @throws RuntimeException se houver erro de acesso aos dados
     */
    public ListOperacoesResult execute(ListOperacoesCommand command) {
        log.debug("Iniciando listagem de operações para usuário: {}, página: {}, tamanho: {}",
                 command.usuarioId(), command.page(), command.size());
        
        try {
            // Validação básica
            validateCommand(command);
            
            UsuarioId usuarioId = new UsuarioId(command.usuarioId());
            
            // Criar critérios de filtro
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
            
            Pageable pageable = PageRequest.of(command.page(), command.size());
            
            // Busca com ownership obrigatório
            Page<Operacao> page = operacaoRepository.findByFiltersAndUsuarioId(
                criteria, usuarioId, pageable);
            
            // Converter para DTOs
            List<OperacaoDTO> operacaoDTOs = page.getContent()
                .stream()
                .map(mapper::toDTO)
                .toList();
            
            log.debug("Listagem concluída. Encontradas {} operações de {} total",
                     page.getNumberOfElements(), page.getTotalElements());
            
            return new ListOperacoesResult(
                operacaoDTOs,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos para listagem de operações: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Erro de acesso aos dados durante listagem de operações: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao listar operações", e);
        } catch (Exception e) {
            log.error("Erro inesperado durante listagem de operações: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao listar operações", e);
        }
    }
    
    /**
     * Valida os dados do comando.
     */
    private void validateCommand(ListOperacoesCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        if (command.usuarioId() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        
        if (command.page() < 0) {
            throw new IllegalArgumentException("Número da página deve ser maior ou igual a zero");
        }
        
        if (command.size() <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser maior que zero");
        }
        
        if (command.size() > 1000) {
            throw new IllegalArgumentException("Tamanho da página não pode exceder 1000 itens");
        }
    }
}
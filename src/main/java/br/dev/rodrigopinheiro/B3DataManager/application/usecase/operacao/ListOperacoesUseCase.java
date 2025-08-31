package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ListOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ListOperacoesResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.OperacaoDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.mapper.OperacaoDTOMapper;
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
     */
    public ListOperacoesResult execute(ListOperacoesCommand command) {
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
        
        return new ListOperacoesResult(
            operacaoDTOs,
            page.getTotalPages(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }
}
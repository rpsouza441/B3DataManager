package br.dev.rodrigopinheiro.B3DataManager.presentation.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.OperacaoDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre entidades de domínio e DTOs de apresentação.
 * Isola a camada de apresentação do domínio.
 */
@Component
public class OperacaoDTOMapper {
    
    /**
     * Converte uma entidade de domínio para DTO de apresentação.
     * 
     * @param operacao Entidade de domínio
     * @return DTO para apresentação
     */
    public OperacaoDTO toDTO(Operacao operacao) {
        if (operacao == null) {
            return null;
        }
        
        return new OperacaoDTO(
            operacao.getId(),
            operacao.getEntradaSaida(),
            operacao.getData(),
            operacao.getMovimentacao(),
            operacao.getProduto(),
            operacao.getInstituicao(),
            operacao.getQuantidade().value(),
            operacao.getPrecoUnitario().getValue(),
            operacao.getValorOperacao().getValue(),      // Valor original da B3
            operacao.getValorCalculado().getValue(),     // Valor calculado
            operacao.getDuplicado(),
            operacao.getDimensionado(),
            operacao.temDiferencaValor(),                // Indica se há diferença
            operacao.getDiferencaValor()                 // Diferença absoluta
        );
    }
}
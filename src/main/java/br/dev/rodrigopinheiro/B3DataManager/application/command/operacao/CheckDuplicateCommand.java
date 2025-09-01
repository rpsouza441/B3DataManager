package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Comando para verificação de duplicidade de operações.
 * 
 * <p>Este comando encapsula todos os dados necessários para verificar se uma operação
 * já existe no sistema, considerando os critérios de duplicidade definidos pelas
 * regras de negócio.</p>
 * 
 * <p>Critérios de duplicidade:</p>
 * <ul>
 *   <li>Data da operação</li>
 *   <li>Tipo de movimentação</li>
 *   <li>Produto</li>
 *   <li>Instituição</li>
 *   <li>Quantidade</li>
 *   <li>Preço unitário</li>
 *   <li>Valor da operação</li>
 *   <li>Usuário proprietário</li>
 * </ul>
 * 
 * @param data Data da operação
 * @param movimentacao Tipo de movimentação (ex: "Compra", "Venda", "Dividendo")
 * @param produto Código e nome do produto (ex: "PETR4 - PETROBRAS")
 * @param instituicao Nome da instituição financeira
 * @param quantidade Quantidade de ativos
 * @param precoUnitario Preço unitário do ativo
 * @param valorOperacao Valor total da operação
 * @param usuarioId ID do usuário proprietário da operação
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record CheckDuplicateCommand(
    LocalDate data,
    String movimentacao,
    String produto,
    String instituicao,
    BigDecimal quantidade,
    BigDecimal precoUnitario,
    BigDecimal valorOperacao,
    UsuarioId usuarioId
) {
    
    /**
     * Construtor que valida os parâmetros obrigatórios.
     */
    public CheckDuplicateCommand {
        if (data == null) {
            throw new IllegalArgumentException("Data da operação é obrigatória");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        if (quantidade == null) {
            throw new IllegalArgumentException("Quantidade é obrigatória");
        }
        if (precoUnitario == null) {
            throw new IllegalArgumentException("Preço unitário é obrigatório");
        }
        if (valorOperacao == null) {
            throw new IllegalArgumentException("Valor da operação é obrigatório");
        }
    }
}
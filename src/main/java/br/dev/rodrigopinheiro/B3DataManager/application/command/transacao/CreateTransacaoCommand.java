package br.dev.rodrigopinheiro.B3DataManager.application.command.transacao;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;

/**
 * Comando para criação de transação a partir de uma operação.
 * 
 * <p>Este comando encapsula uma operação que deve ser processada
 * para criar a transação correspondente no sistema.</p>
 * 
 * <p>Validações realizadas:</p>
 * <ul>
 *   <li>Operação não pode ser nula</li>
 *   <li>Operação deve ter usuário associado</li>
 *   <li>Operações duplicadas são ignoradas automaticamente</li>
 * </ul>
 * 
 * <p>Fluxo de processamento:</p>
 * <ul>
 *   <li>Verificação de duplicidade</li>
 *   <li>Criação/obtenção de agregados (Portfolio, Instituição)</li>
 *   <li>Criação da transação via factory</li>
 *   <li>Criação de ativo financeiro (se aplicável)</li>
 *   <li>Persistência dos agregados</li>
 * </ul>
 * 
 * @param operacao A operação a ser processada para criação da transação
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record CreateTransacaoCommand(
    OperacaoEntity operacao
) {
    
    /**
     * Construtor com validação.
     * 
     * @param operacao A operação a ser processada
     * @throws IllegalArgumentException se a operação for nula
     */
    public CreateTransacaoCommand {
        if (operacao == null) {
            throw new IllegalArgumentException("Operação não pode ser nula");
        }
    }
}
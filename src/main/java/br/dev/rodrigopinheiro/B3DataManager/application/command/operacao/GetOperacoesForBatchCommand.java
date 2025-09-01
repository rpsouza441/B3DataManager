package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

/**
 * Comando para buscar operações para processamento em batch.
 * 
 * <p>Este comando encapsula os critérios para buscar operações que precisam
 * ser processadas em lote, tipicamente operações não dimensionadas e não duplicadas.</p>
 * 
 * @param dimensionado Indica se deve buscar operações dimensionadas ou não
 * @param duplicado Indica se deve buscar operações duplicadas ou não
 * @param pageSize Tamanho da página para processamento em lote
 * @param offset Offset para paginação
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record GetOperacoesForBatchCommand(
    boolean dimensionado,
    boolean duplicado,
    int pageSize,
    int offset
) {
    
    /**
     * Construtor que valida os parâmetros.
     */
    public GetOperacoesForBatchCommand {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser positivo");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset não pode ser negativo");
        }
    }
    
    /**
     * Cria comando para buscar operações padrão para batch.
     * 
     * @param pageSize Tamanho da página
     * @param offset Offset da página
     * @return Comando configurado para operações não dimensionadas e não duplicadas
     */
    public static GetOperacoesForBatchCommand forDefaultBatch(int pageSize, int offset) {
        return new GetOperacoesForBatchCommand(false, false, pageSize, offset);
    }
}
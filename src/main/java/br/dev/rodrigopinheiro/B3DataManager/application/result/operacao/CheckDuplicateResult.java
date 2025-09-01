package br.dev.rodrigopinheiro.B3DataManager.application.result.operacao;

/**
 * Resultado da verificação de duplicidade de operações.
 * 
 * <p>Este resultado encapsula as informações sobre a verificação de duplicidade,
 * indicando se a operação é duplicada e, em caso positivo, qual é o ID da
 * operação original.</p>
 * 
 * <p>Casos de uso:</p>
 * <ul>
 *   <li><strong>Operação única:</strong> isDuplicate = false, originalId = null</li>
 *   <li><strong>Operação duplicada:</strong> isDuplicate = true, originalId = ID da operação original</li>
 * </ul>
 * 
 * <p>Este resultado é usado para:</p>
 * <ul>
 *   <li>Marcar operações como duplicadas durante importação</li>
 *   <li>Estabelecer referência à operação original</li>
 *   <li>Permitir auditoria e rastreabilidade</li>
 * </ul>
 * 
 * @param isDuplicate Indica se a operação é duplicada
 * @param originalId ID da operação original (null se não for duplicada)
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record CheckDuplicateResult(
    boolean isDuplicate,
    Long originalId
) {
    
    /**
     * Cria um resultado indicando que a operação não é duplicada.
     * 
     * @return Resultado com isDuplicate = false e originalId = null
     */
    public static CheckDuplicateResult notDuplicate() {
        return new CheckDuplicateResult(false, null);
    }
    
    /**
     * Cria um resultado indicando que a operação é duplicada.
     * 
     * @param originalId ID da operação original
     * @return Resultado com isDuplicate = true e o ID da operação original
     * @throws IllegalArgumentException se originalId for null
     */
    public static CheckDuplicateResult duplicate(Long originalId) {
        if (originalId == null) {
            throw new IllegalArgumentException("ID da operação original é obrigatório para operações duplicadas");
        }
        return new CheckDuplicateResult(true, originalId);
    }
    
    /**
     * Construtor que valida a consistência dos dados.
     */
    public CheckDuplicateResult {
        if (isDuplicate && originalId == null) {
            throw new IllegalArgumentException("ID da operação original é obrigatório quando a operação é duplicada");
        }
        if (!isDuplicate && originalId != null) {
            throw new IllegalArgumentException("ID da operação original deve ser null quando a operação não é duplicada");
        }
    }
}
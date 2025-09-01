package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;

import java.util.List;

/**
 * Comando para geração de relatório de erros de importação Excel.
 * 
 * <p>Este comando encapsula os dados necessários para gerar um arquivo Excel
 * contendo as linhas que apresentaram erro durante a importação, incluindo
 * os dados originais para facilitar a correção.</p>
 * 
 * <p>Funcionalidades do relatório gerado:</p>
 * <ul>
 *   <li>Primeira coluna: Mensagem de erro detalhada</li>
 *   <li>Colunas seguintes: Dados originais da linha com erro</li>
 *   <li>Cabeçalho: Nomes das colunas para facilitar identificação</li>
 *   <li>Formatação: Auto-ajuste de largura das colunas</li>
 * </ul>
 * 
 * <p>Benefícios para o usuário:</p>
 * <ul>
 *   <li>Visualização clara dos erros encontrados</li>
 *   <li>Dados originais preservados para correção</li>
 *   <li>Possibilidade de corrigir diretamente no arquivo</li>
 *   <li>Reimportação simples após correções</li>
 * </ul>
 * 
 * <p>Formato do arquivo gerado:</p>
 * <pre>
 * | ERRO                    | Data       | Entrada/Saída | Movimentação | Produto | ... |
 * |-------------------------|------------|---------------|--------------|---------|-----|
 * | Data inválida           | 32/13/2025 | Entrada       | Compra       | PETR4   | ... |
 * | Quantidade deve ser > 0 | 15/08/2025 | Saída         | Venda        | VALE3   | ... |
 * </pre>
 * 
 * @param errors Lista de erros com dados originais das linhas
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record GenerateErrorReportCommand(
    List<ExcelRowError> errors
) {
    
    /**
     * Construtor que valida os parâmetros obrigatórios.
     */
    public GenerateErrorReportCommand {
        if (errors == null) {
            throw new IllegalArgumentException("Lista de erros não pode ser nula");
        }
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("Lista de erros não pode estar vazia");
        }
    }
    
    /**
     * Retorna o número de erros no relatório.
     * 
     * @return Quantidade de linhas com erro
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Verifica se há erros para processar.
     * 
     * @return true se há pelo menos um erro
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
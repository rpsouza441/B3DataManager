package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;

import java.io.InputStream;

/**
 * Comando para importação de operações a partir de arquivo Excel.
 * 
 * <p>Este comando encapsula os dados necessários para processar um arquivo Excel
 * e importar as operações contidas nele, associando-as ao usuário especificado.</p>
 * 
 * <p>O arquivo Excel deve seguir o formato padrão esperado pelo sistema:</p>
 * <ul>
 *   <li>Primeira linha: Cabeçalho (será ignorada)</li>
 *   <li>Colunas esperadas: Data, Entrada/Saída, Movimentação, Produto, Instituição, Quantidade, Preço Unitário, Valor Operação</li>
 *   <li>Formato de arquivo: .xlsx (Excel 2007+)</li>
 * </ul>
 * 
 * <p>Validações realizadas:</p>
 * <ul>
 *   <li>Arquivo não pode ser nulo</li>
 *   <li>Usuário deve ser válido e autorizado</li>
 *   <li>Cada linha do Excel será validada individualmente</li>
 * </ul>
 * 
 * <p>Tratamento de erros:</p>
 * <ul>
 *   <li>Linhas com erro são coletadas e reportadas</li>
 *   <li>Linhas válidas são processadas normalmente</li>
 *   <li>Processo não é interrompido por erros em linhas individuais</li>
 * </ul>
 * 
 * @param inputStream Stream do arquivo Excel a ser processado
 * @param usuarioId ID do usuário proprietário das operações
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record ImportExcelCommand(
    InputStream inputStream,
    UsuarioId usuarioId
) {
    
    /**
     * Construtor que valida os parâmetros obrigatórios.
     */
    public ImportExcelCommand {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream do arquivo Excel é obrigatório");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        if (usuarioId.value() <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser positivo");
        }
    }
}
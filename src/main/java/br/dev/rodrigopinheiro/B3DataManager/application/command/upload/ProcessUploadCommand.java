package br.dev.rodrigopinheiro.B3DataManager.application.command.upload;

import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;

/**
 * Comando para processamento de upload de arquivos Excel.
 * 
 * <p>Encapsula todos os dados necessários para processar um arquivo
 * Excel enviado via upload pela interface web.</p>
 * 
 * <h3>Dados Incluídos:</h3>
 * <ul>
 *   <li><strong>fileBytes:</strong> Conteúdo binário do arquivo Excel</li>
 *   <li><strong>fileName:</strong> Nome original do arquivo (para validação e logs)</li>
 *   <li><strong>usuarioId:</strong> ID do usuário que está fazendo o upload</li>
 * </ul>
 * 
 * <h3>Validações Esperadas:</h3>
 * <ul>
 *   <li>fileBytes não pode ser nulo ou vazio</li>
 *   <li>fileName deve ter extensão .xlsx</li>
 *   <li>usuarioId deve ser válido e não nulo</li>
 *   <li>Tamanho do arquivo deve estar dentro dos limites</li>
 * </ul>
 * 
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * ProcessUploadCommand command = new ProcessUploadCommand(
 *     arquivoBytes,
 *     "operacoes_janeiro.xlsx",
 *     new UsuarioId(1L)
 * );
 * 
 * UploadProcessingResult result = processUploadUseCase.execute(command);
 * }</pre>
 * 
 * @param fileBytes Conteúdo binário do arquivo Excel
 * @param fileName Nome original do arquivo
 * @param usuarioId ID do usuário que está fazendo o upload
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record ProcessUploadCommand(
    byte[] fileBytes,
    String fileName,
    UsuarioId usuarioId
) {
    
    /**
     * Construtor que valida os parâmetros básicos.
     */
    public ProcessUploadCommand {
        if (fileBytes == null) {
            throw new IllegalArgumentException("Conteúdo do arquivo não pode ser nulo");
        }
        
        if (fileName == null) {
            throw new IllegalArgumentException("Nome do arquivo não pode ser nulo");
        }
        
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
    }
    
    /**
     * Retorna o tamanho do arquivo em bytes.
     * 
     * @return Tamanho do arquivo
     */
    public int getFileSize() {
        return fileBytes.length;
    }
    
    /**
     * Verifica se o arquivo tem extensão válida.
     * 
     * @return true se a extensão for .xlsx
     */
    public boolean hasValidExtension() {
        return fileName.toLowerCase().endsWith(".xlsx");
    }
    
    /**
     * Retorna uma representação segura para logs (sem expor o conteúdo do arquivo).
     * 
     * @return String representando o comando de forma segura
     */
    @Override
    public String toString() {
        return String.format("ProcessUploadCommand{fileName='%s', fileSize=%d bytes, usuarioId=%s}",
                fileName, fileBytes.length, usuarioId.value());
    }
}
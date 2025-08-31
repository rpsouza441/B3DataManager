package br.dev.rodrigopinheiro.B3DataManager.application.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;

import java.util.Optional;

/**
 * Port de saída para persistência de operações.
 * Define o contrato que deve ser implementado pela camada de infraestrutura.
 */
public interface OperacaoRepository {
    
    /**
     * Salva uma operação no repositório.
     * 
     * @param operacao A operação a ser salva
     * @return A operação salva com ID gerado
     */
    Operacao save(Operacao operacao);
    
    /**
     * Busca uma operação por ID.
     * 
     * @param id O ID da operação
     * @return Optional contendo a operação se encontrada
     */
    Optional<Operacao> findById(Long id);
    
    /**
     * Verifica se existe uma operação com o ID original e usuário especificados.
     * Usado para detectar duplicatas durante importação.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return true se existe uma operação com esses parâmetros
     */
    boolean existsByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId);
    
    /**
     * Busca uma operação por ID original e usuário.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return Optional contendo a operação se encontrada
     */
    Optional<Operacao> findByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId);
}
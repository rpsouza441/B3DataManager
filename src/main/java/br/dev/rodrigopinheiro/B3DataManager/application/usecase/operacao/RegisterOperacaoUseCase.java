package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.RegisterOperacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario.UsuarioNaoAutorizadoException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case responsável por registrar uma nova operação.
 * Orquestra a validação de negócio, criação da entidade de domínio e persistência.
 */
@Service
public class RegisterOperacaoUseCase {
    
    private final OperacaoRepository operacaoRepository;
    
    public RegisterOperacaoUseCase(OperacaoRepository operacaoRepository) {
        this.operacaoRepository = operacaoRepository;
    }
    
    /**
     * Registra uma nova operação.
     * 
     * @param command Comando contendo os dados da operação
     * @return A operação registrada
     * @throws OperacaoInvalidaException Se a operação não atender aos invariantes
     * @throws UsuarioNaoAutorizadoException Se o usuário não for válido
     */
    @Transactional
    public Operacao execute(RegisterOperacaoCommand command) {
        
        // Validar se o usuário foi fornecido
        if (command.usuarioId() == null) {
            throw new UsuarioNaoAutorizadoException("UsuarioId é obrigatório para registrar uma operação");
        }
        
        // Criar UsuarioId
        UsuarioId usuarioId = new UsuarioId(command.usuarioId());
        
        // Verificar duplicidade se idOriginal foi fornecido
        if (command.idOriginal() != null) {
            boolean existe = operacaoRepository.existsByIdOriginalAndUsuarioId(
                command.idOriginal(), usuarioId);
            
            if (existe) {
                throw new OperacaoInvalidaException(
                    "Já existe uma operação com idOriginal " + command.idOriginal() + 
                    " para o usuário " + usuarioId.value());
            }
        }
        
        // Criar Value Objects
        Quantidade quantidade = new Quantidade(command.quantidade());
        Dinheiro precoUnitario = new Dinheiro(command.precoUnitario());
        Dinheiro valorOperacao = new Dinheiro(command.valorOperacao());
        
        // Criar entidade de domínio (validações são executadas no construtor)
        Operacao operacao = new Operacao(
            null, // ID será gerado pela infraestrutura
            command.entradaSaida(),
            command.data(),
            command.movimentacao(),
            command.produto(),
            command.instituicao(),
            quantidade,
            precoUnitario,
            valorOperacao,
            command.duplicado(),
            command.dimensionado(),
            command.idOriginal(),
            command.deletado(),
            usuarioId
        );
        
        // Persistir a operação
        return operacaoRepository.save(operacao);
    }
}
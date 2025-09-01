package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.RegisterOperacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.transacao.CreateTransacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.transacao.CreateTransacaoUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario.UsuarioNaoAutorizadoException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Use Case responsável por registrar uma nova operação.
 * Orquestra a validação de negócio, criação da entidade de domínio, persistência e criação de transação.
 */
@Slf4j
@Service
public class RegisterOperacaoUseCase {
    
    private final OperacaoRepository operacaoRepository;
    private final CreateTransacaoUseCase createTransacaoUseCase;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public RegisterOperacaoUseCase(
            OperacaoRepository operacaoRepository,
            CreateTransacaoUseCase createTransacaoUseCase) {
        this.operacaoRepository = operacaoRepository;
        this.createTransacaoUseCase = createTransacaoUseCase;
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
        
        // Validar se o usuário foi fornecido (já é Value Object)
        if (command.usuarioId() == null) {
            throw new UsuarioNaoAutorizadoException("UsuarioId é obrigatório para registrar uma operação");
        }
        
        // Verificar duplicidade se idOriginal foi fornecido
        if (command.idOriginal() != null) {
            boolean existe = operacaoRepository.existsByIdOriginalAndUsuarioId(
                command.idOriginal(), command.usuarioId());
            
            if (existe) {
                throw new OperacaoInvalidaException(
                    "Já existe uma operação com idOriginal " + command.idOriginal() + 
                    " para o usuário " + command.usuarioId().value());
            }
        }
        
        // Criar entidade de domínio (validações são executadas no construtor)
        // Value Objects já vêm validados do comando
        Operacao operacao = new Operacao(
            null, // ID será gerado pela infraestrutura
            command.entradaSaida(),
            command.data(),
            command.movimentacao(),
            command.produto(),
            command.instituicao(),
            command.quantidade(),
            command.precoUnitario(),
            command.valorOperacao(),
            command.duplicado(),
            command.dimensionado(),
            command.idOriginal(),
            command.deletado(),
            command.usuarioId()
        );
        
        // Persistir a operação
        Operacao operacaoSalva = operacaoRepository.save(operacao);
        
        log.debug("Operação registrada com ID: {}", operacaoSalva.getId());
        
        // Criar transação correspondente
        try {
            // Converter domain.model.Operacao para infrastructure.persistence.entity.OperacaoEntity
            br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity operacaoEntity = convertToEntity(operacaoSalva);
            CreateTransacaoCommand createTransacaoCommand = new CreateTransacaoCommand(operacaoEntity);
            createTransacaoUseCase.execute(createTransacaoCommand);
            log.debug("Transação criada para operação: {}", operacaoSalva.getId());
        } catch (Exception e) {
            log.error("Erro ao criar transação para operação {}: {}", operacaoSalva.getId(), e.getMessage(), e);
            // Não relança a exceção para não afetar o registro da operação
            // A transação pode ser criada posteriormente via processo de reconciliação
        }
        
        return operacaoSalva;
    }
    
    /**
     * Converte uma operação do modelo de domínio para entidade de infraestrutura.
     * 
     * @param operacao Operação do modelo de domínio
     * @return Operação como entidade de infraestrutura
     */
    private OperacaoEntity convertToEntity(
            Operacao operacao) {
        
        OperacaoEntity entity = 
            new OperacaoEntity();
        
        entity.setId(operacao.getId());
        entity.setEntradaSaida(operacao.getEntradaSaida());
        entity.setData(operacao.getData());
        entity.setMovimentacao(operacao.getMovimentacao());
        entity.setProduto(operacao.getProduto());
        entity.setInstituicao(operacao.getInstituicao());
        entity.setQuantidade(operacao.getQuantidade().value().doubleValue());
        entity.setPrecoUnitario(operacao.getPrecoUnitario().getValue());
        entity.setValorOperacao(operacao.getValorOperacao().getValue());
        entity.setDuplicado(operacao.getDuplicado());
        entity.setDimensionado(operacao.getDimensionado());
        entity.setIdOriginal(operacao.getIdOriginal());
        entity.setDeletado(operacao.getDeletado());
        
        // Usar referência proxy do usuário para evitar validação de campos obrigatórios
        if (operacao.getUsuarioId() != null) {
            Usuario usuarioRef = 
                entityManager.getReference(
                    Usuario.class, 
                    operacao.getUsuarioId().value()
                );
            entity.setUsuario(usuarioRef);
        }
        
        return entity;
    }
}
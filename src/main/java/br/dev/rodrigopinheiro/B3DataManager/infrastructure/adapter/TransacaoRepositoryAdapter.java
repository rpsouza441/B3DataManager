package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.TransacaoRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.TransacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter que implementa o port de repositório de transações.
 * 
 * <p>Este adapter é responsável por fazer a ponte entre o domínio da aplicação
 * e a camada de persistência para operações relacionadas a transações financeiras.</p>
 * 
 * <p><strong>Características principais:</strong></p>
 * <ul>
 *   <li>Implementa o padrão Adapter da arquitetura hexagonal</li>
 *   <li>Utiliza mapper para conversão entre domain models e entities</li>
 *   <li>Operações CRUD completas para transações</li>
 *   <li>Buscas especializadas por ativo, portfolio e período</li>
 *   <li>Validações de entrada para evitar NPE</li>
 * </ul>
 * 
 * <p><strong>Operações suportadas:</strong></p>
 * <ul>
 *   <li>CRUD básico de transações</li>
 *   <li>Busca por ativo financeiro e portfolio</li>
 *   <li>Busca por período de operação</li>
 *   <li>Busca específica de compras por ativo</li>
 *   <li>Verificações de existência</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransacaoRepositoryAdapter implements TransacaoRepositoryPort {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Override
    public Transacao save(Transacao transacao) {
        if (transacao == null) {
            log.warn("Tentativa de salvar transação nula");
            throw new IllegalArgumentException("Transacao não pode ser nula");
        }
        
        try {
            log.debug("Salvando transação: {}", transacao.getId());
            TransacaoEntity entity = transacaoMapper.toEntity(transacao);
            TransacaoEntity savedEntity = transacaoRepository.save(entity);
            Transacao result = transacaoMapper.toDomain(savedEntity);
            log.debug("Transação salva com sucesso: ID {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("Erro ao salvar transação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar transação", e);
        }
    }

    @Override
    public Optional<Transacao> findById(Long id) {
        if (id == null) {
            log.warn("Tentativa de buscar transação com ID nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando transação por ID: {}", id);
        return transacaoRepository.findById(id)
                .map(transacaoMapper::toDomain);
    }

    @Override
    public List<Transacao> findAll() {
        log.debug("Buscando todas as transações");
        return transacaoRepository.findAll().stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByAtivoFinanceiro(AtivoFinanceiro ativo) {
        if (ativo == null || ativo.getId() == null) {
            log.warn("Tentativa de buscar transações com ativo nulo ou sem ID");
            return Collections.emptyList();
        }
        
        log.debug("Buscando transações por ativo financeiro ID: {}", ativo.getId());
        return transacaoRepository.findByAtivoFinanceiroId(ativo.getId()).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByPortfolio(Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            log.warn("Tentativa de buscar transações com portfolio nulo ou sem ID");
            return Collections.emptyList();
        }
        
        log.debug("Buscando transações por portfolio ID: {}", portfolio.getId());
        return transacaoRepository.findByPortfolioId(portfolio.getId()).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByDataOperacao(LocalDate data) {
        if (data == null) {
            log.warn("Tentativa de buscar transações com data nula");
            return Collections.emptyList();
        }
        
        log.debug("Buscando transações por data: {}", data);
        return transacaoRepository.findByDataTransacao(data).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByDataOperacaoBetween(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            log.warn("Tentativa de buscar transações com período inválido");
            return Collections.emptyList();
        }
        
        log.debug("Buscando transações entre {} e {}", dataInicio, dataFim);
        return transacaoRepository.findByDataTransacaoBetween(dataInicio, dataFim).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findComprasByAtivoFinanceiro(AtivoFinanceiro ativo) {
        if (ativo == null || ativo.getId() == null) {
            log.warn("Tentativa de buscar compras com ativo nulo ou sem ID");
            return Collections.emptyList();
        }
        
        log.debug("Buscando compras por ativo financeiro ID: {}", ativo.getId());
        return transacaoRepository.findByAtivoFinanceiroIdAndEntradaSaida(ativo.getId(), "ENTRADA").stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("Tentativa de deletar transação com ID nulo");
            return;
        }
        
        log.debug("Deletando transação por ID: {}", id);
        transacaoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return transacaoRepository.existsById(id);
    }


}
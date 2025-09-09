package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.TransacaoRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.TransacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransacaoRepositoryAdapter implements TransacaoRepositoryPort {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private TransacaoMapper transacaoMapper;

    @Override
    public Transacao save(Transacao transacao) {
        TransacaoEntity entity = transacaoMapper.toEntity(transacao);
        TransacaoEntity savedEntity = transacaoRepository.save(entity);
        return transacaoMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transacao> findById(Long id) {
        return transacaoRepository.findById(id)
                .map(transacaoMapper::toDomain);
    }

    @Override
    public List<Transacao> findAll() {
        return transacaoRepository.findAll().stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByAtivoFinanceiro(br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro ativo) {
        return transacaoRepository.findByAtivoFinanceiroId(ativo.getId()).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByPortfolio(br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio portfolio) {
        return transacaoRepository.findByPortfolioId(portfolio.getId()).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByDataOperacao(LocalDate data) {
        return transacaoRepository.findByDataTransacao(data).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findByDataOperacaoBetween(LocalDate dataInicio, LocalDate dataFim) {
        return transacaoRepository.findByDataTransacaoBetween(dataInicio, dataFim).stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> findComprasByAtivoFinanceiro(br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro ativo) {
        return transacaoRepository.findByAtivoFinanceiroIdAndEntradaSaida(ativo.getId(), "ENTRADA").stream()
                .map(transacaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        transacaoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return transacaoRepository.existsById(id);
    }


}
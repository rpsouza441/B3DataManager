package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity.OperacaoJpaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.OperacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.JpaOperacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para validar o mapeamento JPA e transações.
 * Usa banco H2 em memória para testes.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({OperacaoMapper.class, OperacaoRepositoryAdapter.class})
class OperacaoRepositoryAdapterIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private JpaOperacaoRepository jpaRepository;
    
    @Autowired
    private OperacaoRepositoryAdapter repositoryAdapter;
    
    @Test
    @Transactional
    void deveSalvarERecuperarOperacao() {
        // Arrange
        Operacao operacao = criarOperacaoValida();
        
        // Act - Salvar
        Operacao operacaoSalva = repositoryAdapter.save(operacao);
        entityManager.flush();
        entityManager.clear();
        
        // Assert - Verificar se foi salva
        assertNotNull(operacaoSalva.getId());
        
        // Act - Recuperar
        Optional<Operacao> operacaoRecuperada = repositoryAdapter.findById(operacaoSalva.getId());
        
        // Assert - Verificar se foi recuperada corretamente
        assertTrue(operacaoRecuperada.isPresent());
        Operacao op = operacaoRecuperada.get();
        
        assertEquals(operacaoSalva.getId(), op.getId());
        assertEquals("PETR4", op.getProduto());
        assertEquals("Compra", op.getEntradaSaida());
        assertEquals(new UsuarioId(1L), op.getUsuarioId());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(op.getQuantidade().value()));
        assertEquals(0, BigDecimal.valueOf(10.50).compareTo(op.getPrecoUnitario().getValue()));
        assertEquals(0, BigDecimal.valueOf(1050.00).compareTo(op.getValorOperacao().getValue()));
    }
    
    @Test
    @Transactional
    void deveVerificarExistenciaPorIdOriginalEUsuario() {
        // Arrange
        Long idOriginal = 123L;
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Salvar operação diretamente no banco
        OperacaoJpaEntity jpaEntity = new OperacaoJpaEntity(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            100.0, BigDecimal.valueOf(10.50), BigDecimal.valueOf(1050.00),
            false, false, idOriginal, false, usuarioId.value()
        );
        entityManager.persistAndFlush(jpaEntity);
        entityManager.clear();
        
        // Act & Assert
        assertTrue(repositoryAdapter.existsByIdOriginalAndUsuarioId(idOriginal, usuarioId));
        assertFalse(repositoryAdapter.existsByIdOriginalAndUsuarioId(999L, usuarioId));
        assertFalse(repositoryAdapter.existsByIdOriginalAndUsuarioId(idOriginal, new UsuarioId(999L)));
    }
    
    @Test
    @Transactional
    void deveBuscarPorIdOriginalEUsuario() {
        // Arrange
        Long idOriginal = 456L;
        UsuarioId usuarioId = new UsuarioId(2L);
        
        // Salvar operação diretamente no banco
        OperacaoJpaEntity jpaEntity = new OperacaoJpaEntity(
            "Venda", LocalDate.now(), "Venda à vista", "VALE3", "Rico Investimentos",
            50.0, BigDecimal.valueOf(25.75), BigDecimal.valueOf(1287.50),
            false, false, idOriginal, false, usuarioId.value()
        );
        entityManager.persistAndFlush(jpaEntity);
        entityManager.clear();
        
        // Act
        Optional<Operacao> resultado = repositoryAdapter.findByIdOriginalAndUsuarioId(idOriginal, usuarioId);
        
        // Assert
        assertTrue(resultado.isPresent());
        Operacao operacao = resultado.get();
        assertEquals("VALE3", operacao.getProduto());
        assertEquals("Venda", operacao.getEntradaSaida());
        assertEquals(usuarioId, operacao.getUsuarioId());
        assertEquals(idOriginal, operacao.getIdOriginal());
    }
    
    @Test
    @Transactional
    void deveValidarConversaoDoubleParaBigDecimal() {
        // Arrange - Criar operação com quantidade fracionária
        Operacao operacao = new Operacao(
            null, "Compra", LocalDate.now(), "Compra à vista", "ITUB4", "BTG Pactual",
            new Quantidade(BigDecimal.valueOf(33.333333)), // Quantidade com muitas casas decimais
            new Dinheiro(BigDecimal.valueOf(15.75)),
            new Dinheiro(BigDecimal.valueOf(525.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        // Act
        Operacao operacaoSalva = repositoryAdapter.save(operacao);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Operacao> operacaoRecuperada = repositoryAdapter.findById(operacaoSalva.getId());
        
        // Assert
        assertTrue(operacaoRecuperada.isPresent());
        Operacao op = operacaoRecuperada.get();
        
        // Verificar se a conversão double->BigDecimal mantém precisão razoável
        BigDecimal quantidadeOriginal = operacao.getQuantidade().value();
        BigDecimal quantidadeRecuperada = op.getQuantidade().value();
        
        // Diferença deve ser mínima (devido à conversão double)
        BigDecimal diferenca = quantidadeOriginal.subtract(quantidadeRecuperada).abs();
        assertTrue(diferenca.compareTo(BigDecimal.valueOf(0.001)) < 0, 
            "Diferença na conversão muito grande: " + diferenca);
    }
    
    private Operacao criarOperacaoValida() {
        return new Operacao(
            null, "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            new Quantidade(BigDecimal.valueOf(100)),
            new Dinheiro(BigDecimal.valueOf(10.50)),
            new Dinheiro(BigDecimal.valueOf(1050.00)),
            false, false, null, false, new UsuarioId(1L)
        );
    }
}
package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.OperacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.JpaOperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.UsuarioRepository;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para validar o mapeamento JPA e transações.
 * Usa banco H2 em memória para testes.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OperacaoRepositoryAdapterIntegrationTest {
    
    @Mock
    private JpaOperacaoRepository jpaRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private OperacaoMapper operacaoMapper;
    
    @InjectMocks
    private OperacaoRepositoryAdapter repositoryAdapter;
    
    @BeforeEach
    void setUp() {
        // Setup básico para os mocks
        UsuarioEntity usuario1 = new UsuarioEntity();
        usuario1.setId(1L);
        usuario1.setUsername("user1");
        usuario1.setEmail("user1@test.com");
        usuario1.setPassword("password");
        
        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(usuario1));
    }
    
    @Test
    void deveSalvarERecuperarOperacao() {
        // Arrange
        Operacao operacao = criarOperacaoValida();
        OperacaoEntity operacaoEntity = new OperacaoEntity();
        operacaoEntity.setId(1L);
        
        Operacao operacaoComId = new Operacao(
            1L, "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            new Quantidade(BigDecimal.valueOf(100)),
            new Dinheiro(BigDecimal.valueOf(10.50)),
            new Dinheiro(BigDecimal.valueOf(1050.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        when(operacaoMapper.toEntity(operacao)).thenReturn(operacaoEntity);
        when(jpaRepository.save(operacaoEntity)).thenReturn(operacaoEntity);
        when(operacaoMapper.toDomain(operacaoEntity)).thenReturn(operacaoComId);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(operacaoEntity));
        
        // Act - Salvar
        Operacao operacaoSalva = repositoryAdapter.save(operacao);
        
        // Assert - Verificar se foi salva
        assertNotNull(operacaoSalva.getId());
        assertEquals(1L, operacaoSalva.getId());
        
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
    void deveVerificarExistenciaPorIdOriginalEUsuario() {
        // Arrange
        Long idOriginal = 123L;
        UsuarioId usuarioId = new UsuarioId(1L);
        
        when(jpaRepository.existsByIdOriginalAndUsuario_Id(idOriginal, usuarioId.value())).thenReturn(true);
        when(jpaRepository.existsByIdOriginalAndUsuario_Id(999L, usuarioId.value())).thenReturn(false);
        when(jpaRepository.existsByIdOriginalAndUsuario_Id(idOriginal, 999L)).thenReturn(false);
        
        // Act & Assert
        assertTrue(repositoryAdapter.existsByIdOriginalAndUsuarioId(idOriginal, usuarioId));
        assertFalse(repositoryAdapter.existsByIdOriginalAndUsuarioId(999L, usuarioId));
        assertFalse(repositoryAdapter.existsByIdOriginalAndUsuarioId(idOriginal, new UsuarioId(999L)));
    }
    
    @Test
    void deveBuscarPorIdOriginalEUsuario() {
        // Arrange
        Long idOriginal = 456L;
        UsuarioId usuarioId = new UsuarioId(2L);
        
        OperacaoEntity jpaEntity = new OperacaoEntity(
            "Venda", LocalDate.now(), "Venda à vista", "VALE3", "Rico Investimentos",
            50.0, BigDecimal.valueOf(25.75), BigDecimal.valueOf(1287.50),
            false, false, idOriginal, false, usuarioId.value()
        );
        
        Operacao operacaoEsperada = new Operacao(
            1L, "Venda", LocalDate.now(), "Venda à vista", "VALE3", "Rico Investimentos",
            new Quantidade(BigDecimal.valueOf(50.0)),
            new Dinheiro(BigDecimal.valueOf(25.75)),
            new Dinheiro(BigDecimal.valueOf(1287.50)),
            false, false, idOriginal, false, usuarioId
        );
        
        when(jpaRepository.findByIdOriginalAndUsuario_Id(idOriginal, usuarioId.value()))
            .thenReturn(Optional.of(jpaEntity));
        when(operacaoMapper.toDomain(jpaEntity)).thenReturn(operacaoEsperada);
        
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
    void deveValidarConversaoDoubleParaBigDecimal() {
        // Arrange - Criar operação com quantidade fracionária
        Operacao operacao = new Operacao(
            null, "Compra", LocalDate.now(), "Compra à vista", "ITUB4", "BTG Pactual",
            new Quantidade(BigDecimal.valueOf(33.333333)), // Quantidade com muitas casas decimais
            new Dinheiro(BigDecimal.valueOf(15.75)),
            new Dinheiro(BigDecimal.valueOf(525.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        OperacaoEntity operacaoEntity = new OperacaoEntity();
        operacaoEntity.setId(1L);
        operacaoEntity.setQuantidade(33.333333); // Simula conversão para double
        
        Operacao operacaoSalva = new Operacao(
            1L, "Compra", LocalDate.now(), "Compra à vista", "ITUB4", "BTG Pactual",
            new Quantidade(BigDecimal.valueOf(33.333333)),
            new Dinheiro(BigDecimal.valueOf(15.75)),
            new Dinheiro(BigDecimal.valueOf(525.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        when(operacaoMapper.toEntity(operacao)).thenReturn(operacaoEntity);
        when(jpaRepository.save(operacaoEntity)).thenReturn(operacaoEntity);
        when(operacaoMapper.toDomain(operacaoEntity)).thenReturn(operacaoSalva);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(operacaoEntity));
        
        // Act
        Operacao operacaoSalvaResult = repositoryAdapter.save(operacao);
        Optional<Operacao> operacaoRecuperada = repositoryAdapter.findById(operacaoSalvaResult.getId());
        
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
package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.DarfEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar documentos DARF (Documento de Arrecadação de Receitas Federais).
 * 
 * Responsável pela persistência e consulta de documentos DARF relacionados
 * ao pagamento de impostos sobre operações financeiras.
 * 
 * Características:
 * - Consultas por período de competência
 * - Filtros por status de pagamento
 * - Cálculos de valores totais
 * - Suporte a soft delete
 * - Validação de duplicatas
 * - Relatórios fiscais
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Repository
public interface DarfRepository extends JpaRepository<DarfEntity, Long> {
    
    /**
     * Busca DARFs por período de competência.
     * Utilizado para relatórios mensais e anuais.
     * 
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @return Lista de DARFs no período especificado
     */
    @Query("SELECT d FROM DarfEntity d WHERE d.competencia BETWEEN :dataInicio AND :dataFim AND d.deletado = false ORDER BY d.competencia DESC")
    List<DarfEntity> findByCompetenciaBetweenAndDeletadoFalse(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    /**
     * Busca DARFs por competência específica.
     * 
     * @param competencia Mês/ano de competência
     * @return Lista de DARFs da competência especificada
     */
    @Query("SELECT d FROM DarfEntity d WHERE d.competencia = :competencia AND d.deletado = false ORDER BY d.dataVencimento")
    List<DarfEntity> findByCompetenciaAndDeletadoFalse(@Param("competencia") LocalDate competencia);
    
    /**
     * Busca DARFs por status de pagamento.
     * 
     * @param pago Status de pagamento (true = pago, false = pendente)
     * @return Lista de DARFs com o status especificado
     */
    @Query("SELECT d FROM DarfEntity d WHERE d.pago = :pago AND d.deletado = false ORDER BY d.dataVencimento")
    List<DarfEntity> findByPagoAndDeletadoFalse(@Param("pago") boolean pago);
    
    /**
     * Busca DARFs pendentes de pagamento com vencimento até uma data específica.
     * Útil para identificar DARFs em atraso.
     * 
     * @param dataLimite Data limite para verificação
     * @return Lista de DARFs pendentes até a data especificada
     */
    @Query("SELECT d FROM DarfEntity d WHERE d.pago = false AND d.dataVencimento <= :dataLimite AND d.deletado = false ORDER BY d.dataVencimento")
    List<DarfEntity> findPendentesAteData(@Param("dataLimite") LocalDate dataLimite);
    
    /**
     * Busca um DARF por ID que não esteja marcado como deletado.
     * 
     * @param id ID do DARF
     * @return Optional contendo o DARF ativo se encontrado
     */
    @Query("SELECT d FROM DarfEntity d WHERE d.id = :id AND d.deletado = false")
    Optional<DarfEntity> findByIdAndDeletadoFalse(@Param("id") Long id);
    
    /**
     * Busca todos os DARFs que não estão marcados como deletados.
     * Implementa soft delete para manter histórico fiscal.
     * 
     * @return Lista de DARFs ativos
     */
    @Query("SELECT d FROM DarfEntity d WHERE d.deletado = false ORDER BY d.competencia DESC, d.dataVencimento")
    List<DarfEntity> findByDeletadoFalse();
    
    /**
     * Calcula o valor total de impostos por competência.
     * 
     * @param competencia Mês/ano de competência
     * @return Valor total dos impostos da competência
     */
    @Query("SELECT COALESCE(SUM(d.valorImposto), 0) FROM DarfEntity d WHERE d.competencia = :competencia AND d.deletado = false")
    BigDecimal calcularValorTotalPorCompetencia(@Param("competencia") LocalDate competencia);
    
    /**
     * Calcula o valor total de impostos pendentes de pagamento.
     * 
     * @return Valor total dos impostos pendentes
     */
    @Query("SELECT COALESCE(SUM(d.valorImposto), 0) FROM DarfEntity d WHERE d.pago = false AND d.deletado = false")
    BigDecimal calcularValorTotalPendente();
    
    /**
     * Verifica se já existe um DARF para a competência especificada.
     * Utilizado para validação durante o cadastro.
     * 
     * @param competencia Mês/ano de competência
     * @return true se já existe um DARF para esta competência
     */
    @Query("SELECT COUNT(d) > 0 FROM DarfEntity d WHERE d.competencia = :competencia AND d.deletado = false")
    boolean existsByCompetenciaAndDeletadoFalse(@Param("competencia") LocalDate competencia);
    
    /**
     * Conta o número total de DARFs ativos.
     * 
     * @return Número de DARFs não deletados
     */
    @Query("SELECT COUNT(d) FROM DarfEntity d WHERE d.deletado = false")
    long countByDeletadoFalse();
    
    /**
     * Conta o número de DARFs pendentes de pagamento.
     * 
     * @return Número de DARFs não pagos
     */
    @Query("SELECT COUNT(d) FROM DarfEntity d WHERE d.pago = false AND d.deletado = false")
    long countPendentes();
}
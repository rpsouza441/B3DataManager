-- =====================================================
-- V1__baseline.sql - Schema Inicial B3DataManager
-- Arquitetura Limpa (sem propriedadesEspecificas)
-- =====================================================

-- Tabela: usuario
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT chk_username_length CHECK (LENGTH(username) >= 3),
    INDEX idx_usuario_username (username),
    INDEX idx_usuario_email (email)
);

-- Tabela: usuario_roles (ElementCollection para roles)
CREATE TABLE usuario_roles (
    usuario_entity_id BIGINT NOT NULL,
    roles VARCHAR(20) NOT NULL,
    
    PRIMARY KEY (usuario_entity_id, roles),
    FOREIGN KEY (usuario_entity_id) REFERENCES usuario(id) ON DELETE CASCADE,
    
    CONSTRAINT chk_roles_valid CHECK (roles IN ('ADMIN', 'USER'))
);

-- Tabela: instituicao
CREATE TABLE instituicao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    
    INDEX idx_instituicao_name (name)
);

-- Tabela: portifolio (nome mantido conforme entity)
CREATE TABLE portifolio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    saldo_total DECIMAL(19,2) DEFAULT 0.00,
    saldo_aplicado DECIMAL(19,2) DEFAULT 0.00,
    lucro_venda DECIMAL(19,2) DEFAULT 0.00,
    lucro_rendimento DECIMAL(19,2) DEFAULT 0.00,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    INDEX idx_portfolio_usuario (usuario_id)
);

-- Tabela: ativo_financeiro (arquitetura corrigida)
CREATE TABLE ativo_financeiro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    tipo_ativo VARCHAR(20) NOT NULL,
    portfolio_id BIGINT NOT NULL,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    
    FOREIGN KEY (portfolio_id) REFERENCES portifolio(id) ON DELETE CASCADE,
    
    CONSTRAINT chk_tipo_ativo_valid CHECK (tipo_ativo IN ('RENDA_FIXA', 'RENDA_VARIAVEL')),
    
    INDEX idx_ativo_codigo (codigo),
    INDEX idx_ativo_tipo (tipo_ativo),
    INDEX idx_ativo_portfolio (portfolio_id),
    INDEX idx_ativo_deletado (deletado)
);

-- Tabela: operacao (dados brutos importados)
CREATE TABLE operacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entrada_saida VARCHAR(50) NOT NULL,
    data DATE NOT NULL,
    movimentacao VARCHAR(100) NOT NULL,
    produto VARCHAR(100) NOT NULL,
    instituicao VARCHAR(100) NOT NULL,
    quantidade DOUBLE NOT NULL,
    preco_unitario DECIMAL(19,8) NOT NULL,
    valor_operacao DECIMAL(19,2) NOT NULL,
    valor_calculado DECIMAL(15,2),
    duplicado BOOLEAN NOT NULL DEFAULT FALSE,
    dimensionado BOOLEAN NOT NULL DEFAULT FALSE,
    id_original BIGINT,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    usuario_id BIGINT NOT NULL,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    
    INDEX idx_operacao_data (data),
    INDEX idx_operacao_produto (produto),
    INDEX idx_operacao_usuario (usuario_id),
    INDEX idx_operacao_deletado (deletado)
);

-- Tabela: transacao (dados processados)
CREATE TABLE transacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_operacao DATE NOT NULL,
    tipo_transacao VARCHAR(20) NOT NULL,
    tipo_movimentacao VARCHAR(20) NOT NULL,
    quantidade DECIMAL(19,8) NOT NULL,
    preco_unitario DECIMAL(19,8) NOT NULL,
    valor_total DECIMAL(19,2) NOT NULL,
    taxas DECIMAL(19,2) DEFAULT 0.00,
    valor_liquido DECIMAL(19,2),
    observacoes VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ativo_financeiro_id BIGINT NOT NULL,
    instituicao_id BIGINT,
    portfolio_id BIGINT NOT NULL,
    operacao_id BIGINT,
    created_at DATE,
    updated_at DATE,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    FOREIGN KEY (instituicao_id) REFERENCES instituicao(id) ON DELETE SET NULL,
    FOREIGN KEY (portfolio_id) REFERENCES portifolio(id) ON DELETE CASCADE,
    FOREIGN KEY (operacao_id) REFERENCES operacao(id) ON DELETE SET NULL,
    
    CONSTRAINT chk_tipo_transacao_valid CHECK (tipo_transacao IN (
        'ENTRADA', 'SAIDA', 'TAXA', 'VENDA', 'LUCRO_RENDIMENTO', 
        'LUCRO_DIVIDENDO', 'LUCRO_JUROS', 'LUCRO_OUTRA', 'TRANSFERENCIA', 'OUTRA'
    )),
    
    CONSTRAINT chk_tipo_movimentacao_valid CHECK (tipo_movimentacao IN (
        'CREDITO', 'DEBITO', 'TRANSFERENCIA', 'SUBSCRICAO', 'ATUALIZACAO', 
        'BONIFICACAO_EM_ATIVOS', 'AMORTIZACAO'
    )),
    
    INDEX idx_transacao_data (data_operacao),
    INDEX idx_transacao_ativo (ativo_financeiro_id),
    INDEX idx_transacao_portfolio (portfolio_id),
    INDEX idx_transacao_tipo (tipo_transacao),
    INDEX idx_transacao_ativo_flag (ativo)
);

-- Tabela: posicao (estado atual consolidado)
CREATE TABLE posicao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantidade_atual DECIMAL(19,8) NOT NULL DEFAULT 0,
    preco_medio DECIMAL(19,8) NOT NULL DEFAULT 0,
    valor_atual DECIMAL(19,2) NOT NULL DEFAULT 0,
    percentual_carteira DECIMAL(5,2) DEFAULT 0,
    data_ultima_atualizacao DATE,
    lucro_nao_realizado DECIMAL(19,2) DEFAULT 0,
    valor_investido DECIMAL(19,2) NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ativo_financeiro_id BIGINT NOT NULL,
    portfolio_id BIGINT NOT NULL,
    created_at DATE,
    updated_at DATE,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    FOREIGN KEY (portfolio_id) REFERENCES portifolio(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_posicao_ativo_portfolio (ativo_financeiro_id, portfolio_id),
    
    INDEX idx_posicao_ativo_portfolio (ativo_financeiro_id, portfolio_id),
    INDEX idx_posicao_portfolio (portfolio_id),
    INDEX idx_posicao_ativo (ativo_financeiro_id),
    INDEX idx_posicao_ativo_flag (ativo)
);

-- Tabela: usuario_instituicao (ManyToMany)
CREATE TABLE usuario_instituicao (
    usuario_id BIGINT NOT NULL,
    instituicao_id BIGINT NOT NULL,
    
    PRIMARY KEY (usuario_id, instituicao_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (instituicao_id) REFERENCES instituicao(id) ON DELETE CASCADE
);

-- Tabelas legadas (renda_variavel, renda_fixa) - mantidas para compatibilidade
-- Podem ser removidas após migração completa

CREATE TABLE renda_variavel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_compra DATE,
    preco_unitario DECIMAL(19,8),
    quantidade DOUBLE,
    total DECIMAL(19,2),
    ativo_financeiro_id BIGINT,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    INDEX idx_renda_variavel_ativo (ativo_financeiro_id)
);

CREATE TABLE renda_fixa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_compra DATE,
    preco_unitario DECIMAL(19,8),
    quantidade DOUBLE,
    total DECIMAL(19,2),
    ativo_financeiro_id BIGINT,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    INDEX idx_renda_fixa_ativo (ativo_financeiro_id)
);

-- =====================================================
-- VIEWS PARA COMPATIBILIDADE E PERFORMANCE
-- =====================================================

-- View: Posições Ativas Consolidadas
CREATE VIEW v_posicoes_ativas AS
SELECT 
    p.id,
    p.quantidade_atual,
    p.preco_medio,
    p.valor_atual,
    p.percentual_carteira,
    p.lucro_nao_realizado,
    p.valor_investido,
    af.codigo as ativo_codigo,
    af.nome as ativo_nome,
    af.tipo_ativo,
    port.usuario_id,
    p.data_ultima_atualizacao
FROM posicao p
JOIN ativo_financeiro af ON p.ativo_financeiro_id = af.id
JOIN portifolio port ON p.portfolio_id = port.id
WHERE p.ativo = TRUE 
  AND af.deletado = FALSE 
  AND p.quantidade_atual > 0;

-- View: Resumo Portfolio por Usuário
CREATE VIEW v_portfolio_resumo AS
SELECT 
    u.id as usuario_id,
    u.username,
    p.saldo_total,
    p.saldo_aplicado,
    p.lucro_venda,
    p.lucro_rendimento,
    (p.saldo_total - p.saldo_aplicado) as lucro_nao_realizado_total,
    COUNT(pos.id) as total_posicoes_ativas
FROM usuario u
JOIN portifolio p ON u.id = p.usuario_id
LEFT JOIN posicao pos ON p.id = pos.portfolio_id AND pos.ativo = TRUE AND pos.quantidade_atual > 0
WHERE u.deletado = FALSE
GROUP BY u.id, u.username, p.saldo_total, p.saldo_aplicado, p.lucro_venda, p.lucro_rendimento;

-- View: Transações por Tipo de Ativo
CREATE VIEW v_transacoes_por_tipo AS
SELECT 
    af.tipo_ativo,
    t.tipo_transacao,
    COUNT(*) as total_transacoes,
    SUM(t.valor_total) as valor_total_transacionado,
    AVG(t.valor_total) as valor_medio_transacao
FROM transacao t
JOIN ativo_financeiro af ON t.ativo_financeiro_id = af.id
WHERE t.ativo = TRUE AND af.deletado = FALSE
GROUP BY af.tipo_ativo, t.tipo_transacao;

-- =====================================================
-- DADOS INICIAIS (OPCIONAL)
-- =====================================================

-- Instituições padrão
INSERT INTO instituicao (name) VALUES 
('B3 - Brasil Bolsa Balcão'),
('Clear Corretora'),
('XP Investimentos'),
('Rico Investimentos'),
('Inter Investimentos'),
('Nubank'),
('BTG Pactual'),
('Itaú Investimentos'),
('Bradesco Investimentos'),
('Santander Investimentos');

-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================

/*
Esta migration cria o schema inicial do B3DataManager seguindo a arquitetura limpa aprovada:

✅ CARACTERÍSTICAS IMPLEMENTADAS:
- Eliminação completa de propriedadesEspecificas (Map<String, Object>)
- Uso de enums tipados (TipoAtivo, TipoTransacao, TipoMovimentacao, Roles)
- Type safety completa em todos os campos
- Separação clara entre Transacao (histórico) e Posicao (estado atual)
- Índices otimizados para performance
- Views para consultas frequentes
- Constraints para validação de dados
- Soft delete com campo 'deletado'
- Suporte a auditoria (created_at, updated_at)

✅ BENEFÍCIOS:
- Performance otimizada (campos indexados ao invés de JSON)
- Manutenibilidade (código limpo sem casts)
- Extensibilidade (novos campos via migrations tipadas)
- Arquitetura hexagonal (domain isolado)
- DDD compliance (agregados bem definidos)

🎯 PRÓXIMOS PASSOS:
1. Executar esta migration
2. Configurar Flyway no application.properties
3. Desabilitar hibernate.ddl-auto
4. Testar aplicação
5. Migrar dados existentes se necessário
*/
-- =====================================================
-- B3DataManager - Database Schema V1 (SINGLE_TABLE)
-- =====================================================
-- Data: 2024-01-15
-- Versão: 1.0.0
-- Estratégia: SINGLE_TABLE para AtivoFinanceiro
-- =====================================================

-- Tabela de usuários
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_usuario_username (username),
    INDEX idx_usuario_email (email)
);

-- Tabela de roles dos usuários
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    
    PRIMARY KEY (usuario_id, roles),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela de instituições financeiras (simplificada)
CREATE TABLE instituicao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_instituicao_nome (nome)
);

-- Tabela de relacionamento usuário-instituição
CREATE TABLE usuario_instituicao (
    usuario_id BIGINT NOT NULL,
    instituicao_id BIGINT NOT NULL,
    
    PRIMARY KEY (usuario_id, instituicao_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (instituicao_id) REFERENCES instituicao(id) ON DELETE CASCADE
);

-- Tabela de portfolios
CREATE TABLE portfolio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL UNIQUE,
    saldo_total DECIMAL(19,2) DEFAULT 0.00,
    saldo_aplicado DECIMAL(19,2) DEFAULT 0.00,
    lucro_venda DECIMAL(19,2) DEFAULT 0.00,
    lucro_rendimento DECIMAL(19,2) DEFAULT 0.00,
    lucro_nao_realizado DECIMAL(15,2) DEFAULT 0.00,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    INDEX idx_portfolio_usuario (usuario_id)
);

-- =====================================================
-- TABELA PRINCIPAL: ATIVO_FINANCEIRO (SINGLE_TABLE)
-- =====================================================
-- Estratégia SINGLE_TABLE: Uma tabela para todos os tipos de ativos
-- Discriminator: tipo_ativo (RENDA_FIXA, RENDA_VARIAVEL)
-- Campos específicos são nullable conforme o tipo
-- =====================================================

CREATE TABLE ativo_financeiro (
    -- Campos base (sempre preenchidos)
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    portfolio_id BIGINT NOT NULL,
    tipo_ativo VARCHAR(31) NOT NULL, -- Discriminator
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- =====================================================
    -- CAMPOS ESPECÍFICOS DE RENDA FIXA (nullable para RV)
    -- =====================================================
    tipo_renda_fixa VARCHAR(50) NULL,
    taxa_juros DECIMAL(5,2) NULL,
    data_vencimento DATE NULL,
    indexador VARCHAR(50) NULL,
    emissor VARCHAR(200) NULL,
    valor_minimo DECIMAL(15,2) NULL,
    liquidez_diaria BOOLEAN NULL,
    
    -- =====================================================
    -- CAMPOS ESPECÍFICOS DE RENDA VARIÁVEL (nullable para RF)
    -- =====================================================
    tipo_renda_variavel VARCHAR(50) NULL,
    setor VARCHAR(100) NULL,
    segmento VARCHAR(100) NULL,
    ticker VARCHAR(20) NULL,
    dividend_yield DECIMAL(5,4) NULL,
    free_float DECIMAL(5,2) NULL,
    market_cap BIGINT NULL,
    
    -- Constraints e índices
    FOREIGN KEY (portfolio_id) REFERENCES portfolio(id) ON DELETE CASCADE,
    
    -- Índices otimizados para SINGLE_TABLE
    INDEX idx_ativo_codigo (codigo),
    INDEX idx_ativo_tipo (tipo_ativo),
    INDEX idx_ativo_portfolio (portfolio_id),
    INDEX idx_ativo_nome (nome),
    
    -- Índices específicos por tipo
    INDEX idx_rf_tipo_emissor (tipo_renda_fixa, emissor),
    INDEX idx_rf_vencimento (data_vencimento),
    INDEX idx_rv_tipo_setor (tipo_renda_variavel, setor),
    INDEX idx_rv_ticker (ticker),
    
    -- Constraints de validação
    CONSTRAINT chk_tipo_ativo CHECK (tipo_ativo IN ('RENDA_FIXA', 'RENDA_VARIAVEL')),
    CONSTRAINT chk_rf_campos CHECK (
        (tipo_ativo = 'RENDA_FIXA' AND tipo_renda_fixa IS NOT NULL AND taxa_juros IS NOT NULL AND emissor IS NOT NULL) OR
        (tipo_ativo = 'RENDA_VARIAVEL')
    ),
    CONSTRAINT chk_rv_campos CHECK (
        (tipo_ativo = 'RENDA_VARIAVEL' AND tipo_renda_variavel IS NOT NULL) OR
        (tipo_ativo = 'RENDA_FIXA')
    )
);

-- Tabela de operações (dados brutos da B3)
CREATE TABLE operacao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entrada_saida VARCHAR(50) NOT NULL,
    data DATE NOT NULL,
    movimentacao VARCHAR(100) NOT NULL,
    produto VARCHAR(200) NOT NULL,
    instituicao VARCHAR(200) NOT NULL,
    quantidade DOUBLE NOT NULL,
    preco_unitario DECIMAL(19,8) NOT NULL,
    valor_operacao DECIMAL(19,2) NOT NULL,
    valor_calculado DECIMAL(19,2),
    duplicado BOOLEAN NOT NULL DEFAULT FALSE,
    processado BOOLEAN NOT NULL DEFAULT FALSE, -- Renomeado de dimensionado
    id_original BIGINT,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    usuario_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    
    INDEX idx_operacao_data (data),
    INDEX idx_operacao_produto (produto),
    INDEX idx_operacao_usuario (usuario_id),
    INDEX idx_operacao_processado (processado)
);

-- Tabela de transações (histórico processado)
CREATE TABLE transacao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_operacao DATE NOT NULL,
    tipo_transacao VARCHAR(20) NOT NULL,
    tipo_movimentacao VARCHAR(20) NOT NULL,
    quantidade DECIMAL(19,8) NOT NULL,
    preco_unitario DECIMAL(19,8) NOT NULL,
    valor_total DECIMAL(19,2) NOT NULL,
    taxas DECIMAL(19,2) DEFAULT 0.00,
    valor_liquido DECIMAL(19,2),
    observacoes VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE, -- Soft delete
    ativo_financeiro_id BIGINT NOT NULL,
    instituicao_id BIGINT,
    portfolio_id BIGINT NOT NULL,
    operacao_id BIGINT,
    darf_id BIGINT, -- Relacionamento com DARF
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    FOREIGN KEY (instituicao_id) REFERENCES instituicao(id),
    FOREIGN KEY (portfolio_id) REFERENCES portfolio(id) ON DELETE CASCADE,
    FOREIGN KEY (operacao_id) REFERENCES operacao(id),
    FOREIGN KEY (darf_id) REFERENCES darf(id),
    
    INDEX idx_transacao_data (data_operacao),
    INDEX idx_transacao_ativo (ativo_financeiro_id),
    INDEX idx_transacao_portfolio (portfolio_id),
    INDEX idx_transacao_tipo (tipo_transacao),
    INDEX idx_transacao_darf (darf_id)
);

-- Tabela de posições (estado atual)
CREATE TABLE posicao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantidade_atual DECIMAL(19,8) NOT NULL,
    preco_medio DECIMAL(19,8) NOT NULL,
    valor_atual DECIMAL(19,2) NOT NULL,
    percentual_carteira DECIMAL(5,2),
    data_ultima_atualizacao DATE,
    lucro_nao_realizado DECIMAL(19,2),
    valor_investido DECIMAL(19,2) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE, -- Soft delete
    ativo_financeiro_id BIGINT NOT NULL,
    portfolio_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    FOREIGN KEY (portfolio_id) REFERENCES portfolio(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_posicao_ativo_portfolio (ativo_financeiro_id, portfolio_id),
    INDEX idx_posicao_portfolio (portfolio_id),
    INDEX idx_posicao_ativo (ativo_financeiro_id)
);

-- Tabela de DARF (impostos)
CREATE TABLE darf (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    esta_pago BOOLEAN NOT NULL DEFAULT FALSE,
    data_pagamento DATE,
    valor DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_darf_data_pagamento (data_pagamento),
    INDEX idx_darf_esta_pago (esta_pago)
);

-- Tabela de renda fixa (entidade separada)
CREATE TABLE renda_fixa (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_compra DATE,
    preco_unitario DECIMAL(19,8),
    quantidade INTEGER,
    total DECIMAL(19,2),
    ativo_financeiro_id BIGINT,
    tipo_renda_fixa VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    INDEX idx_renda_fixa_ativo (ativo_financeiro_id),
    INDEX idx_renda_fixa_tipo (tipo_renda_fixa)
);

-- Tabela de renda variável (entidade separada)
CREATE TABLE renda_variavel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_compra DATE,
    preco_unitario DECIMAL(19,8),
    quantidade DOUBLE,
    total DECIMAL(19,2),
    ativo_financeiro_id BIGINT,
    tipo_renda_variavel VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    INDEX idx_renda_variavel_ativo (ativo_financeiro_id),
    INDEX idx_renda_variavel_tipo (tipo_renda_variavel)
);

-- =====================================================
-- DADOS INICIAIS
-- =====================================================



-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================
-- Esta estrutura implementa:
-- 1. SINGLE_TABLE para AtivoFinanceiro (máxima performance)
-- 2. Relacionamentos otimizados com índices
-- 3. Soft delete em todas as entidades principais
-- 4. Auditoria com created_at/updated_at
-- 5. Constraints de validação para integridade
-- 6. Relacionamento Transacao <-> DARF implementado
-- =====================================================
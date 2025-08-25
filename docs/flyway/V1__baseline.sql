-- B3DataManager - Baseline Schema Migration
-- Data: 2025-08-25
-- Descrição: Schema inicial baseado no estado atual das entidades JPA

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_usuario_username (username),
    INDEX idx_usuario_email (email),
    INDEX idx_usuario_deletado (deletado)
);

-- Tabela de roles dos usuários
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, roles),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela de instituições
CREATE TABLE IF NOT EXISTS instituicao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    codigo VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_instituicao_nome (nome),
    INDEX idx_instituicao_codigo (codigo)
);

-- Tabela de relacionamento usuário-instituição
CREATE TABLE IF NOT EXISTS usuario_instituicao (
    usuario_id BIGINT NOT NULL,
    instituicao_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, instituicao_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (instituicao_id) REFERENCES instituicao(id) ON DELETE CASCADE
);

-- Tabela de portfólio
CREATE TABLE IF NOT EXISTS portifolio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    saldo_total DECIMAL(15,2) DEFAULT 0.00,
    saldo_aplicado DECIMAL(15,2) DEFAULT 0.00,
    lucro_venda DECIMAL(15,2) DEFAULT 0.00,
    lucro_rendimento DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    INDEX idx_portfolio_usuario (usuario_id)
);

-- Tabela de ativos financeiros
CREATE TABLE IF NOT EXISTS ativo_financeiro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    nome VARCHAR(255),
    tipo VARCHAR(50) NOT NULL,
    quantidade DECIMAL(15,4) DEFAULT 0.0000,
    preco_medio DECIMAL(10,4) DEFAULT 0.0000,
    valor_total DECIMAL(15,2) DEFAULT 0.00,
    deletado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portifolio(id) ON DELETE CASCADE,
    INDEX idx_ativo_portfolio (portfolio_id),
    INDEX idx_ativo_codigo (codigo),
    INDEX idx_ativo_tipo (tipo),
    INDEX idx_ativo_deletado (deletado)
);

-- Tabela de renda variável
CREATE TABLE IF NOT EXISTS renda_variavel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ativo_financeiro_id BIGINT NOT NULL,
    tipo_renda_variavel VARCHAR(50) NOT NULL,
    setor VARCHAR(100),
    segmento VARCHAR(100),
    dividend_yield DECIMAL(5,4),
    p_vp DECIMAL(8,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    INDEX idx_renda_variavel_ativo (ativo_financeiro_id),
    INDEX idx_renda_variavel_tipo (tipo_renda_variavel)
);

-- Tabela de renda fixa
CREATE TABLE IF NOT EXISTS renda_fixa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ativo_financeiro_id BIGINT NOT NULL,
    tipo_renda_fixa VARCHAR(50) NOT NULL,
    taxa_juros DECIMAL(8,4),
    data_vencimento DATE,
    indexador VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE CASCADE,
    INDEX idx_renda_fixa_ativo (ativo_financeiro_id),
    INDEX idx_renda_fixa_tipo (tipo_renda_fixa),
    INDEX idx_renda_fixa_vencimento (data_vencimento)
);

-- Tabela de transações
CREATE TABLE IF NOT EXISTS transacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    ativo_financeiro_id BIGINT,
    tipo_transacao VARCHAR(50) NOT NULL,
    quantidade DECIMAL(15,4) NOT NULL,
    preco_unitario DECIMAL(10,4) NOT NULL,
    valor_total DECIMAL(15,2) NOT NULL,
    taxa_corretagem DECIMAL(10,2) DEFAULT 0.00,
    data_transacao DATE NOT NULL,
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portifolio(id) ON DELETE CASCADE,
    FOREIGN KEY (ativo_financeiro_id) REFERENCES ativo_financeiro(id) ON DELETE SET NULL,
    INDEX idx_transacao_portfolio (portfolio_id),
    INDEX idx_transacao_ativo (ativo_financeiro_id),
    INDEX idx_transacao_data (data_transacao),
    INDEX idx_transacao_tipo (tipo_transacao)
);

-- Tabela de operações (importação de dados)
CREATE TABLE IF NOT EXISTS operacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data VARCHAR(20) NOT NULL,
    entrada_saida VARCHAR(20),
    movimentacao VARCHAR(100),
    produto VARCHAR(100),
    instituicao VARCHAR(100),
    quantidade DECIMAL(15,4),
    preco_unitario DECIMAL(10,4),
    valor_operacao DECIMAL(15,2),
    duplicado BOOLEAN DEFAULT FALSE,
    dimensionado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operacao_data (data),
    INDEX idx_operacao_produto (produto),
    INDEX idx_operacao_instituicao (instituicao),
    INDEX idx_operacao_duplicado (duplicado)
);

-- Tabela de DARF (Documento de Arrecadação de Receitas Federais)
CREATE TABLE IF NOT EXISTS darf (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    mes_referencia VARCHAR(7) NOT NULL, -- formato YYYY-MM
    valor_imposto DECIMAL(10,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    pago BOOLEAN DEFAULT FALSE,
    data_pagamento DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    INDEX idx_darf_usuario (usuario_id),
    INDEX idx_darf_mes (mes_referencia),
    INDEX idx_darf_vencimento (data_vencimento)
);

-- Inserir usuário administrador padrão (senha: admin123 - deve ser alterada)
INSERT IGNORE INTO usuario (username, password, email, deletado) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6pM7qcOPJy', 'admin@b3datamanager.com', FALSE);

-- Inserir role de administrador
INSERT IGNORE INTO usuario_roles (usuario_id, roles) 
SELECT id, 'ADMIN' FROM usuario WHERE username = 'admin';

-- Criar portfólio para o usuário administrador
INSERT IGNORE INTO portifolio (usuario_id, saldo_total, saldo_aplicado, lucro_venda, lucro_rendimento)
SELECT id, 0.00, 0.00, 0.00, 0.00 FROM usuario WHERE username = 'admin';

-- Comentários para documentação
-- Este script cria o schema baseline para o B3DataManager
-- Baseado nas entidades JPA existentes no projeto
-- Inclui índices para otimização de performance
-- Inclui usuário administrador padrão (senha deve ser alterada)
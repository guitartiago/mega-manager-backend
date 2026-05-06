-- Adicionar campos de rastreamento
ALTER TABLE lancamento
ADD COLUMN fechamento_id BIGINT,
ADD COLUMN data_processamento TIMESTAMP;

-- Constraint (opcional mas recomendado)
ALTER TABLE lancamento
ADD CONSTRAINT fk_lancamento_fechamento
    FOREIGN KEY (fechamento_id)
    REFERENCES fechamento_conta(id)
    ON DELETE SET NULL;

-- Índices para performance
CREATE INDEX idx_lancamento_cliente_fechamento
ON lancamento(cliente_id, fechamento_id);

CREATE INDEX idx_lancamento_nao_processado
ON lancamento(cliente_id)
WHERE fechamento_id IS NULL;
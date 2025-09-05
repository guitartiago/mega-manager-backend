-- Índices (buscas e joins)
CREATE INDEX IF NOT EXISTS ix_produtos_nome                 ON produtos (nome);
CREATE INDEX IF NOT EXISTS ix_produtos_ativo                ON produtos (ativo);
CREATE INDEX IF NOT EXISTS ix_entradas_produto_datacompra   ON entradas_estoque (produto_id, data_compra DESC);
CREATE INDEX IF NOT EXISTS ix_consumos_cliente_data         ON consumos (cliente_id, data_hora DESC);
CREATE INDEX IF NOT EXISTS ix_consumos_produto              ON consumos (produto_id);

-- Unicidade (comente se não quiser)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_clientes_email') THEN
    ALTER TABLE clientes ADD CONSTRAINT uq_clientes_email UNIQUE (email);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_produtos_nome') THEN
    ALTER TABLE produtos ADD CONSTRAINT uq_produtos_nome UNIQUE (nome);
  END IF;
END$$;

-- Defaults práticos
ALTER TABLE produtos  ALTER COLUMN ativo     SET DEFAULT TRUE;
ALTER TABLE consumos  ALTER COLUMN data_hora SET DEFAULT now();

-- Checks de sanidade
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_produtos_preco_venda_pos') THEN
    ALTER TABLE produtos ADD CONSTRAINT ck_produtos_preco_venda_pos CHECK (preco_venda >= 0);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_entradas_quantidade_pos') THEN
    ALTER TABLE entradas_estoque ADD CONSTRAINT ck_entradas_quantidade_pos CHECK (quantidade > 0);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_entradas_saldo_nao_neg') THEN
    ALTER TABLE entradas_estoque ADD CONSTRAINT ck_entradas_saldo_nao_neg CHECK (saldo >= 0);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_consumos_quantidade_pos') THEN
    ALTER TABLE consumos ADD CONSTRAINT ck_consumos_quantidade_pos CHECK (quantidade > 0);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_consumos_valores_pos') THEN
    ALTER TABLE consumos ADD CONSTRAINT ck_consumos_valores_pos CHECK (valor_unitario >= 0 AND valor_total >= 0);
  END IF;
END$$;

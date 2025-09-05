DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_entradas_estoque_produtos') THEN
    ALTER TABLE entradas_estoque
      ADD CONSTRAINT fk_entradas_estoque_produtos
      FOREIGN KEY (produto_id) REFERENCES produtos(id);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consumos_clientes') THEN
    ALTER TABLE consumos
      ADD CONSTRAINT fk_consumos_clientes
      FOREIGN KEY (cliente_id) REFERENCES clientes(id);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consumos_produtos') THEN
    ALTER TABLE consumos
      ADD CONSTRAINT fk_consumos_produtos
      FOREIGN KEY (produto_id) REFERENCES produtos(id);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consumos_entrada_estoque') THEN
    ALTER TABLE consumos
      ADD CONSTRAINT fk_consumos_entrada_estoque
      FOREIGN KEY (entrada_estoque_id) REFERENCES entradas_estoque(id);
  END IF;
END$$;

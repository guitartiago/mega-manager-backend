-- Remover a trigger criada no V4
DROP TRIGGER IF EXISTS trg_set_valor_total ON consumos;
DROP FUNCTION IF EXISTS set_valor_total_consumos();

-- Garantir coerência via invariante (sem “processar” nada no DB)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_consumos_valor_total') THEN
    ALTER TABLE consumos
      ADD CONSTRAINT ck_consumos_valor_total
      CHECK (valor_total = quantidade::numeric * valor_unitario);
  END IF;
END$$;
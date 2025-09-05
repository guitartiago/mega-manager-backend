CREATE OR REPLACE FUNCTION set_valor_total_consumos()
RETURNS trigger AS $$
BEGIN
  NEW.valor_total := (NEW.quantidade::numeric * NEW.valor_unitario);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_set_valor_total ON consumos;
CREATE TRIGGER trg_set_valor_total
BEFORE INSERT OR UPDATE OF quantidade, valor_unitario
ON consumos
FOR EACH ROW
EXECUTE FUNCTION set_valor_total_consumos();

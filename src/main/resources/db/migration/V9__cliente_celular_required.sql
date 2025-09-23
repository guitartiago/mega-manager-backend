ALTER TABLE clientes
  ADD COLUMN IF NOT EXISTS celular VARCHAR(20);

-- 2) backfill com valor único e válido (ex.: +55 + id com 11 dígitos)
UPDATE clientes
SET celular = '+55' || lpad(id::text, 11, '0')   -- gera algo como +5500000000123
WHERE celular IS NULL;

-- 3) validação de formato (passa para +55XXXXXXXXXXX)
ALTER TABLE clientes
  ADD CONSTRAINT ck_clientes_celular_formato
  CHECK (celular ~ '^\+?\d{10,13}$');

-- 4) índice único (agora não tem duplicado)
CREATE UNIQUE INDEX IF NOT EXISTS ux_clientes_celular ON clientes (celular);

-- 5) torna obrigatório
ALTER TABLE clientes
  ALTER COLUMN celular SET NOT NULL;
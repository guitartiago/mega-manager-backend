-- (opcional mas recomendado) garante que não haja duplicidade de (usuario_id, perfil)
CREATE UNIQUE INDEX IF NOT EXISTS ux_usuario_perfil
  ON usuario_perfil (usuario_id, perfil);

-- Insere/atualiza o usuário admin e pega o id
WITH upsert AS (
  INSERT INTO usuario (username, senha_hash, ativo)
  VALUES ('admin', '$2a$10$v1h5y7juWbKXcuEltYxtdOtobeaESGIkcVDzp4PnWo2X2WlwqV0bO', true)
  ON CONFLICT (username)
  DO UPDATE SET
    senha_hash = EXCLUDED.senha_hash,
    ativo      = true
  RETURNING id
)
-- Insere as roles necessárias (ignora se já existirem)
INSERT INTO usuario_perfil (usuario_id, perfil)
SELECT id, rol
FROM upsert, unnest(ARRAY['ROLE_ADMIN','ROLE_USER']) AS rol
ON CONFLICT DO NOTHING;
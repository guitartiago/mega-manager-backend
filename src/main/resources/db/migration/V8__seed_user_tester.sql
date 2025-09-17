-- garante que (usuario_id, perfil) não duplique
CREATE UNIQUE INDEX IF NOT EXISTS ux_usuario_perfil
  ON usuario_perfil (usuario_id, perfil);

-- cria/atualiza usuário tester
WITH upsert AS (
  INSERT INTO usuario (username, senha_hash, ativo)
  VALUES ('userTest', '$2a$10$FK6ge8a3pyb8/utcMqpXGO29hxljbIec.RbnjmuBVJMtcW8XSeUha', true)
  ON CONFLICT (username)
  DO UPDATE SET
    senha_hash = EXCLUDED.senha_hash,
    ativo      = true
  RETURNING id
)
INSERT INTO usuario_perfil (usuario_id, perfil)
SELECT id, rol
FROM upsert, unnest(ARRAY['ROLE_USER']) AS rol
ON CONFLICT DO NOTHING;
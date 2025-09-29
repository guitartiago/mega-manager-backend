-- Crie apenas se ainda não houver a tabela de vínculo entre usuários e perfis
CREATE TABLE IF NOT EXISTS usuarios_perfis (
  usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  perfil_id  UUID NOT NULL REFERENCES perfis(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, perfil_id)
);

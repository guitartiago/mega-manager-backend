create table if not exists usuario (
  id bigserial primary key,
  username varchar(80) not null unique,
  senha_hash varchar(100) not null,
  ativo boolean not null default true
);

create table if not exists usuario_perfil (
  usuario_id bigint not null references usuario(id) on delete cascade,
  perfil varchar(40) not null
);
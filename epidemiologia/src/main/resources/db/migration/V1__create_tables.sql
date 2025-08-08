CREATE TABLE alertas (
    id                 BIGSERIAL  PRIMARY KEY,    
    horario_alerta     BIGINT NOT NULL,
    tipo               VARCHAR(255) NOT NULL,
    bairro             VARCHAR(255) NOT NULL,
    bairro_codigo      BIGINT NOT NULL,
    qtd_casos_semanais TEXT
);

CREATE TABLE eventos_clinicos (
    id             BIGSERIAL PRIMARY KEY,
    tipo           VARCHAR(255) NOT NULL,
    codigo_bairro  BIGINT NOT NULL,
    timestamp      BIGINT NOT NULL
);
ALTER SEQUENCE eventos_clinicos_id_seq INCREMENT BY 100;

create table resultado(
idresultado INT primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
texto VARCHAR(500) NOT NULL,
hora timestamp not null)

create table coleta(
idcoleta INT primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,
nome VARCHAR(100),
semana VARCHAR(50),
destinatario VARCHAR(100),
hora timestamp not null,
CONSTRAINT FK_coleta_resultado FOREIGN KEY (idcoleta) REFERENCES resultado(idresultado)
)

create table coleta2(
idcoleta2 INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
tipo VARCHAR(50),
hora timestamp not null,
CONSTRAINT FK_coleta2_resultado FOREIGN KEY (idcoleta2) REFERENCES resultado(idresultado)
)
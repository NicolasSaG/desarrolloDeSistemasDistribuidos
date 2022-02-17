create database servicio_web;
use servicio_web;
create table usuarios(
    id_usuario integer auto_increment primary key,
    email varchar(100) not null,
    nombre varchar(100) not null,
    apellido_paterno varchar(100) not null,
    apellido_materno varchar(100),
    fecha_nacimiento datetime not null,
    telefono varchar(20),
    genero char(1)
);

create table fotos_usuarios
(
    id_foto integer auto_increment primary key,
    foto longblob,
    id_usuario integer not null
);

alter table fotos_usuarios add foreign key (id_usuario) references usuarios(id_usuario);
create unique index usuarios_1 on usuarios(email);

--nuevo usuario
--create user hugo identified by 'contraseña-del-usuario-hugo';
create user hugo identified by 'hugo';
grant all on servicio_web.* to hugo;

--GRANT ALL PRIVILEGES ON servicio_web.* TO 'hugo'@'%' IDENTIFIED BY "hugo";
flush privileges;
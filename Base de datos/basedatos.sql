drop database if exists basedatos;
create database basedatos;
use basedatos;

create table productos (
codigo int primary key auto_increment,
nombre text,
precio double,
categoria text
);

create table mesas (
id int primary key,
ocupada boolean default false
);

create table consumicion (
id int auto_increment,
mesa int,
producto int,
cantidad int,
primary key(id),
foreign key (mesa) references mesas(id),
foreign key (producto) references productos(codigo)
);

create table factura (
id int auto_increment,
mesa int,
fecha timestamp,
importe double,
primary key(id),
foreign key (mesa) references mesas(id)
);

create table detalles_factura (
id int auto_increment,
factura int,
producto int,
cantidad int,
primary key(id),
foreign key (producto) references productos(codigo),
foreign key (factura) references factura(id)
);

insert into productos (nombre,precio,categoria) values
('caña',1.5,'cervezas'),
('clara',1.5,'cervezas'),
('jarra',2,'cervezas'),
('sandy',1.5,'cervezas'),
('sinalcohol',1.5,'cervezas'),
('barril',3,'cervezas'),

('amareto',5,'alcohol'),
('jackdaniels',3,'alcohol'),
('campari',4,'alcohol'),
('baileys',5,'alcohol'),
('anis',5,'alcohol'),
('beefeater',5,'alcohol'),
('larios',5,'alcohol'),
('martini',5,'alcohol'),
('contreau',5,'alcohol'),
('kiwi',5,'alcohol'),
('barcelo',5,'alcohol'),
('bacardi',5,'alcohol'),

('cocacola',2,'refrescos'),
('aquarius',2,'refrescos'),
('nestea',2,'refrescos'),
('fanta',2,'refrescos'),
('sprite',2,'refrescos'),
('te',2,'refrescos'),
('agua',1,'refrescos'),
('tonica',2,'refrescos'),
('mosto',2,'refrescos'),
('redbull',3.5,'refrescos'),

('blanco',3,'vinos'),
('tinto',3,'vinos'),
('rosado',3,'vinos'),
('reserva',3,'vinos'),
('pitarra',1,'vinos'),
('crianza',4,'vinos'),
('juvecamps',4,'vinos'),
('sidra',3,'vinos'),

('capuchino',2.5,'cafes'),
('cortado',2.5,'cafes'),
('descafeinado',2.5,'cafes'),
('chocolate',2,'cafes'),
('colacao',2,'cafes'),
('conleche',2.5,'cafes'),
('carajillo',2.5,'cafes'),
('irlandes',2.5,'cafes'),
('americano',2.5,'cafes'),
('leche',1.5,'cafes'),

('mousse',2,'postres'),
('pistachos',2,'postres'),
('tarta',2,'postres'),
('manzana',1,'postres'),
('naranja',1,'postres'),
('tiramisu',2,'postres'),
('mandarina',1,'postres'),
('profiteroles',2,'postres'),

('patatas',4,'raciones'),
('alitaspollo',4,'raciones'),
('croquetas',4,'raciones'),
('bacalao',4,'raciones'),
('albondigas',4,'raciones'),
('jamon',4,'raciones'),
('morro',4,'raciones'),
('ensaladilla',4,'raciones'),
('calamares',4,'raciones'),
('caracoles',4,'raciones'),
('almejas',4,'raciones'),
('brocheta',4,'raciones'),
('ensaladilla',4,'raciones'),

('bacalao',3,'montaditos'),
('chorizo',3,'montaditos'),
('jamon',3,'montaditos'),
('lomo',3,'montaditos'),
('morzilla',3,'montaditos'),
('salmon',3,'montaditos'),
('anchoa',3,'montaditos'),
('salchicha',3,'montaditos'),
('mojama',3,'montaditos'),
('serrano',3,'montaditos'),

('atun',5,'bocadillos'),
('jamon',5,'bocadillos'),
('lomo',5,'bocadillos'),
('queso',5,'bocadillos'),
('vegetal',5,'bocadillos'),
('torilla',5,'bocadillos'),
('york',5,'bocadillos'),

('croissant',3.5,'desayunos'),
('donut',3.5,'desayunos'),
('normal',2.5,'desayunos'),
('especial',3.5,'desayunos'),
('napolitana',3.5,'desayunos'),
('magdalena',3.5,'desayunos'),
('ensaimada',3.5,'desayunos');

insert into mesas values 
(1,false),
(2,false),
(3,false),
(4,false),
(5,false),
(6,false),
(7,false),
(8,false),
(9,false),
(10,false),
(11,false);
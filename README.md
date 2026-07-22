# GoTogether

GoTogether es una aplicación web para descubrir y organizar eventos y viajes entre
estudiantes universitarios. Está construida como una API REST (Spring + Jersey +
Hibernate + Spring Security) que sirve a una single page application hecha con
React.

La aplicación está deployada en el servidor de la cátedra y se puede acceder
[acá](http://pawserver.it.itba.edu.ar/paw-2025a-10/).

## Build and run

El proyecto se construye con Maven y Java 21. Alcanza con empaquetarlo y servir el
archivo `.war` resultante con Tomcat, teniendo una base PostgreSQL con los permisos
adecuados: la aplicación genera por su cuenta todas las tablas necesarias.

```bash
mvn clean package
```

## Usuarios

En la base de producción tenemos configurados usuarios para cada nivel de acceso de
la aplicación:

| Nivel de acceso | Email                     | Contraseña    |
|-----------------|---------------------------|---------------|
| Administrador   | `paw.2025a.10@gmail.com`  | `admin1234`   |
| Usuario         | `lstupnik@itba.edu.ar`    | `Password123` |
| Usuario         | `lulostup123@gmail.com`   | `Password123` |

El resto del contenido de la aplicación (eventos, viajes, calificaciones) es público
y se puede navegar sin autenticarse.

El administrador puede, además de todo lo que puede un usuario, gestionar los
catálogos de ciudades, carreras, universidades e intereses, resolver los reportes que
levantan los usuarios y bloquear cuentas.

## Integrantes

Este proyecto fue realizado en un entorno académico, como parte de la currícula de
Proyecto de Aplicaciones Web del Instituto Tecnológico de Buenos Aires (ITBA), por:

| Nombre | Legajo |
|--------|--------|
| Ivo Vilamowski | 64210 |
| Nicolás Koron | 64094 |
| Tomás González Colasantti | 63281 |
| Luciano Stupnik | 64233 |
| Matías Rossi Seifert | 63202 |
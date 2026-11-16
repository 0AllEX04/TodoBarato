# TODO BARATO

Proyecto académico con backend Java 17, Spring Boot, Gradle y MySQL 8.0.
El panel administrativo y la aplicación móvil todavía no están implementados.

## Requisitos

- Git y Docker Desktop instalado, iniciado y configurado para contenedores Linux.
- Internet en el primer arranque para descargar imágenes y dependencias.
- Puertos locales 3306 (MySQL) y 8081 (backend) disponibles.

No es necesario instalar Java, Gradle, MySQL Server ni Workbench en el equipo
para ejecutar los servicios con Docker.

## Preparar una copia nueva

```sh
git clone https://github.com/0AllEX04/TodoBarato.git
cd TodoBarato
```

En PowerShell:

```powershell
Copy-Item .env.example .env
```

En Linux o macOS:

```sh
cp .env.example .env
```

`.env.example` contiene credenciales públicas de demostración local. Edita
`.env` si necesitas otros valores. Nunca subas `.env` a Git. `DB_USER` debe ser
un usuario de aplicación, no `root`. Compose lee este archivo automáticamente;
Spring Boot ejecutado directamente desde IntelliJ necesita esas variables en
su configuración de ejecución.

## Arrancar solo la base de datos

```sh
docker compose up -d --wait --wait-timeout 180 mysql-db
docker compose ps
```

Para una prueba local sin crear `.env`, se puede usar el archivo de demostración
explícitamente (es la modalidad utilizada en la comprobación inicial):

```sh
docker compose --env-file .env.example up -d --wait --wait-timeout 180 mysql-db
docker compose --env-file .env.example ps
```

En esa modalidad, agrega `--env-file .env.example` después de `docker compose`
también en los comandos de consulta y diagnóstico siguientes.

En un volumen vacío, MySQL crea la base `todobarato`, un usuario de aplicación
con acceso a esa base e importa el SQL existente. El resultado esperado es:

- Servicio `mysql-db` con estado `healthy`.
- Tablas `marcas` y `productos`.
- Marca inicial Toyota.
- Relación de `productos.id_marca` con `marcas.idmarca`.

La relación actual conserva `ON DELETE CASCADE`: eliminar una marca puede
eliminar sus productos. La regla de negocio se revisará antes de probar borrados.

## Consultar los datos

```sh
docker compose exec mysql-db mysql -u todobarato_app -p todobarato
```

Introduce la contraseña de `DB_PASSWORD` en el aviso interactivo. Si cambiaste
`DB_USER`, utiliza ese nombre. Dentro del cliente:

```sql
SHOW TABLES;
SELECT idmarca, nombre, estado FROM marcas;
SHOW CREATE TABLE productos;
EXIT;
```

La base se publica únicamente en `127.0.0.1:3306`. DB Browser for SQLite no
permite administrar esta base MySQL.

## Persistencia y diagnóstico

Los datos se conservan en el volumen `mysql_todobarato_data` del proyecto Compose.
El SQL de inicialización se ejecuta solo cuando ese volumen está vacío. Editar
el SQL o las contraseñas del archivo `.env` después de la primera inicialización
no actualiza automáticamente las tablas ni las cuentas existentes.

```sh
docker compose logs --tail 100 mysql-db
docker compose stop mysql-db
docker compose start mysql-db
```

Detener o reiniciar el servicio conserva los datos. No uses `down -v` ni elimines
el volumen para resolver un problema sin revisar y respaldar sus datos.

## Arrancar el backend y MySQL

Springdoc se actualizó a 3.1.1 para la familia Spring Boot 4.1. Con `.env`
preparado, construye e inicia los servicios:

```sh
docker compose up --build -d
```

También puedes usar los valores públicos de demostración sin crear `.env`:

```sh
docker compose --env-file .env.example up --build -d
docker compose --env-file .env.example logs --tail 100 backend
```

Espera a que el log muestre `Started TodobaratoApplication`. El estado `Up`
del contenedor no demuestra por sí solo que Spring Boot terminó de arrancar.
El backend espera a que MySQL esté saludable y Hibernate valida el esquema
de las entidades existentes sin modificarlo.

Comprueba estas direcciones en el navegador o con una petición GET en Postman:

- Marcas: http://localhost:8081/api/v1/marcas
- Swagger UI: http://localhost:8081/api/v1/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api/v1/v3/api-docs

En la comprobación del 16 de noviembre de 2026:

- La compilación y generación del JAR dentro de Docker finalizaron correctamente.
- El backend arrancó con Java 17 y conectó a MySQL 8.0.46.
- El listado respondió HTTP 200 con la marca Toyota.
- Swagger respondió HTTP 200 con su HTML y OpenAPI respondió HTTP 200 con las
  rutas `/marcas` y `/marcas/{idmarca}`.

Estas comprobaciones fueron de lectura. No verifican creación, actualización
ni eliminación de marcas. El Dockerfile actual utiliza `-x test`, por lo que
la construcción no ejecuta las pruebas automatizadas. También queda pendiente
revisar las fechas: el registro inicial tiene `03:31:46` en el SQL y la API
devolvió `08:31:46`, un desfase de cinco horas.

Los cambios futuros de esquema deberán aplicarse explícitamente; no se realizan
automáticamente sobre una base que ya contiene datos.

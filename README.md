# Servicio de Pólizas (policy-service)

## Descripción

El repositorio contiene el servicio `policy-service`, una API reactiva (Spring WebFlux) para cotización y emisión de pólizas para mascotas. El servicio sigue una arquitectura hexagonal (puertos y adaptadores) y utiliza R2DBC con PostgreSQL para persistencia y Flyway para migraciones.

## Arquitectura

La aplicación está organizada en capas claras:

- `application.usecase`: Casos de uso (lógica de la aplicación): `CreateQuoteUseCase`, `IssuePolicyUseCase`.
- `domain.model`: Modelos de dominio: `Quote`, `Policy`, `Pet`, `Owner`, `Plan`, `Species`, `PolicyStatus`.
- `domain.ports`: Interfaces (puertos) para repositorios y publicación de eventos.
- `infrastructure.adapters.db`: Implementaciones de repositorios usando Spring Data R2DBC (`QuoteRepositoryAdapter`, `PolicyRepositoryAdapter`).
- `infrastructure.adapters.web`: Controladores HTTP y DTOs (`QuoteController`, `PolicyController`).
- `infrastructure.adapters.events`: Publicador de eventos (en este proyecto se registra en consola).

Diagrama C4

![Diagrama C4](docs/C4%20diagram.png)

El diagrama muestra la topología de componentes y cómo los controladores HTTP invocan casos de uso que a su vez interactúan con los puertos (repositorios y publicadores de eventos). Las adaptaciones concretas (DB, consola) están aisladas en la capa de infraestructura.

## Modelos y comportamiento

Se describen los modelos principales y sus atributos relevantes:

- `Quote`
  - `id` (String): Identificador de la cotización (UUID).
  - `pet` (Pet): Datos de la mascota asociados.
  - `plan` (Plan): Plan seleccionado (`BASIC`, `PREMIUM`).
  - `totalAmount` (BigDecimal): Monto calculado.
  - `expirationDate` (LocalDateTime): Fecha de expiración (30 días desde la creación).

- `Policy`
  - `id` (String): Identificador de la póliza (UUID).
  - `quoteId` (String): ID de la cotización usada para emitir la póliza.
  - `owner` (Owner): Datos del dueño (nombre, documento, email).
  - `status` (PolicyStatus): Estado (`ACTIVE`, etc.).
  - `issuedAt` (LocalDateTime): Marca de tiempo de emisión.

- `Pet`
  - `name`, `species` (`DOG` o `CAT`), `breed`, `age` (int).

- `Plan` (enum)
  - `BASIC` (multiplicador 1.0)
  - `PREMIUM` (multiplicador 2.0)

- `Species` (enum)
  - `DOG` (riskFactor 0.20)
  - `CAT` (riskFactor 0.10)

Lógica de precios (en `PricingService`):
- Partir de un precio base de 10.0.
- Sumar incremento por especie: base * riskFactor.
- Si la mascota tiene más de 5 años, aplicar un 50% adicional.
- Multiplicar por el multiplicador del `Plan`.
- Redondear a 2 decimales.

## Endpoints HTTP

La API expone los siguientes endpoints (base `/api`):

1) Crear cotización

- Método: `POST`
- Ruta: `/api/quotes`
- Request DTO: `QuoteRequest`
  - `petName` (string, obligatorio)
  - `species` (string, obligatorio) ? valores: `dog` o `cat` (no sensible a mayúsculas)
  - `breed` (string, opcional)
  - `age` (int, >= 0)
  - `plan` (string, obligatorio) ? valores: `basic` o `premium`

Ejemplo de petición (curl):

```bash
curl -X POST http://localhost:8080/api/quotes \
  -H "Content-Type: application/json" \
  -d "{\"petName\": \"Fido\", \"species\": \"dog\", \"breed\": \"Labrador\", \"age\": 6, \"plan\": \"premium\"}"
```

Ejemplo de respuesta (HTTP 200):

```json
{
  "id": "<quote-id>",
  "petName": "Fido",
  "totalAmount": 30.00,
  "expirationDate": "2026-02-21T14:30:00"
}
```

2) Emitir póliza

- Método: `POST`
- Ruta: `/api/policies`
- Request DTO: `IssuePolicyRequest`
  - `quoteId` (string, obligatorio)
  - `ownerName` (string, obligatorio)
  - `ownerId` (string, obligatorio)
  - `ownerEmail` (string, obligatorio, formato email)

Ejemplo de petición (curl):

```bash
curl -X POST http://localhost:8080/api/policies \
  -H "Content-Type: application/json" \
  -d "{\"quoteId\": \"<quote-id>\", \"ownerName\": \"María Pérez\", \"ownerId\": \"12345678\", \"ownerEmail\": \"maria.perez@example.com\"}"
```

Ejemplo de respuesta (HTTP 200):

```json
{
  "id": "<policy-id>",
  "status": "ACTIVE",
  "issuedAt": "2026-01-22T15:00:00",
  "message": "Póliza emitida exitosamente. Facturación iniciada."
}
```

Errores comunes:
- Si la `quoteId` no existe, la respuesta será un error con mensaje: "La cotización no existe".
- Si la cotización ha expirado, la respuesta será un error con mensaje: "La cotización ha expirado".
- Validaciones de request usan `jakarta.validation` y retornan errores 400 cuando faltan campos o el email no es válido.

## Cómo levantar el proyecto (Windows - cmd.exe)

Requisitos previos:
- Java 21 (se recomienda usar el `toolchain` de Gradle; sin embargo, disponer de Java 21 facilita la ejecución local).
- PostgreSQL en `localhost:5432` con una base de datos `pet_insurance_db` y usuario `postgres` / contraseña `postgres`, o ajustar `src/main/resources/application.properties`.
- Gradle Wrapper incluido en el repositorio (`gradlew.bat`).

Pasos:

1. Desde la raíz del proyecto, ejecutar las migraciones y compilar:

```bat
gradlew.bat clean build
```

2. Ejecutar la aplicación con el Gradle Wrapper:

```bat
gradlew.bat bootRun
```

La aplicación arrancará por defecto en el puerto 8080. Si se desea cambiar la configuración de la base de datos, editar `src/main/resources/application.properties` o usar variables de entorno (por ejemplo `SPRING_R2DBC_URL`, `SPRING_R2DBC_USERNAME`, `SPRING_R2DBC_PASSWORD`).

Pruebas unitarias e integración:

```bat
gradlew.bat test
```

## Configuración de base de datos

La aplicación utiliza Flyway para ejecutar migraciones al arrancar (configurado en `application.properties`). Las propiedades por defecto apuntan a:

- JDBC (Flyway): `jdbc:postgresql://localhost:5432/pet_insurance_db` (usuario `postgres`, contraseña `admin`).
- R2DBC (runtime): `r2dbc:postgresql://localhost:5432/pet_insurance_db` (usuario `postgres`, contraseña `admin`).

Si no dispone de PostgreSQL local, puede usar Docker Compose si lo desea; en el repositorio existe un `docker-compose.yml` (revisar y adaptar antes de usar).
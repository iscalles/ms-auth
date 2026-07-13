# ms-auth — Colegio Bernardo O'Higgins

Microservicio de autenticación del sistema **Libro de Clases Digital** (DSY1106 Fullstack III).  
Gestiona el ciclo de vida completo de la sesión: login, emisión de JWT, renovación de tokens, logout, recuperación de contraseña y administración de cuentas de acceso.

---

## Responsabilidades

- Autenticar usuarios con RUT y contraseña (BCrypt)
- Emitir access token JWT (15 min) y refresh token (7 días)
- Renovar el access token sin requerir nuevo login
- Revocar sesiones al hacer logout (elimina el refresh token)
- Recuperar contraseña por correo (token de un solo uso, Mailtrap sandbox)
- Inicializar cuentas nuevas con contraseña temporal y flag `debeCambiarPassword`
- Desactivar cuentas al eliminar un usuario (llamado por ms-usuario)

---

## Flujo de autenticación

```
1. POST /auth/login  →  valida RUT + password BCrypt
                    →  consulta ms-usuario para obtener nombre y roles
                    →  emite accessToken (JWT, 15 min) + refreshToken (7 días en BD)
                    →  responde con LoginResponseDTO

2. Petición autenticada  →  BFF valida el JWT localmente (sin llamar a ms-auth)
                         →  inyecta X-User-Id en cada request downstream

3. POST /auth/refresh  →  valida refreshToken en BD
                       →  emite nuevo accessToken
                       →  responde con nuevo LoginResponseDTO

4. POST /auth/logout   →  elimina el refreshToken de la BD
```

---

## Endpoints REST

### Autenticación — `/auth` (Puerto 8082)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | Pública | Autentica con RUT y contraseña, retorna JWT |
| `POST` | `/auth/refresh` | Pública | Renueva el access token usando un refresh token válido |
| `GET` | `/auth/validate` | JWT | Valida si un token es válido (`true`/`false`) |
| `POST` | `/auth/logout` | JWT | Cierra sesión eliminando el refresh token |
| `POST` | `/auth/recuperar-password` | Pública | Envía correo con token de recuperación (responde igual exista o no el correo) |

**Body `POST /auth/login`:**
```json
{ "rutUsuario": "12.345.678-9", "password": "miPassword123" }
```

**Respuesta `LoginResponseDTO`:**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "uuid-...",
  "idUsuario": 5,
  "nombreCompleto": "Juan Pérez",
  "roles": ["DOCENTE"],
  "debeCambiarPassword": false
}
```

### Cuenta de acceso — `/cuenta-acceso`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/cuenta-acceso` | Lista todas las cuentas |
| `GET` | `/cuenta-acceso/{id}` | Busca una cuenta por ID |
| `POST` | `/cuenta-acceso` | Crea una cuenta directamente |
| `PUT` | `/cuenta-acceso/{id}` | Actualiza una cuenta |
| `DELETE` | `/cuenta-acceso/{id}` | Elimina una cuenta |
| `POST` | `/cuenta-acceso/inicializar` | Crea cuenta con contraseña temporal (hashea BCrypt, `debeCambiarPassword=true`) |
| `POST` | `/cuenta-acceso/cambiar-contrasena` | Cambia contraseña validando la actual |
| `PUT` | `/cuenta-acceso/usuario/{idUsuario}/desactivar` | Desactiva la cuenta (llamado por ms-usuario al eliminar usuario) |

**Body `POST /cuenta-acceso/inicializar`:**
```json
{ "idUsuario": 10, "passwordPlano": "Temporal#2025" }
```

### Refresh token — `/refresh-token`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/refresh-token` | Lista todos los refresh tokens |
| `GET` | `/refresh-token/{id}` | Busca por ID |
| `POST` | `/refresh-token` | Crea un refresh token |
| `PUT` | `/refresh-token/{id}` | Actualiza un refresh token |
| `DELETE` | `/refresh-token/{id}` | Elimina un refresh token |

---

## Modelo de datos

### `CuentaAcceso`

| Campo | Tipo | Descripción |
|---|---|---|
| `idCuenta` | `Long` (PK) | Identificador único (secuencia Oracle) |
| `idUsuario` | `Long` (único) | Referencia al usuario en ms-usuario |
| `passwordHash` | `String` | Contraseña hasheada con BCrypt |
| `tokenRecuperacion` | `String` | Token de un solo uso para recuperar contraseña |
| `estadoCuenta` | `String` | `ACTIVO` / `ELIMINADO` |
| `ultimoAcceso` | `LocalDateTime` | Fecha y hora del último login exitoso |
| `debeCambiarPassword` | `boolean` | `true` al crear la cuenta — fuerza cambio en el primer login |

---

## Configuración

```properties
# application.properties
spring.application.name=authService
server.port=8082

# JWT
jwt.secret=<mismo-secret-que-el-BFF>
jwt.expiration=900000          # 15 minutos en ms
jwt.refresh-expiration=604800000  # 7 días en ms

# Oracle Autonomous Database
spring.datasource.url=jdbc:oracle:thin:@proyectolibroasistencia_high?TNS_ADMIN=<ruta_wallet>
spring.datasource.username=ms_auth
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1

# ms-usuario (para obtener nombre y roles en el login)
ms-usuario.url=http://localhost:8081

# Correo (Mailtrap sandbox — no entrega a bandejas reales)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
```

> **Importante:** `jwt.secret` debe coincidir exactamente con el del BFF. Si difieren, todos los tokens emitidos serán rechazados en el perímetro.

---

## Dependencias con otros microservicios

| Microservicio | Tipo | Para qué |
|---|---|---|
| **ms-usuario** (8081) | Feign Client (`UsuarioClient`) | Obtener nombre completo y roles del usuario durante el login |

---

## Ejecución

```bash
# Desde la carpeta authService/
./mvnw spring-boot:run
```

> Requiere conectividad con Oracle Autonomous Database y el Wallet configurado.

---

## Tests unitarios

```bash
./mvnw test -Dtest="CuentaAccesoServiceImplTest" -Dsurefire.failIfNoSpecifiedTests=false
```

| Clase | Tests | Casos cubiertos |
|---|---|---|
| `CuentaAccesoServiceImplTest` | 10 | Inicializar cuenta (ya existe, nueva con email fallido), cambiar contraseña (sin cuenta, password incorrecto, correcto), desactivar cuenta (existe, no existe), actualizar no encontrado, eliminar (existente y no existente) |

Los tests usan `@ExtendWith(MockitoExtension.class)` — no requieren base de datos ni Spring context.

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 3.2.12 | Framework base |
| Spring Data JPA | 3.x | Acceso a base de datos |
| Oracle Autonomous DB | — | Persistencia (esquema `ms_auth`) |
| JJWT | 0.12.6 | Generación y validación de JWT |
| BCrypt | (via Spring Security) | Hash de contraseñas |
| OpenFeign | (via spring-cloud) | Comunicación con ms-usuario |
| Spring Mail | 3.x | Envío de correos de recuperación |
| JUnit 5 + Mockito | (via spring-boot-starter-test) | Tests unitarios |

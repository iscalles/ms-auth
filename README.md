# ms-auth — Microservicio de Autenticación

Colegio Bernardo O'Higgins · Proyecto Libro de Clases Digital

Microservicio responsable de la **autenticación y gestión de sesiones** del sistema. Implementa inicio de sesión con JWT, renovación de tokens, cierre de sesión y gestión de cuentas de acceso. Se comunica con `ms-usuario` para obtener los datos del usuario durante el login.

---

## Responsabilidades

- Validar credenciales (RUT + contraseña) contra la base de datos
- Emitir `accessToken` (JWT, 15 minutos) y `refreshToken` (7 días)
- Revocar refresh tokens al cerrar sesión
- Gestionar cuentas de acceso (`CUENTA_ACCESO`)
- Hashear contraseñas con **BCrypt**

---

## Requisitos previos

| Herramienta | Versión |
|---|---|
| Java JDK | 21 |
| Maven | 3.8 o superior |
| Oracle Autonomous Database | Wallet configurado |
| ms-usuario | Corriendo en `http://localhost:8081` |

---

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/iscalles/ms-auth.git
cd ms-auth/authService

# 2. Copiar el wallet de Oracle a la ruta configurada
# El wallet debe estar en:
# src/main/resources/wallet/Wallet_proyectoLibroAsistencia/

# 3. Compilar
mvn clean package -DskipTests

# 4. Ejecutar
mvn spring-boot:run
```

El servicio inicia en `http://localhost:8082`.

---

## Configuración (`application.properties`)

```properties
server.port=8082

# JWT
jwt.secret=misecretatuysuperaguantadadesdeprotegidodelmundoentero
jwt.expiration=900000          # 15 minutos (en ms)
jwt.refresh-expiration=604800000  # 7 días (en ms)

# Base de datos Oracle (Autonomous Database)
spring.datasource.url=jdbc:oracle:thin:@proyectolibroasistencia_high?TNS_ADMIN=<ruta-wallet>
spring.datasource.username=ms_auth
spring.datasource.password=<contraseña>
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# URL del microservicio de usuarios
ms-usuario.url=http://localhost:8081
```

---

## Endpoints REST

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/login` | Público | Autenticación con RUT y contraseña |
| POST | `/auth/refresh` | Público | Renovar access token con refresh token |
| POST | `/auth/logout` | JWT requerido | Revocar sesión y eliminar refresh tokens |
| GET | `/auth/validate` | JWT requerido | Validar si el token actual es válido |
| GET | `/cuenta-acceso` | JWT requerido | Listar todas las cuentas |
| POST | `/cuenta-acceso/inicializar` | JWT requerido | Crear cuenta con contraseña en texto plano (hashea internamente) |
| POST | `/cuenta-acceso/cambiar-contrasena` | JWT requerido | Cambiar contraseña validando la actual |


---

## Modelo de datos (tablas en `ms_auth`)

| Tabla | Descripción |
|---|---|
| `CUENTA_ACCESO` | Credenciales de acceso (hash BCrypt de contraseña por usuario) |
| `REFRESH_TOKEN` | Tokens de renovación activos por usuario |

---

## Patrones de diseño implementados

| Patrón | Implementación |
|---|---|
| **Proxy** | Consume `ms-usuario` a través de `UsuarioClient` (OpenFeign) para obtener datos del usuario |
| **Strategy** | `PasswordService` encapsula el algoritmo de hashing (BCrypt), separado de la lógica de autenticación |
| **Factory Method** | `JwtService` construye tokens con distintas propiedades (access vs refresh) |
| **Repository** | `CuentaAccesoRepository` y `RefreshTokenRepository` abstraen el acceso a la base de datos |

---

## Tecnologías

- Spring Boot 3.2.12
- Java 21
- Spring Security
- jjwt 0.12.6 (JWT)
- Spring Data JPA + Hibernate
- Oracle Autonomous Database
- OpenFeign (comunicación con ms-usuario)
- BCrypt (hashing de contraseñas)
- Maven (arquetipo `spring-boot-starter-parent`)

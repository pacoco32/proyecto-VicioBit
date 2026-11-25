# Proyecto-VicioBit

API REST enfocada en una tienda de videojuegos.

Proyecto en parejas del módulo **Programación Web Avanzada** (Versión 1).

## Temática

- Tienda de videojuegos, llamada **VicioBit**.

## Integrantes

- Ángel Rodríguez Claver
- Francisco Manuel Cabrerizo Guerrero (Paco)

## Ramas

| Rama | Persona |
|---|---|
| `main` | Versión estable (etiquetas `v1`, `v2`...) |
| `develop` | Integración de las Pull Requests |
| `feature/pacoDAW` | Francisco Manuel |
| `feature/angelDAW` | Ángel |

Flujo de trabajo: cada persona hace commits solo en su rama. Al terminar una tarea se abre una Pull Request hacia `develop`, que revisa y aprueba la otra persona. Al terminar la versión se fusiona `develop` en `main` y se crea la etiqueta `v1`. Los mensajes de commit siguen la convención [Conventional Commits](https://www.conventionalcommits.org/es/) (`feat`, `fix`, `test`, `docs`...).

## Nombre específico de las clases

| Clase genérica | En este proyecto |
|---|---|
| Producto | **Videojuego** |
| Categoria | **Genero** |

Atributos propios de la temática de los videojuegos: `plataforma` y `PEGI`.

> Las rutas de la API mantienen el nombre genérico en el caso de los videojuegos: `/api/productos` y `/api/admin/productos`.

## Tecnologías

- Java 21
- Spring Boot (Spring Web y Spring Security)
- Maven
- Firebase Authentication (verificación de tokens con Firebase Admin SDK)
- Postman (colección de pruebas)

## Estructura del repositorio

```
proyecto-VicioBit/
├── backend/            Proyecto Spring Boot (com.viciobit)
│   └── src/main/java/com/viciobit/
│       ├── config/       SecurityConfig, FirebaseConfig
│       ├── controller/   Controladores (públicos y /api/admin)
│       ├── dto/          DTOs de entrada y salida
│       ├── exception/    Excepciones propias
│       ├── model/        Modelos
│       ├── security/     Filtro de autenticación con Firebase
│       └── service/      Lógica de negocio y datos en memoria
├── postman/            Colección de Postman
├── docs/
│   ├── api.md          Tabla de endpoints
│   └── memorias/       Memorias en PDF de cada versión
└── README.md
```

## Cómo arrancar el proyecto

### 1. Requisitos

- Java 21 y Maven.
- Una cuenta de servicio de Firebase (archivo JSON). **Se facilita en la memoria o entrega de Moodle y no se sube al repositorio.**

### 2. `application.properties`

Copiar la plantilla y editarla:

```bash
cd backend/src/main/resources
cp application-example.properties application.properties
```

Propiedades principales:

```properties
spring.application.name=viciobit
server.port=9090 //Puerto utilizado por Paco para pruebas, cambiar a uno libre
spring.web.error.include-message=always

# Correos de los usuarios administradores (separados por comas)
viciobit.admins=admin@viciobit.com,admin2@viciobit.com
```

`application.properties` no se sube al repositorio (está en `.gitignore`).

### 3. Configurar las credenciales de Firebase

Guardar el JSON de la cuenta de servicio **fuera del repositorio** y definir la variable de entorno `GOOGLE_APPLICATION_CREDENTIALS` con su ruta.

Linux / macOS:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/ruta/a/cuenta-servicio.json"
```

Windows (PowerShell):

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\ruta\a\cuenta-servicio.json"
```

Si se arranca desde un IDE, definir la variable en la configuración de ejecución de la aplicación.

### 4. Ejecutar

```bash
cd backend
./mvnw spring-boot:run
```

En Windows: `mvnw.cmd spring-boot:run`. La API queda disponible en `http://localhost:9090`.

### Ejecutar desde un IDE

Si se arranca desde un IDE en lugar de la terminal, hay que ejecutar la clase `ViciobitApplication` (`backend/src/main/java/com/viciobit/ViciobitApplication.java`) y definir la variable de entorno `GOOGLE_APPLICATION_CREDENTIALS` en su configuración de ejecución.

**IntelliJ IDEA**

1. *Run → Edit Configurations...* y seleccionar `ViciobitApplication`.
2. *Modify options → Environment variables*.
3. Añadir `GOOGLE_APPLICATION_CREDENTIALS=/ruta/a/cuenta-servicio.json`.

**Eclipse / Spring Tool Suite**

1. *Run → Run Configurations...* y seleccionar `ViciobitApplication`.
2. Pestaña *Environment* → *New...*.
3. Nombre: `GOOGLE_APPLICATION_CREDENTIALS`; valor: la ruta al JSON.

**Visual Studio Code**

Añadir la variable en `.vscode/launch.json` (este archivo no debe subirse al repositorio):

```json
{
  "type": "java",
  "name": "ViciobitApplication",
  "request": "launch",
  "mainClass": "com.viciobit.ViciobitApplication",
  "env": {
    "GOOGLE_APPLICATION_CREDENTIALS": "/ruta/a/cuenta-servicio.json"
  }
}
```

En los tres casos hay que haber creado antes `application.properties` a partir de `application-example.properties`. La API arranca en `http://localhost:9090`.

## Autenticación y roles

La autenticación se implementa con **Firebase Authentication**:

1. El cliente inicia sesión en Firebase y obtiene un ID token.
2. Envía el token en cada petición: `Authorization: Bearer <token>`.
3. El filtro `FirebaseAuthenticationFilter` lo verifica con Firebase Admin SDK y asigna el rol:
   - `ROLE_ADMIN` si el correo está en `viciobit.admins`.
   - `ROLE_CLIENTE` en cualquier otro caso.

Todas las rutas requieren autenticación. Las rutas `/api/admin/**` solo las puede usar un administrador.

| Situación | Código |
|---|---|
| Sin token | 401 |
| Token no válido o caducado | 401 |
| Cliente en una ruta `/api/admin/**` | 403 |
| Administrador en una ruta `/api/admin/**` | 200 |

Los ID tokens de Firebase caducan a la hora: si todas las peticiones empiezan a dar 401, hay que volver a hacer login.

## Usuarios de prueba

Cuentas creadas solo para probar la API (Firebase Authentication):

| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | `cliente@viciobit.com` | `123456` |
| Administrador | `admin@viciobit.com` | `123456` |

## Pruebas con Postman

La colección está en `postman/`.

1. Importar el archivo `.json` en Postman.
2. Rellenar la variable de colección **`FIREBASE_API_KEY`** con la clave de API web del proyecto de Firebase (se facilita en la memoria o entrega de Moodle).
3. Ejecutar primero las peticiones **Login - Cliente** y **Login - Admin**. Guardan los tokens en las variables `FIREBASE_TOKEN` y `FIREBASE_ADMIN_TOKEN`, que usan el resto de peticiones.
4. Ejecutar el resto de carpetas: filtros, carrito, géneros y permisos (incluyen los casos que deben fallar: 400, 401, 403, 404 y 409).

## Endpoints

La tabla completa de endpoints (método, ruta, rol, DTO de entrada, DTO de salida y códigos de respuesta) está en [`docs/api.md`](docs/api.md).

## Reglas de negocio principales

- El stock nunca queda en negativo y no se puede añadir al carrito más unidades que el stock.
- El SKU de un videojuego es único.
- No puede haber dos géneros con el mismo nombre y no se puede eliminar un género con videojuegos.
- Eliminar un videojuego lo marca como retirado; los listados públicos solo muestran videojuegos activos.
- Los importes (subtotal, gastos de envío y total) los calcula siempre el servidor.
- Los gastos de envío son configurables en `application.properties`.

## Seguridad del repositorio

No se suben al repositorio la cuenta de servicio de Firebase, la API key ni el `application.properties` real. Solo se incluye `application-example.properties` como plantilla.

Gracias por leer y probar este proyecto que ha costado sudor y lágrimas sacarlo. Un beso <3

Ángel y Paco

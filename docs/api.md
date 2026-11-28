# VicioBit - Documentación de la API

Todas las rutas requieren un token válido (`401` si falta o no es válido).
Las rutas `/api/admin/**` solo las puede usar un `ADMIN` (`403` si es `CLIENTE`).

## Géneros

| Método | Ruta | Descripción | Rol | DTO entrada | DTO salida | Respuestas |
|---|---|---|---|---|---|---|
| GET | /api/generos | Lista todos los géneros | Autenticado | - | `List<GeneroDTO>` | 200 |
| GET | /api/generos/{id} | Consulta un género por su ID | Autenticado | - | `GeneroDTO` | 200, 404 |
| GET | /api/admin/generos | Lista los géneros desde administración | ADMIN | - | `List<GeneroDTO>` | 200 |
| POST | /api/admin/generos | Crea un nuevo género | ADMIN | `GeneroCrearDTO` | `GeneroDTO` | 201, 400, 409 |
| PUT | /api/admin/generos/{id} | Modifica un género existente | ADMIN | `GeneroCrearDTO` | `GeneroDTO` | 200, 400, 404, 409 |
| DELETE | /api/admin/generos/{id} | Elimina un género sin videojuegos | ADMIN | - | - | 204, 404, 409 |

## Videojuegos

| Método | Ruta | Descripción | Rol | DTO entrada | DTO salida | Respuestas |
|---|---|---|---|---|---|---|
| GET | /api/productos/{id} | Detalle de un videojuego activo | Autenticado | - | `VideojuegoDetalleDto` | 200, 404 |
| GET | /api/productos/destacados | Videojuegos activos destacados | Autenticado | - | `List<VideojuegoResumenDto>` | 200 |
| GET | /api/admin/productos | Lista todos, incluidos los retirados | ADMIN | - | `List<VideojuegoDetalleDto>` | 200 |
| POST | /api/admin/productos | Crea un videojuego | ADMIN | `VideojuegoCrearDto` | `VideojuegoDetalleDto` | 201, 400, 409 |
| PUT | /api/admin/productos/{id} | Modifica un videojuego | ADMIN | `VideojuegoCrearDto` | `VideojuegoDetalleDto` | 200, 400, 404, 409 |
| DELETE | /api/admin/productos/{id} | Retira un videojuego del catálogo | ADMIN | - | - | 204, 404 |
| PUT | /api/admin/productos/{id}/stock | Suma o resta stock | ADMIN | `StockDto` | `VideojuegoDetalleDto` | 200, 400, 404, 409 |
| PUT | /api/admin/productos/{id}/reactivar | Vuelve a activar un videojuego retirado | ADMIN | - | `VideojuegoDetalleDto` | 200, 404 |

## Búsqueda, filtros y ordenación

| Método | Ruta | Descripción | Rol | DTO entrada | DTO salida | Respuestas |
|---|---|---|---|---|---|---|
| GET | /api/productos | Busca con filtros combinables (`generoId`, `q`, `precioMin`, `precioMax`, `soloDisponibles`, `plataforma`, `orden`, `direccion`) | Autenticado | `VideojuegoFiltroDTO` (parámetros) | `List<VideojuegoResumenDto>` | 200, 400 |
| GET | /api/generos/{id}/productos | Videojuegos de un género | Autenticado | - | `List<VideojuegoResumenDto>` | 200, 404 |
| GET | /api/productos/sku/{sku} | Busca un videojuego por su SKU | Autenticado | - | `VideojuegoDetalleDto` | 200, 404 |
| GET | /api/admin/productos/stock-bajo?umbral= | Videojuegos con poco stock | ADMIN | - | `List<VideojuegoDetalleDto>` | 200 |

## Carrito

| Método | Ruta | Descripción | Rol | DTO entrada | DTO salida | Respuestas |
|---|---|---|---|---|---|---|
| GET | /api/carrito | Ver el carrito con totales y avisos | Autenticado | - | `CarritoDTO` | 200 |
| POST | /api/carrito/lineas | Añade un videojuego al carrito | Autenticado | `LineaCarritoCrearDTO` | `CarritoDTO` | 200, 400, 404, 409 |
| PUT | /api/carrito/lineas/{videojuegoId} | Cambia la cantidad de una línea | Autenticado | `LineaCarritoCrearDTO` | `CarritoDTO` | 200, 400, 404, 409 |
| DELETE | /api/carrito/lineas/{videojuegoId} | Quita una línea | Autenticado | - | `CarritoDTO` | 200, 404 |
| DELETE | /api/carrito | Vacía el carrito | Autenticado | - | - | 204 |

## Usuarios

| Método | Ruta | Descripción | Rol | DTO entrada | DTO salida | Respuestas |
|---|---|---|---|---|---|---|
| GET | /api/usuarios/me | Datos del usuario autenticado y su rol | Autenticado | - | `UsuarioDto` | 200 |
| PUT | /api/usuarios/me | Modifica nombre y dirección de envío | Autenticado | `UsuarioActualizarDto` | `UsuarioDto` | 200, 400 |
| GET | /api/admin/usuarios | Lista los usuarios | ADMIN | - | `List<UsuarioDto>` | 200 |
| PUT | /api/admin/usuarios/{id}/rol | Cambia el rol de un usuario | ADMIN | `CambiarRolDto` | `UsuarioDto` | 200, 400, 404 |

## Pedidos

| Método | Ruta | Descripción | Rol | DTO entrada | DTO salida | Respuestas |
|---|---|---|---|---|---|---|
| POST | /api/pedidos | Confirma el carrito | Autenticado | `ConfirmarPedidoDto` | `PedidoDto` | 201, 400, 409 |
| GET | /api/pedidos?estado= | Mis pedidos, filtrando por estado | Autenticado | - | `List<PedidoResumenDto>` | 200, 400 |
| GET | /api/pedidos/{id} | Detalle de uno de mis pedidos | Autenticado | - | `PedidoDto` | 200, 404 |
| PUT | /api/pedidos/{id}/cancelar | Cancela uno de mis pedidos | Autenticado | - | `PedidoDto` | 200, 404, 409 |
| GET | /api/admin/pedidos?estado=&desde=&hasta=&email= | Todos los pedidos con filtros | ADMIN | - | `List<PedidoResumenDto>` | 200, 400 |
| GET | /api/admin/pedidos/{id} | Detalle de cualquier pedido | ADMIN | - | `PedidoDto` | 200, 404 |
| PUT | /api/admin/pedidos/{id}/estado | Cambia el estado de un pedido | ADMIN | `CambiarEstadoDto` | `PedidoDto` | 200, 400, 404, 409 |
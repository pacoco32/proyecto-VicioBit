
package com.viciobit.service;

import com.google.firebase.auth.FirebaseToken;
import com.viciobit.dto.CarritoDTO;
import com.viciobit.dto.LineaCarritoDTO;
import com.viciobit.dto.LineaCarritoCrearDTO;
import com.viciobit.exception.ConflictoException;
import com.viciobit.exception.DatosInvalidosException;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Videojuego;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarritoService {

    private final VideojuegoService videojuegoService;

    private final Map<String, CarritoDTO> carritos = new HashMap<>();

    //@Value recoge un valor del propeties y lo vuelca en una variable de Java
    @Value("${viciobit.carrito.max-unidades-por-videojuego}")
    private int maxUnidadesPorVideojuego;

    @Value("${viciobit.envio.coste}")
    private BigDecimal costeEnvio;

    @Value("${viciobit.envio.gratis-desde}")
    private BigDecimal envioGratisDesde;

    public CarritoService(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }


    //PENDIENTE DE INTEGRACIÓN CON ANGELILLO:
    //Cuando esté disponible UsuarioService o como lo llames,
    //sustituir la obtención directa del FirebaseToken
    //por el usuario autenticado que proporcione ese servicio.
    //El carrito se identificará mediante el UID de Firebase.

    //Obtiene el UID del usuario autenticado
    private String obtenerUidUsuario() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        FirebaseToken usuario = (FirebaseToken) authentication.getPrincipal();

        return usuario.getUid();
    }

    //Busca el carrito del usuario o crea uno nuevo
    private CarritoDTO obtenerOCrearCarrito() {

        String uid = obtenerUidUsuario();

        if (!carritos.containsKey(uid)) {
            carritos.put(uid, new CarritoDTO());
        }

        return carritos.get(uid);
    }

    //Consultar el carrito
    public CarritoDTO obtenerCarrito() {

        CarritoDTO carrito = obtenerOCrearCarrito();

        actualizarCarrito(carrito);

        return carrito;
    }

    //Añadir un videojuego al carrito
    public CarritoDTO agregarLinea(LineaCarritoCrearDTO entrada) {

        if (entrada.getVideojuegoId() == null) {
            throw new DatosInvalidosException("Hay que indicar el videojuego");
        }

        validarCantidad(entrada.getCantidad());

        Videojuego videojuego = videojuegoService.buscarPorId(entrada.getVideojuegoId());

        comprobarVideojuegoDisponible(videojuego);

        CarritoDTO carrito = obtenerOCrearCarrito();

        LineaCarritoDTO linea = buscarLinea(carrito, entrada.getVideojuegoId());

        int cantidadNueva = entrada.getCantidad();

        if (linea != null) {
            cantidadNueva += linea.getCantidad();
        }

        comprobarLimite(cantidadNueva);
        comprobarStock(videojuego, cantidadNueva);

        if (linea == null) {

            linea = new LineaCarritoDTO();
            linea.setVideojuego(videojuegoService.aResumen(videojuego));
            linea.setCantidad(cantidadNueva);
            carrito.getLineas().add(linea);

        } else {

            linea.setCantidad(cantidadNueva);
        }

        actualizarCarrito(carrito);

        return carrito;
    }

    //Modificar la cantidad de un videojuego
    public CarritoDTO modificarCantidad(Long videojuegoId, Integer cantidad) {

        validarCantidad(cantidad);

        Videojuego videojuego =videojuegoService.buscarPorId(videojuegoId);

        comprobarVideojuegoDisponible(videojuego);
        comprobarLimite(cantidad);
        comprobarStock(videojuego, cantidad);

        CarritoDTO carrito = obtenerOCrearCarrito();

        LineaCarritoDTO linea = buscarLinea(carrito, videojuegoId);

        if (linea == null) {
            throw new RecursoNotFoundException("El videojuego no está en el carrito");
        }

        linea.setCantidad(cantidad);

        actualizarCarrito(carrito);

        return carrito;
    }

    //Eliminar un videojuego del carrito
    public CarritoDTO eliminarLinea(Long videojuegoId) {

        CarritoDTO carrito = obtenerOCrearCarrito();

        LineaCarritoDTO linea = buscarLinea(carrito, videojuegoId);

        if (linea == null) {
            throw new RecursoNotFoundException("El videojuego no está en el carrito");
        }

        carrito.getLineas().remove(linea);

        actualizarCarrito(carrito);

        return carrito;
    }

    //Vaciar el carrito
    public CarritoDTO vaciarCarrito() {

        CarritoDTO carrito = obtenerOCrearCarrito();

        carrito.getLineas().clear();

        actualizarCarrito(carrito);

        return carrito;
    }

    //Buscar una línea del carrito por ID de videojuego
    private LineaCarritoDTO buscarLinea(
            CarritoDTO carrito, Long videojuegoId) {

        for (LineaCarritoDTO linea : carrito.getLineas()) {

            if (linea.getVideojuego().getId().equals(videojuegoId)) {
                return linea;
            }
        }

        return null;
    }

    //Comprobar que la cantidad es válida
    private void validarCantidad(Integer cantidad) {

        if (cantidad == null || cantidad < 1) {
            throw new DatosInvalidosException("La cantidad debe ser como mínimo 1");
        }
    }

    //Comprobar el límite de unidades por videojuego
    private void comprobarLimite(int cantidad) {

        if (cantidad > maxUnidadesPorVideojuego) {
            throw new ConflictoException("Se ha superado el máximo de unidades permitido");
        }
    }

    //Comprobar que el videojuego está activo
    private void comprobarVideojuegoDisponible(Videojuego videojuego) {

        if (!videojuego.isActivo()) {
            throw new ConflictoException("El videojuego está retirado y no se puede añadir");
        }
    }

    //Comprobar que hay stock suficiente
    private void comprobarStock(Videojuego videojuego, int cantidad) {

        if (cantidad > videojuego.getStock()) {
            throw new ConflictoException("No hay stock suficiente para esa cantidad");
        }
    }

    //Actualizar importes y avisos del carrito
    private void actualizarCarrito(CarritoDTO carrito) {

        BigDecimal subtotal = BigDecimal.ZERO;
        int numeroArticulos = 0;

        List<String> avisos = new ArrayList<>();

        for (LineaCarritoDTO linea : carrito.getLineas()) {

            Videojuego videojuego = videojuegoService.buscarPorId(linea.getVideojuego().getId());

            BigDecimal subtotalLinea = videojuego.getPrecio().multiply(BigDecimal.valueOf(linea.getCantidad()));

            linea.setVideojuego(videojuegoService.aResumen(videojuego));
            linea.setSubtotal(subtotalLinea);

            subtotal = subtotal.add(subtotalLinea);
            numeroArticulos += linea.getCantidad();

            if (!videojuego.isActivo()) {
                avisos.add(videojuego.getNombre() + " está retirado");
            }

            if (linea.getCantidad() > videojuego.getStock()) {
                avisos.add("No hay stock suficiente de " + videojuego.getNombre());
            }
        }

        BigDecimal gastosEnvio = BigDecimal.ZERO;

        if (!carrito.getLineas().isEmpty() && subtotal.compareTo(envioGratisDesde) < 0) {

            gastosEnvio = costeEnvio;
        }

        BigDecimal total = subtotal.add(gastosEnvio);

        BigDecimal importeParaEnvioGratis = envioGratisDesde.subtract(subtotal);

        if (importeParaEnvioGratis.compareTo(BigDecimal.ZERO) < 0) {
            importeParaEnvioGratis = BigDecimal.ZERO;
        }

        carrito.setNumeroArticulos(numeroArticulos);
        carrito.setSubtotal(subtotal);
        carrito.setGastosEnvio(gastosEnvio);
        carrito.setTotal(total);
        carrito.setImporteParaEnvioGratis(importeParaEnvioGratis);
        carrito.setAvisos(avisos);
    }
}

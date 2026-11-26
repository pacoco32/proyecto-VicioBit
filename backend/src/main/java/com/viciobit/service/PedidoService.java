package com.viciobit.service;

import com.viciobit.dto.CambiarEstadoDto;
import com.viciobit.dto.ConfirmarPedidoDto;
import com.viciobit.dto.LineaPedidoDto;
import com.viciobit.dto.PedidoDto;
import com.viciobit.dto.PedidoResumenDto;
import com.viciobit.exception.ConflictoException;
import com.viciobit.exception.DatosInvalidosException;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Carrito;
import com.viciobit.model.EstadoPedido;
import com.viciobit.model.LineaCarrito;
import com.viciobit.model.LineaPedido;
import com.viciobit.model.Pedido;
import com.viciobit.model.Usuario;
import com.viciobit.model.Videojuego;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final VideojuegoService videojuegoService;
    private final CarritoService carritoService;
    private final UsuarioActualService usuarioActualService;
    private final BigDecimal costeEnvio;
    private final BigDecimal envioGratisDesde;

    private final List<Pedido> pedidos = new ArrayList<>();
    private Long siguienteId = 1L;

    public PedidoService(VideojuegoService videojuegoService,
                         CarritoService carritoService,
                         UsuarioActualService usuarioActualService,
                         @Value("${tienda.envio.coste:4.99}") BigDecimal costeEnvio,
                         @Value("${tienda.envio.gratis-desde:50.00}") BigDecimal envioGratisDesde) {
        this.videojuegoService = videojuegoService;
        this.carritoService = carritoService;
        this.usuarioActualService = usuarioActualService;
        this.costeEnvio = costeEnvio;
        this.envioGratisDesde = envioGratisDesde;
    }

    // cliente

    public PedidoDto confirmar(ConfirmarPedidoDto entrada) {
        Usuario usuario = usuarioActualService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoDe(usuario);

        if (carrito.getLineas().isEmpty()) {
            throw new ConflictoException("El carrito está vacío");
        }
        String direccion = elegirDireccion(entrada, usuario);
        comprobarStock(carrito);

        Pedido pedido = new Pedido();
        pedido.setId(siguienteId);
        pedido.setCodigo(crearCodigo(siguienteId));
        siguienteId++;
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccionEnvio(direccion);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineaCarrito lineaCarrito : carrito.getLineas()) {
            int cantidad = lineaCarrito.getCantidad();
            Videojuego videojuego = videojuegoService.buscarPorId(lineaCarrito.getVideojuego().getId());

            LineaPedido linea = new LineaPedido();
            linea.setVideojuego(videojuego);
            linea.setNombreVideojuego(videojuego.getNombre());
            linea.setPrecioUnitario(videojuego.getPrecio());
            linea.setCantidad(cantidad);
            linea.setSubtotal(videojuego.getPrecio().multiply(new BigDecimal(cantidad)));
            pedido.getLineas().add(linea);

            subtotal = subtotal.add(linea.getSubtotal());
            videojuegoService.descontarStock(videojuego.getId(), cantidad);
        }

        BigDecimal envio = costeEnvio;
        if (subtotal.compareTo(envioGratisDesde) >= 0) {
            envio = BigDecimal.ZERO;
        }
        pedido.setSubtotal(subtotal);
        pedido.setGastosEnvio(envio);
        pedido.setTotal(subtotal.add(envio));

        pedidos.add(pedido);
        carritoService.vaciarCarritoDe(usuario);
        return aDto(pedido);
    }

    public List<PedidoResumenDto> listarMios(EstadoPedido estado) {
        Usuario usuario = usuarioActualService.obtenerUsuarioActual();
        List<PedidoResumenDto> resultado = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            boolean esMio = pedido.getUsuario().getId().equals(usuario.getId());
            boolean coincideEstado = estado == null || pedido.getEstado() == estado;
            if (esMio && coincideEstado) {
                resultado.add(aResumen(pedido));
            }
        }
        return resultado;
    }

    // un pedido de otro usuario se trata como si no existiera
    public PedidoDto obtenerMio(Long id) {
        Usuario usuario = usuarioActualService.obtenerUsuarioActual();
        return aDto(buscarDeUsuario(id, usuario));
    }

    public PedidoDto cancelar(Long id) {
        Usuario usuario = usuarioActualService.obtenerUsuarioActual();
        Pedido pedido = buscarDeUsuario(id, usuario);
        cambiarEstadoDePedido(pedido, EstadoPedido.CANCELADO);
        return aDto(pedido);
    }

    // administración

    public List<PedidoResumenDto> listarTodos(EstadoPedido estado, LocalDate desde, LocalDate hasta, String email) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException("La fecha desde no puede ser posterior a la fecha hasta");
        }

        List<PedidoResumenDto> resultado = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            boolean cumple = true;
            if (estado != null && pedido.getEstado() != estado) {
                cumple = false;
            }
            if (desde != null && pedido.getFecha().isBefore(desde)) {
                cumple = false;
            }
            if (hasta != null && pedido.getFecha().isAfter(hasta)) {
                cumple = false;
            }
            if (email != null && !email.isBlank()) {
                String correo = pedido.getUsuario().getEmail();
                if (correo == null || !correo.toLowerCase().contains(email.trim().toLowerCase())) {
                    cumple = false;
                }
            }
            if (cumple) {
                resultado.add(aResumen(pedido));
            }
        }
        return resultado;
    }

    public PedidoDto obtenerCualquiera(Long id) {
        return aDto(buscarPorId(id));
    }

    public PedidoDto cambiarEstado(Long id, CambiarEstadoDto entrada) {
        Pedido pedido = buscarPorId(id);
        if (entrada.getEstado() == null) {
            throw new DatosInvalidosException("Hay que indicar el estado");
        }
        cambiarEstadoDePedido(pedido, entrada.getEstado());
        return aDto(pedido);
    }

    // auxiliares

    // la transición la decide el enumerado; al cancelar se repone el stock
    private void cambiarEstadoDePedido(Pedido pedido, EstadoPedido nuevo) {
        if (!pedido.getEstado().puedeCambiarA(nuevo)) {
            throw new ConflictoException("No se puede pasar el pedido de " + pedido.getEstado() + " a " + nuevo);
        }
        if (nuevo == EstadoPedido.CANCELADO) {
            for (LineaPedido linea : pedido.getLineas()) {
                videojuegoService.reponerStock(linea.getVideojuego().getId(), linea.getCantidad());
            }
        }
        pedido.setEstado(nuevo);
    }

    // si algún videojuego falla no se modifica nada
    private void comprobarStock(Carrito carrito) {
        String problemas = "";
        for (LineaCarrito linea : carrito.getLineas()) {
            Videojuego videojuego = videojuegoService.buscarPorId(linea.getVideojuego().getId());
            if (!videojuego.isActivo() || videojuego.getStock() < linea.getCantidad()) {
                if (!problemas.isEmpty()) {
                    problemas = problemas + ", ";
                }
                problemas = problemas + videojuego.getNombre();
            }
        }
        if (!problemas.isEmpty()) {
            throw new ConflictoException("No hay stock suficiente de: " + problemas);
        }
    }

    // la dirección del cuerpo y, si no hay, la del perfil
    private String elegirDireccion(ConfirmarPedidoDto entrada, Usuario usuario) {
        if (entrada != null && entrada.getDireccionEnvio() != null && !entrada.getDireccionEnvio().isBlank()) {
            return entrada.getDireccionEnvio().trim();
        }
        if (usuario.getDireccionEnvio() != null && !usuario.getDireccionEnvio().isBlank()) {
            return usuario.getDireccionEnvio();
        }
        throw new DatosInvalidosException("Hay que indicar una dirección de envío");
    }

    private String crearCodigo(Long numero) {
        return String.format("PED-%d-%04d", LocalDate.now().getYear(), numero);
    }

    private Pedido buscarPorId(Long id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId().equals(id)) {
                return pedido;
            }
        }
        throw new RecursoNotFoundException("No existe el pedido " + id);
    }

    private Pedido buscarDeUsuario(Long id, Usuario usuario) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId().equals(id) && pedido.getUsuario().getId().equals(usuario.getId())) {
                return pedido;
            }
        }
        throw new RecursoNotFoundException("No existe el pedido " + id);
    }

    private PedidoResumenDto aResumen(Pedido pedido) {
        PedidoResumenDto dto = new PedidoResumenDto();
        dto.setId(pedido.getId());
        dto.setCodigo(pedido.getCodigo());
        dto.setUsuarioEmail(pedido.getUsuario().getEmail());
        dto.setFecha(pedido.getFecha());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        return dto;
    }

    private PedidoDto aDto(Pedido pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setId(pedido.getId());
        dto.setCodigo(pedido.getCodigo());
        dto.setUsuarioEmail(pedido.getUsuario().getEmail());
        dto.setFecha(pedido.getFecha());
        dto.setEstado(pedido.getEstado());
        dto.setDireccionEnvio(pedido.getDireccionEnvio());
        dto.setSubtotal(pedido.getSubtotal());
        dto.setGastosEnvio(pedido.getGastosEnvio());
        dto.setTotal(pedido.getTotal());
        for (LineaPedido linea : pedido.getLineas()) {
            LineaPedidoDto lineaDto = new LineaPedidoDto();
            lineaDto.setVideojuegoId(linea.getVideojuego().getId());
            lineaDto.setNombreVideojuego(linea.getNombreVideojuego());
            lineaDto.setPrecioUnitario(linea.getPrecioUnitario());
            lineaDto.setCantidad(linea.getCantidad());
            lineaDto.setSubtotal(linea.getSubtotal());
            dto.getLineas().add(lineaDto);
        }
        return dto;
    }
}
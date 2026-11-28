package com.viciobit.service;

import com.viciobit.dto.StockDto;
import com.viciobit.dto.VideojuegoCrearDto;
import com.viciobit.dto.VideojuegoDetalleDto;
import com.viciobit.dto.VideojuegoResumenDto;
import com.viciobit.dto.VideojuegoFiltroDTO;
import com.viciobit.exception.ConflictoException;
import com.viciobit.exception.DatosInvalidosException;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Direccion;
import com.viciobit.model.Genero;
import com.viciobit.model.OrdenVideojuego;
import com.viciobit.model.Plataforma;
import com.viciobit.model.Videojuego;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideojuegoService {

    private final GeneroService generoService;
    private final List<Videojuego> videojuegos = new ArrayList<>();
    private Long siguienteId = 1L;

    public VideojuegoService(GeneroService generoService) {
        this.generoService = generoService;
        cargarDatosDeEjemplo();
    }

    // consultas públicas

    public VideojuegoDetalleDto obtenerActivo(Long id) {
        Videojuego videojuego = buscarPorId(id);
        if (!videojuego.isActivo()) {
            throw new RecursoNotFoundException("No existe el videojuego " + id);
        }
        return aDetalle(videojuego);
    }

    public List<VideojuegoResumenDto> listarDestacados() {
        List<VideojuegoResumenDto> resultado = new ArrayList<>();
        for (Videojuego videojuego : videojuegos) {
            if (videojuego.isActivo() && videojuego.isDestacado()) {
                resultado.add(aResumen(videojuego));
            }
        }
        return resultado;
    }

    // administración

    public List<VideojuegoDetalleDto> listarTodos() {
        List<VideojuegoDetalleDto> resultado = new ArrayList<>();
        for (Videojuego videojuego : videojuegos) {
            resultado.add(aDetalle(videojuego));
        }
        return resultado;
    }

    public VideojuegoDetalleDto crear(VideojuegoCrearDto entrada) {
        validar(entrada);
        Genero genero = buscarGenero(entrada.getGeneroId());
        String sku = entrada.getSku().trim().toUpperCase();
        comprobarSkuLibre(sku, null);

        Videojuego videojuego = new Videojuego();
        videojuego.setId(siguienteId);
        siguienteId++;
        videojuego.setActivo(true);
        videojuego.setFechaAlta(LocalDate.now());
        rellenar(videojuego, entrada, sku, genero);

        videojuegos.add(videojuego);
        return aDetalle(videojuego);
    }

    public VideojuegoDetalleDto modificar(Long id, VideojuegoCrearDto entrada) {
        Videojuego videojuego = buscarPorId(id);
        validar(entrada);
        Genero genero = buscarGenero(entrada.getGeneroId());
        String sku = entrada.getSku().trim().toUpperCase();
        comprobarSkuLibre(sku, id);

        rellenar(videojuego, entrada, sku, genero);
        return aDetalle(videojuego);
    }

    // no se borra, se marca como retirado
    public void retirar(Long id) {
        Videojuego videojuego = buscarPorId(id);
        videojuego.setActivo(false);
    }

    public VideojuegoDetalleDto reactivar(Long id) {
        Videojuego videojuego = buscarPorId(id);
        videojuego.setActivo(true);
        return aDetalle(videojuego);
    }

    // cantidad positiva suma, negativa resta
    public VideojuegoDetalleDto cambiarStock(Long id, StockDto entrada) {
        Videojuego videojuego = buscarPorId(id);
        if (entrada.getCantidad() == null) {
            throw new DatosInvalidosException("Hay que indicar la cantidad");
        }
        int nuevoStock = videojuego.getStock() + entrada.getCantidad();
        if (nuevoStock < 0) {
            throw new ConflictoException("El stock no puede quedar en negativo");
        }
        videojuego.setStock(nuevoStock);
        return aDetalle(videojuego);
    }

    // usados por otros servicios

    public Videojuego buscarPorId(Long id) {
        for (Videojuego videojuego : videojuegos) {
            if (videojuego.getId().equals(id)) {
                return videojuego;
            }
        }
        throw new RecursoNotFoundException("No existe el videojuego " + id);
    }

    public List<Videojuego> listarActivos() {
        List<Videojuego> resultado = new ArrayList<>();
        for (Videojuego videojuego : videojuegos) {
            if (videojuego.isActivo()) {
                resultado.add(videojuego);
            }
        }
        return resultado;
    }

    //FILTROS//
    //Filtra videojuegos según los criterios recibidos
    public List<Videojuego> filtrarVideojuegos(
            List<Videojuego> videojuegos,
            VideojuegoFiltroDTO filtros) {

        //Validar rango de precios
        if (filtros.getPrecioMin() != null &&
                filtros.getPrecioMax() != null &&
                filtros.getPrecioMin().compareTo(filtros.getPrecioMax()) > 0) {

            throw new DatosInvalidosException(
                    "El precio mínimo no puede ser superior al máximo");
        }

        return videojuegos.stream()

                //Filtro por género
                .filter(v -> filtros.getGeneroId() == null ||
                        (v.getGenero() != null &&
                                v.getGenero().getId() != null &&
                                v.getGenero().getId().equals(filtros.getGeneroId())))

                //Filtro por texto
                .filter(v -> filtros.getQ() == null ||
                        filtros.getQ().isBlank() ||
                        (v.getNombre() != null &&
                                normalizarTexto(v.getNombre()).contains(
                                        normalizarTexto(filtros.getQ()))) ||
                        (v.getDescripcion() != null &&
                                normalizarTexto(v.getDescripcion()).contains(
                                        normalizarTexto(filtros.getQ()))))

                //Filtro por precio mínimo
                .filter(v -> filtros.getPrecioMin() == null ||
                        (v.getPrecio() != null &&
                                v.getPrecio().compareTo(filtros.getPrecioMin()) >= 0))

                //Filtro por precio máximo
                .filter(v -> filtros.getPrecioMax() == null ||
                        (v.getPrecio() != null &&
                                v.getPrecio().compareTo(filtros.getPrecioMax()) <= 0))

                //Filtro por disponibilidad
                .filter(v -> !Boolean.TRUE.equals(filtros.getSoloDisponibles()) ||
                        (v.getStock() != null && v.getStock() > 0))

                //Filtro por plataforma
                .filter(v -> filtros.getPlataforma() == null ||
                        v.getPlataforma() == filtros.getPlataforma())

                .toList();
    }

    //Normalizar texto
    private String normalizarTexto(String texto) {
        return java.text.Normalizer.normalize(
                texto.toLowerCase(),
                java.text.Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");
    }

    // ordena una copia de la lista con el método de la burbuja
    public List<Videojuego> ordenar(List<Videojuego> lista, OrdenVideojuego orden, Direccion direccion) {
        List<Videojuego> resultado = new ArrayList<>(lista);
        for (int i = 0; i < resultado.size() - 1; i++) {
            for (int j = 0; j < resultado.size() - 1 - i; j++) {
                Videojuego a = resultado.get(j);
                Videojuego b = resultado.get(j + 1);
                int comparacion = comparar(a, b, orden);
                boolean intercambiar;
                if (direccion == Direccion.DESC) {
                    intercambiar = comparacion < 0;
                } else {
                    intercambiar = comparacion > 0;
                }
                if (intercambiar) {
                    resultado.set(j, b);
                    resultado.set(j + 1, a);
                }
            }
        }
        return resultado;
    }

    public VideojuegoResumenDto aResumen(Videojuego videojuego) {
        VideojuegoResumenDto dto = new VideojuegoResumenDto();
        dto.setId(videojuego.getId());
        dto.setNombre(videojuego.getNombre());
        dto.setPrecio(videojuego.getPrecio());
        dto.setImagenUrl(videojuego.getImagenUrl());
        dto.setGeneroNombre(videojuego.getGenero().getNombre());
        dto.setPlataforma(videojuego.getPlataforma());
        dto.setDisponible(videojuego.getStock() > 0);
        return dto;
    }

    // auxiliares

    private int comparar(Videojuego a, Videojuego b, OrdenVideojuego orden) {
        if (orden == OrdenVideojuego.PRECIO) {
            return a.getPrecio().compareTo(b.getPrecio());
        }
        if (orden == OrdenVideojuego.NOVEDAD) {
            return a.getFechaAlta().compareTo(b.getFechaAlta());
        }
        return a.getNombre().compareToIgnoreCase(b.getNombre());
    }

    private void validar(VideojuegoCrearDto entrada) {
        if (entrada.getSku() == null || entrada.getSku().isBlank()) {
            throw new DatosInvalidosException("El SKU es obligatorio");
        }
        if (entrada.getNombre() == null || entrada.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        if (entrada.getPrecio() == null || entrada.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El precio debe ser mayor que 0");
        }
        if (entrada.getStock() == null || entrada.getStock() < 0) {
            throw new DatosInvalidosException("El stock no puede ser negativo");
        }
        if (entrada.getGeneroId() == null) {
            throw new DatosInvalidosException("El género es obligatorio");
        }
        if (entrada.getPlataforma() == null) {
            throw new DatosInvalidosException("La plataforma es obligatoria");
        }
        if (entrada.getPegi() == null || !pegiValido(entrada.getPegi())) {
            throw new DatosInvalidosException("El PEGI debe ser 3, 7, 12, 16 o 18");
        }
    }

    private boolean pegiValido(int pegi) {
        return pegi == 3 || pegi == 7 || pegi == 12 || pegi == 16 || pegi == 18;
    }

        // género inexistente: 400
    private Genero buscarGenero(Long generoId) {
        try {
            return generoService.buscarPorId(generoId);
        } catch (RecursoNotFoundException e) {
            throw new DatosInvalidosException("El género indicado no existe");
        }
    }

    // idActual es null al crear
    private void comprobarSkuLibre(String sku, Long idActual) {
        for (Videojuego videojuego : videojuegos) {
            boolean esElMismo = idActual != null && videojuego.getId().equals(idActual);
            if (!esElMismo && videojuego.getSku().equals(sku)) {
                throw new ConflictoException("Ya existe un videojuego con el SKU " + sku);
            }
        }
    }

    private void rellenar(Videojuego videojuego, VideojuegoCrearDto entrada, String sku, Genero genero) {
        videojuego.setSku(sku);
        videojuego.setNombre(entrada.getNombre().trim());
        videojuego.setDescripcion(entrada.getDescripcion());
        videojuego.setPrecio(entrada.getPrecio());
        videojuego.setStock(entrada.getStock());
        videojuego.setImagenUrl(entrada.getImagenUrl());
        videojuego.setDestacado(entrada.isDestacado());
        videojuego.setGenero(genero);
        videojuego.setPlataforma(entrada.getPlataforma());
        videojuego.setPegi(entrada.getPegi());
    }

    private VideojuegoDetalleDto aDetalle(Videojuego videojuego) {
        VideojuegoDetalleDto dto = new VideojuegoDetalleDto();
        dto.setId(videojuego.getId());
        dto.setSku(videojuego.getSku());
        dto.setNombre(videojuego.getNombre());
        dto.setDescripcion(videojuego.getDescripcion());
        dto.setPrecio(videojuego.getPrecio());
        dto.setStock(videojuego.getStock());
        dto.setImagenUrl(videojuego.getImagenUrl());
        dto.setActivo(videojuego.isActivo());
        dto.setDestacado(videojuego.isDestacado());
        dto.setFechaAlta(videojuego.getFechaAlta());
        dto.setGeneroId(videojuego.getGenero().getId());
        dto.setGeneroNombre(videojuego.getGenero().getNombre());
        dto.setPlataforma(videojuego.getPlataforma());
        dto.setPegi(videojuego.getPegi());
        return dto;
    }

    // datos de ejemplo (cambiar las URL por imágenes reales)
    private void cargarDatosDeEjemplo() {
        agregarEjemplo("SW-001", "The Legend of Zelda: Tears of the Kingdom", "Aventura de mundo abierto en Hyrule.",
                "59.99", 15, "https://media.vandal.net/i/1440x1080/9-2022/202292911251929_1.jpg", true, true, 1L, Plataforma.SWITCH, 12, 40);
        agregarEjemplo("SW-002", "Super Mario Odyssey", "Plataformas 3D con Mario.",
                "49.99", 20, "https://gaming-cdn.com/images/products/2618/orig/super-mario-odyssey-switch-nintendo-eshop-cover.jpg?v=1755159411", true, true, 1L, Plataforma.SWITCH, 7, 90);
        agregarEjemplo("SW-003", "Mario Kart 8 Deluxe", "Carreras con objetos para jugar en familia.",
                "52.95", 22, "https://difmark.com/images/product/6/3/36366/mario-kart-8-deluxe-nintendo_icon-1200-600_10.jpg", true, true, 3L, Plataforma.SWITCH, 3, 15);
        agregarEjemplo("PS-001", "God of War Ragnarök", "Kratos y Atreus se enfrentan al fin del mundo.",
                "69.99", 10, "https://gaming-cdn.com/images/products/9312/orig/god-of-war-ragnarok-playstation-5-juego-playstation-store-europe-cover.jpg?v=1730134893", true, true, 2L, Plataforma.PS5, 18, 70);
        agregarEjemplo("PS-002", "Astro Bot", "Plataformas con muchos guiños a la historia de PlayStation.",
                "59.99", 8, "https://image.api.playstation.com/vulcan/ap/rnd/202406/0500/8f15268257b878597757fcc5f2c9545840867bc71fc863b1.png", true, false, 1L, Plataforma.PS5, 7, 30);
        agregarEjemplo("PS-003", "EA Sports FC 25", "Simulador de fútbol con equipos y ligas oficiales.",
                "69.99", 18, "https://image.api.playstation.com/vulcan/ap/rnd/202503/2520/f3c135f8ebdc50b782c6f5f02b27130b499e22847f05aee6.png", true, false, 3L, Plataforma.PS5, 3, 20);
        agregarEjemplo("PS-004", "Gran Turismo 7", "Simulador de conducción con cientos de coches.",
                "39.99", 6, "https://image.api.playstation.com/vulcan/ap/rnd/202511/0707/9e4e76f597c62f2591b312164ba164e04d2e96a016ec3208.jpg", false, false, 3L, Plataforma.PS5, 3, 180);   // retirado
        agregarEjemplo("XB-001", "Halo Infinite", "Disparos en primera persona con el Jefe Maestro.",
                "39.99", 0, "https://store-images.s-microsoft.com/image/apps.21536.13727851868390641.c9cc5f66-aff8-406c-af6b-440838730be0.68796bde-cbf5-4eaa-a299-011417041da6", true, false, 2L, Plataforma.XBOX, 16, 120);   // sin stock
        agregarEjemplo("XB-002", "Forza Horizon 5", "Carreras en mundo abierto por México.",
                "49.99", 14, "https://image.api.playstation.com/vulcan/ap/rnd/202502/1900/631436cfbc1d64659c778e3783f29fafad6022145e0ffec8.jpg", true, true, 3L, Plataforma.XBOX, 3, 100);
        agregarEjemplo("PC-001", "Hades", "Acción roguelike en el inframundo griego.",
                "24.99", 30, "https://image.api.playstation.com/vulcan/ap/rnd/202104/0517/9AcM3vy5t77zPiJyKHwRfnNT.png", true, false, 2L, Plataforma.PC, 12, 150);
        agregarEjemplo("PC-002", "Hollow Knight", "Aventura de exploración en un reino de insectos.",
                "14.99", 25, "https://image.api.playstation.com/vulcan/ap/rnd/202601/1505/db56a6084b7472aa32ed311aac24889948fb12a5f8d5006d.png", true, false, 1L, Plataforma.PC, 7, 200);
        agregarEjemplo("PC-003", "Doom Eternal", "Disparos frenéticos contra los demonios.",
                "19.99", 0, "https://store-images.s-microsoft.com/image/apps.33238.14607483449828807.1bf8995c-7221-490e-a6d4-8718054337ce.b335eb86-4cf4-4aca-96ec-8a1dbc19e496", true, false, 2L, Plataforma.PC, 18, 250);   // sin stock
    }

    private void agregarEjemplo(String sku, String nombre, String descripcion, String precio, int stock,
                                String imagenUrl, boolean activo, boolean destacado, Long generoId,
                                Plataforma plataforma, int pegi, int diasDeAntiguedad) {
        Videojuego videojuego = new Videojuego();
        videojuego.setId(siguienteId);
        siguienteId++;
        videojuego.setSku(sku);
        videojuego.setNombre(nombre);
        videojuego.setDescripcion(descripcion);
        videojuego.setPrecio(new BigDecimal(precio));
        videojuego.setStock(stock);
        videojuego.setImagenUrl(imagenUrl);
        videojuego.setActivo(activo);
        videojuego.setDestacado(destacado);
        videojuego.setFechaAlta(LocalDate.now().minusDays(diasDeAntiguedad));
        videojuego.setGenero(generoService.buscarPorId(generoId));
        videojuego.setPlataforma(plataforma);
        videojuego.setPegi(pegi);
        videojuegos.add(videojuego);
    }

        // lo usa PedidoService al confirmar un pedido
    public void descontarStock(Long id, int cantidad) {
        Videojuego videojuego = buscarPorId(id);
        if (videojuego.getStock() < cantidad) {
            throw new ConflictoException("No hay stock suficiente de " + videojuego.getNombre());
        }
        videojuego.setStock(videojuego.getStock() - cantidad);
    }

    // lo usa PedidoService al cancelar un pedido
    public void reponerStock(Long id, int cantidad) {
        Videojuego videojuego = buscarPorId(id);
        videojuego.setStock(videojuego.getStock() + cantidad);
    }

    //Comprueba si hay videojuegos asociados a un género
    public boolean tieneVideojuegosAsociados(Long generoId) {
        for (Videojuego videojuego : videojuegos) {
            if (videojuego.getGenero().getId().equals(generoId)) {
                return true;
            }
        }
        return false;
    }

    //Se encarga de coordinar el filtrado y la conversión. Utiliza los filtros creados anteriormente
    public List<VideojuegoResumenDto> listarFiltrados(VideojuegoFiltroDTO filtros) {

        List<Videojuego> videojuegosFiltrados =
                filtrarVideojuegos(listarActivos(), filtros);

        List<VideojuegoResumenDto> resultado = new ArrayList<>();

        for (Videojuego videojuego : videojuegosFiltrados) {
            resultado.add(aResumen(videojuego));
        }

        return resultado;
    }
}


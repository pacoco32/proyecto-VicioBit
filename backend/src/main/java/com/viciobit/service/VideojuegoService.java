//Buscar un videojuego activo por su SKU
public VideojuegoDetalleDto buscarPorSku(String sku) {

    for (Videojuego videojuego : videojuegos) {

        if (videojuego.isActivo() &&
                videojuego.getSku().equalsIgnoreCase(sku)) {

            return aDetalle(videojuego);
        }
    }

    throw new RecursoNotFoundException("No existe el videojuego con SKU " + sku);
}

package com.viciobit.model;

import java.math.BigDecimal;

public class LineaPedido {

    private Videojuego videojuego;
    private String nombreVideojuego;     // copia del nombre en el momento de la compra
    private BigDecimal precioUnitario;   // copia del precio en el momento de la compra
    private Integer cantidad;
    private BigDecimal subtotal;

    public LineaPedido() {}

    public Videojuego getVideojuego() { return videojuego; }
    public void setVideojuego(Videojuego videojuego) { this.videojuego = videojuego; }

    public String getNombreVideojuego() { return nombreVideojuego; }
    public void setNombreVideojuego(String nombreVideojuego) { this.nombreVideojuego = nombreVideojuego; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
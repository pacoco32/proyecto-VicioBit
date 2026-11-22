package com.viciobit.dto;

import java.math.BigDecimal;

public class LineaCarritoDTO {

    private VideojuegoResumenDto videojuego;
    private Integer cantidad;
    private BigDecimal subtotal;

    public LineaCarritoDTO() {}

    public VideojuegoResumenDto getVideojuego() {
        return videojuego;
    }

    public void setVideojuego(VideojuegoResumenDto videojuego) {
        this.videojuego = videojuego;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}

package com.viciobit.dto;

import java.math.BigDecimal;

public class LineaPedidoDto {

    private Long videojuegoId;
    private String nombreVideojuego;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;

    public LineaPedidoDto() {}

    public Long getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(Long videojuegoId) { this.videojuegoId = videojuegoId; }

    public String getNombreVideojuego() { return nombreVideojuego; }
    public void setNombreVideojuego(String nombreVideojuego) { this.nombreVideojuego = nombreVideojuego; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
package com.viciobit.dto;

public class LineaCarritoCrearDTO {

    private Long videojuegoId;
    private Integer cantidad;

    public LineaCarritoCrearDTO() {}

    public Long getVideojuegoId() {
        return videojuegoId;
    }

    public void setVideojuegoId(Long videojuegoId) {
        this.videojuegoId = videojuegoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

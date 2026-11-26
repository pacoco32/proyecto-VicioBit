package com.viciobit.dto;

import com.viciobit.model.EstadoPedido;

public class CambiarEstadoDto {

    private EstadoPedido estado;

    public CambiarEstadoDto() {}

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
}
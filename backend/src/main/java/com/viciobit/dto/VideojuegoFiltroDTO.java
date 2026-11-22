package com.viciobit.dto;

import com.viciobit.model.Plataforma;

import java.math.BigDecimal;

public class VideojuegoFiltroDTO {

    private Long generoId;
    private String q; //Buscar por nombre o descripción
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private Boolean soloDisponibles;
    private Plataforma plataforma;

    public VideojuegoFiltroDTO() {}

    public Long getGeneroId() {
        return generoId;
    }

    public void setGeneroId(Long generoId) {
        this.generoId = generoId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public BigDecimal getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(BigDecimal precioMin) {
        this.precioMin = precioMin;
    }

    public BigDecimal getPrecioMax() {
        return precioMax;
    }

    public void setPrecioMax(BigDecimal precioMax) {
        this.precioMax = precioMax;
    }

    public Boolean getSoloDisponibles() {
        return soloDisponibles;
    }

    public void setSoloDisponibles(Boolean soloDisponibles) {
        this.soloDisponibles = soloDisponibles;
    }

    public Plataforma getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(Plataforma plataforma) {
        this.plataforma = plataforma;
    }
}

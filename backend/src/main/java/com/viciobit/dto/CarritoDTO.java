package com.viciobit.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarritoDTO {

    private List<LineaCarritoDTO> lineas = new ArrayList<>();
    private Integer numeroArticulos;
    private BigDecimal subtotal;
    private BigDecimal gastosEnvio;
    private BigDecimal total;
    private BigDecimal importeParaEnvioGratis;
    private List<String> avisos = new ArrayList<>();

    public CarritoDTO() {}

    public List<LineaCarritoDTO> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaCarritoDTO> lineas) {
        this.lineas = lineas;
    }

    public Integer getNumeroArticulos() {
        return numeroArticulos;
    }

    public void setNumeroArticulos(Integer numeroArticulos) {
        this.numeroArticulos = numeroArticulos;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGastosEnvio() {
        return gastosEnvio;
    }

    public void setGastosEnvio(BigDecimal gastosEnvio) {
        this.gastosEnvio = gastosEnvio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getImporteParaEnvioGratis() {
        return importeParaEnvioGratis;
    }

    public void setImporteParaEnvioGratis(BigDecimal importeParaEnvioGratis) {
        this.importeParaEnvioGratis = importeParaEnvioGratis;
    }

    public List<String> getAvisos() {
        return avisos;
    }

    public void setAvisos(List<String> avisos) {
        this.avisos = avisos;
    }
}

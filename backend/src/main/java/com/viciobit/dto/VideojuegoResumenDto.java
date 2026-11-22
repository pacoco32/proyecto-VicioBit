package com.viciobit.dto;

import com.viciobit.model.Plataforma;

import java.math.BigDecimal;

public class VideojuegoResumenDto {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String imagenUrl;
    private String generoNombre;
    private Plataforma plataforma;
    private boolean disponible;     // true si hay stock

    public VideojuegoResumenDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getGeneroNombre() { return generoNombre; }
    public void setGeneroNombre(String generoNombre) { this.generoNombre = generoNombre; }

    public Plataforma getPlataforma() { return plataforma; }
    public void setPlataforma(Plataforma plataforma) { this.plataforma = plataforma; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
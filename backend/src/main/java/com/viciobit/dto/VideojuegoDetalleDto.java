package com.viciobit.dto;

import com.viciobit.model.Plataforma;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VideojuegoDetalleDto {

    private Long id;
    private String sku;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagenUrl;
    private boolean activo;
    private boolean destacado;
    private LocalDate fechaAlta;
    private Long generoId;
    private String generoNombre;
    private Plataforma plataforma;
    private Integer pegi;

    public VideojuegoDetalleDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public Long getGeneroId() { return generoId; }
    public void setGeneroId(Long generoId) { this.generoId = generoId; }

    public String getGeneroNombre() { return generoNombre; }
    public void setGeneroNombre(String generoNombre) { this.generoNombre = generoNombre; }

    public Plataforma getPlataforma() { return plataforma; }
    public void setPlataforma(Plataforma plataforma) { this.plataforma = plataforma; }

    public Integer getPegi() { return pegi; }
    public void setPegi(Integer pegi) { this.pegi = pegi; }
}
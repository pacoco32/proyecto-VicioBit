package com.viciobit.dto;
//DTO de SALIDA
//Sirve para devolver los datos del género al cliente cuando consulta, crea o modifica uno.
public class GeneroDTO {

    private Long id;
    private String nombre;
    private String descripcion;

    public GeneroDTO() {}

    public GeneroDTO(Long id, String nombre,  String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

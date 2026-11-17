package com.viciobit.dto;
//DTO de ENTRADA
//Recibe los datos al crear un género
//No Id porque lo asignará el servicio
//Sirve para recibir los datos que envía el cliente cuando crea o modifica un género.
public class GeneroCrearDTO {

    private String nombre;
    private String descripcion;

    public GeneroCrearDTO() {}

    public GeneroCrearDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

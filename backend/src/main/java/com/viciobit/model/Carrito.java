package com.viciobit.model;

import java.util.ArrayList;
import java.util.List;

//Carrito completo de un usuario
public class Carrito {

    private Usuario usuario;
    private List<LineaCarrito> lineas = new ArrayList<>();

    public Carrito() {}

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<LineaCarrito> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaCarrito> lineas) {
        this.lineas = lineas;
    }
}

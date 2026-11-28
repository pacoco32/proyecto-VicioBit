package com.viciobit.dto;

import com.viciobit.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PedidoResumenDto {

    private Long id;
    private String codigo;
    private String usuarioEmail;
    private LocalDate fecha;
    private EstadoPedido estado;
    private BigDecimal total;

    public PedidoResumenDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
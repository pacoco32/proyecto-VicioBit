package com.viciobit.dto;

import com.viciobit.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDto {

    private Long id;
    private String codigo;
    private String usuarioEmail;
    private LocalDate fecha;
    private EstadoPedido estado;
    private String direccionEnvio;
    private BigDecimal subtotal;
    private BigDecimal gastosEnvio;
    private BigDecimal total;
    private List<LineaPedidoDto> lineas = new ArrayList<>();

    public PedidoDto() {}

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

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getGastosEnvio() { return gastosEnvio; }
    public void setGastosEnvio(BigDecimal gastosEnvio) { this.gastosEnvio = gastosEnvio; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<LineaPedidoDto> getLineas() { return lineas; }
    public void setLineas(List<LineaPedidoDto> lineas) { this.lineas = lineas; }
}
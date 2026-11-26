package com.viciobit.controller;

import com.viciobit.dto.CambiarEstadoDto;
import com.viciobit.dto.PedidoDto;
import com.viciobit.dto.PedidoResumenDto;
import com.viciobit.model.EstadoPedido;
import com.viciobit.service.PedidoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/pedidos")
public class PedidoAdminController {

    private final PedidoService pedidoService;

    public PedidoAdminController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResumenDto>> listarTodos(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(pedidoService.listarTodos(estado, desde, hasta, email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerCualquiera(id));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoDto> cambiarEstado(@PathVariable Long id, @RequestBody CambiarEstadoDto entrada) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, entrada));
    }
}
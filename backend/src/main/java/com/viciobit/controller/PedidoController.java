package com.viciobit.controller;

import com.viciobit.dto.ConfirmarPedidoDto;
import com.viciobit.dto.PedidoDto;
import com.viciobit.dto.PedidoResumenDto;
import com.viciobit.model.EstadoPedido;
import com.viciobit.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDto> confirmar(@RequestBody(required = false) ConfirmarPedidoDto entrada) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.confirmar(entrada));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResumenDto>> listarMios(@RequestParam(required = false) EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.listarMios(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerMio(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }
}
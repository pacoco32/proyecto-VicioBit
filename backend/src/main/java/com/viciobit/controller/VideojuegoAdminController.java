package com.viciobit.controller;

import com.viciobit.dto.StockDto;
import com.viciobit.dto.VideojuegoCrearDto;
import com.viciobit.dto.VideojuegoDetalleDto;
import com.viciobit.service.VideojuegoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/productos")
public class VideojuegoAdminController {

    private final VideojuegoService videojuegoService;

    public VideojuegoAdminController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    @GetMapping
    public ResponseEntity<List<VideojuegoDetalleDto>> listarTodos() {
        return ResponseEntity.ok(videojuegoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<VideojuegoDetalleDto> crear(@RequestBody VideojuegoCrearDto entrada) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videojuegoService.crear(entrada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideojuegoDetalleDto> modificar(@PathVariable Long id,
                                                          @RequestBody VideojuegoCrearDto entrada) {
        return ResponseEntity.ok(videojuegoService.modificar(id, entrada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retirar(@PathVariable Long id) {
        videojuegoService.retirar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<VideojuegoDetalleDto> cambiarStock(@PathVariable Long id,
                                                             @RequestBody StockDto entrada) {
        return ResponseEntity.ok(videojuegoService.cambiarStock(id, entrada));
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<VideojuegoDetalleDto> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(videojuegoService.reactivar(id));
    }
}
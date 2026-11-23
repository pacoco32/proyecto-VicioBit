package com.viciobit.controller;

import com.viciobit.dto.VideojuegoDetalleDto;
import com.viciobit.dto.VideojuegoResumenDto;
import com.viciobit.service.VideojuegoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class VideojuegoController {

    private final VideojuegoService videojuegoService;

    public VideojuegoController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideojuegoDetalleDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(videojuegoService.obtenerActivo(id));
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<VideojuegoResumenDto>> listarDestacados() {
        return ResponseEntity.ok(videojuegoService.listarDestacados());
    }
}
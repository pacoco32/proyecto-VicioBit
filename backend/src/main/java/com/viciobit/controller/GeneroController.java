package com.viciobit.controller;

import com.viciobit.dto.VideojuegoResumenDto;
import com.viciobit.service.GeneroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Opciones publicas
@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    //GET - Listar géneros
    @GetMapping
    public ResponseEntity<?> listarGeneros() {
        return ResponseEntity.ok(generoService.listarGeneros());
    }

    //GET - Búsqueda por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> consultarGenero(@PathVariable Long id) {
        return ResponseEntity.ok(generoService.buscarGeneroId(id));
    }

    //GET - Videojuegos de un género
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<VideojuegoResumenDto>> productosDeGenero(@PathVariable Long id) {
        return ResponseEntity.ok(generoService.listarVideojuegosDeGenero(id));
    }
}

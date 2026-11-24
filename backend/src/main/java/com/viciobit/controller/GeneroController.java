package com.viciobit.controller;

import com.viciobit.service.GeneroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Opciones publicas
@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    // GET - Listar géneros
    @GetMapping
    public ResponseEntity<?> listarGeneros() {
        return ResponseEntity.ok(generoService.listarGeneros());
    }

    // GET - Búsqueda por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> consultarGenero(@PathVariable Long id) {
        return ResponseEntity.ok(generoService.buscarGeneroId(id));
    }
}

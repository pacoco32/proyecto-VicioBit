package com.viciobit.controller;

import com.viciobit.dto.GeneroCrearDTO;
import com.viciobit.dto.GeneroDTO;
import com.viciobit.service.GeneroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Opciones de administrador
@RestController
@RequestMapping("/api/admin/generos")
public class GeneroAdminController {

    private final GeneroService generoService;

    public GeneroAdminController(GeneroService generoService) {
        this.generoService = generoService;
    }

    // GET - Listar géneros
    @GetMapping
    public ResponseEntity<?> listarAdminGeneros() {
        return ResponseEntity.ok(generoService.listarGeneros());
    }

    // POST - Crear género
    @PostMapping
    public ResponseEntity<?> crearGenero(@RequestBody GeneroCrearDTO generoCrearDTO) {
        GeneroDTO genero = generoService.crearGenero(generoCrearDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(genero);
    }

    // PUT - Modificar género
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarGenero(
            @PathVariable Long id,
            @RequestBody GeneroCrearDTO generoCrearDTO) {

        GeneroDTO genero = generoService.modificarGenero(id, generoCrearDTO);

        return ResponseEntity.ok(genero);
    }

    // DELETE - Eliminar género
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarGenero(@PathVariable Long id) {
        generoService.eliminarGenero(id);

        return ResponseEntity.noContent().build();
    }
}

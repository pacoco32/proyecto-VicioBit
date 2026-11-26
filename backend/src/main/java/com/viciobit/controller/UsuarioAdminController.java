package com.viciobit.controller;

import com.viciobit.dto.CambiarRolDto;
import com.viciobit.dto.UsuarioDto;
import com.viciobit.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioDto> cambiarRol(@PathVariable Long id, @RequestBody CambiarRolDto entrada) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, entrada));
    }
}
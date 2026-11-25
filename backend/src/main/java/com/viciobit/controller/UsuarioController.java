package com.viciobit.controller;

import com.viciobit.dto.UsuarioActualizarDto;
import com.viciobit.dto.UsuarioDto;
import com.viciobit.service.UsuarioActualService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioActualService usuarioActualService;

    public UsuarioController(UsuarioActualService usuarioActualService) {
        this.usuarioActualService = usuarioActualService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> obtenerPerfil() {
        return ResponseEntity.ok(usuarioActualService.obtenerPerfil());
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDto> actualizarPerfil(@RequestBody UsuarioActualizarDto entrada) {
        return ResponseEntity.ok(usuarioActualService.actualizarPerfil(entrada));
    }
}
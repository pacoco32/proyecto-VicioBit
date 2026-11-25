package com.viciobit.controller;

import com.viciobit.dto.CarritoDTO;
import com.viciobit.dto.LineaCarritoCrearDTO;
import com.viciobit.service.CarritoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    //Consultar el carrito
    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito() {
        return ResponseEntity.ok(carritoService.obtenerCarrito());
    }

    //Añadir un videojuego al carrito
    @PostMapping("/lineas")
    public ResponseEntity<CarritoDTO> agregarLinea(@RequestBody LineaCarritoCrearDTO entrada) {

        return ResponseEntity.ok(carritoService.agregarLinea(entrada));
    }

    //Modificar la cantidad de un videojuego
    @PutMapping("/lineas/{videojuegoId}")
    public ResponseEntity<CarritoDTO> modificarCantidad(
                                    @PathVariable Long videojuegoId,
                                    @RequestBody LineaCarritoCrearDTO entrada) {

        return ResponseEntity.ok(carritoService.modificarCantidad(videojuegoId, entrada.getCantidad()));
    }

    //Eliminar un videojuego del carrito
    @DeleteMapping("/lineas/{videojuegoId}")
    public ResponseEntity<CarritoDTO> eliminarLinea(@PathVariable Long videojuegoId) {

        return ResponseEntity.ok(carritoService.eliminarLinea(videojuegoId));
    }

    //Vaciar el carrito
    @DeleteMapping
    public ResponseEntity<CarritoDTO> vaciarCarrito() {
        return ResponseEntity.ok(carritoService.vaciarCarrito());
    }
}

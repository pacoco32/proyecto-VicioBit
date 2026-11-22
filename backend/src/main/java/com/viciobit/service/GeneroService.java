package com.viciobit.service;

import com.viciobit.dto.GeneroCrearDTO;
import com.viciobit.dto.GeneroDTO;
import com.viciobit.exception.ConflictoException;
import com.viciobit.exception.DatosInvalidosException;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Genero;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeneroService {

    private List<Genero> generos = new ArrayList<>(); //Lista de géneros. Al no contar con una BBDD los metemos en una lista
    private Long siguienteId = 1L; //Asigna ids consecutivos a los géneros que vayamos creando/* Dependencia pendiente para comprobar si hay videojuegos asociados antes de eliminar un género.

    /*Se activará cuando exista VideojuegoService.
    @Lazy
    private VideojuegoService videojuegoService;*/

    public GeneroService() {}

    //Muestra los géneros
    public List<GeneroDTO> listarGeneros() {
        List<GeneroDTO> listaGeneros = new ArrayList<>();

        for (Genero genero : generos) {
            GeneroDTO generoDTO = new GeneroDTO(genero.getId(), genero.getNombre(), genero.getDescripcion());

            listaGeneros.add(generoDTO);
        }

        return listaGeneros;
    }

    //Busca por id un genero - devuelve su model para futuros usos
    public Genero buscarGeneroModelId(Long id) {
        for (Genero genero : generos) {
            if (genero.getId().equals(id)) {
                return genero;
            }
        }
        throw new RecursoNotFoundException("Genero no encontrado");
    }

    //Busca genero y devuelve su DTO para que el controller no trabaje directamente con el modelo
    public GeneroDTO buscarGeneroId(Long id) {
        Genero genero = buscarGeneroModelId(id);

        return new GeneroDTO(genero.getId(), genero.getNombre(), genero.getDescripcion());
    }

    //Crear un genero
    public GeneroDTO crearGenero(GeneroCrearDTO generoCrearDTO) {

        validacionNombre(generoCrearDTO.getNombre()); //Necesitamos el metodo para validar el nombre
        validacionDuplicado(generoCrearDTO.getNombre(), null); //Necesitamos llamar al metodo para saber si está o no
        Genero genero = new Genero();
        genero.setId(siguienteId++);
        genero.setNombre(generoCrearDTO.getNombre());
        genero.setDescripcion(generoCrearDTO.getDescripcion());

        generos.add(genero);

        return new GeneroDTO(genero.getId(), genero.getNombre(), genero.getDescripcion());
    }

    //Validacion de nombre
    public void validacionNombre(String nombre) {

        //isBlank() reconoce espacios en blanco
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("El nombre hay que ponerlo");
        }
    }

    //Evitar mismo nombre
    //Genero a saltar/excluir de mientras se comprueba
    public void validacionDuplicado(String nombre, Long idExcluido) {
        String n = nombre.toLowerCase();

        for (Genero genero : generos) {
            if (genero.getNombre().toLowerCase().equals(n) && !genero.getId().equals(idExcluido)) {
                throw new ConflictoException("El nombre ya existe");
            }
        }
    }

    //Modificar un género
    public GeneroDTO modificarGenero(Long id, GeneroCrearDTO generoCrearDTO) {

        Genero genero = buscarGeneroModelId(id); //Buscamos el genero por id

        validacionNombre(generoCrearDTO.getNombre());
        validacionDuplicado(generoCrearDTO.getNombre(), id);

        genero.setNombre(generoCrearDTO.getNombre());
        genero.setDescripcion(generoCrearDTO.getDescripcion());

        return new GeneroDTO(genero.getId(), genero.getNombre(), genero.getDescripcion());
    }

    //Eliminar un genero //Falta VideojuegoService para comprobacion
    public void eliminarGenero(Long id) {
        Genero genero = buscarGeneroModelId(id);

        generos.remove(genero);
    }


}

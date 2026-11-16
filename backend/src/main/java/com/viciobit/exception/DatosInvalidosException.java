package com.viciobit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DatosInvalidosException extends ResponseStatusException {

    public DatosInvalidosException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, mensaje);
    }
}

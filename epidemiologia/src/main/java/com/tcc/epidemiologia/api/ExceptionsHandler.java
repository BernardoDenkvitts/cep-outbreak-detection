package com.tcc.epidemiologia.api;

import com.tcc.epidemiologia.service.CoordenadasInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(CoordenadasInvalidaException.class)
    public ProblemDetail handleUnprocessableEntity(Exception ex) {
        ProblemDetail pb = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pb.setTitle("Unprocessable Entity");
        pb.setType(URI.create("CepEpidemiologia/unprocessable-entity"));
        pb.setDetail(ex.getMessage());
        return pb;
    }

}

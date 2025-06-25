package com.tcc.epidemiologia.service;

public class CoordenadasInvalidaException extends RuntimeException {
    public CoordenadasInvalidaException() {
        super("Latitude e Longitude invalidas, não está dentro de Passo Fundo");
    }
}

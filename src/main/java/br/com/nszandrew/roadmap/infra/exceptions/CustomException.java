package br.com.nszandrew.roadmap.infra.exceptions;

public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}

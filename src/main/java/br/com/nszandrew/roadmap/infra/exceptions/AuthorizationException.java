package br.com.nszandrew.roadmap.infra.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}

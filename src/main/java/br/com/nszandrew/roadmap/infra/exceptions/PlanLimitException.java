package br.com.nszandrew.roadmap.infra.exceptions;

public class PlanLimitException extends RuntimeException {
    public PlanLimitException(String message) {
        super(message);
    }
}

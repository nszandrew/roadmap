package br.com.nszandrew.roadmap.model.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRoadMapDTO(@NotNull String title, String description) {
}

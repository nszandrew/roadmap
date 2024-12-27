package br.com.nszandrew.roadmap.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotNull
        String name,
                                 @Email
                                 String email,
                                 @NotNull
                                 String password) {
}

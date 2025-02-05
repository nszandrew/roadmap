package br.com.nszandrew.roadmap.model.dto.user;

public record UserUpdateDTO(
                            Long id,
                            String name,
                            String currentPassword,
                            String newPassword,
                            Boolean active) {
}

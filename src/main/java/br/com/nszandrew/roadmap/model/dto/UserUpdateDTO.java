package br.com.nszandrew.roadmap.model.dto;

public record UserUpdateDTO(String name,
                            String currentPassword,
                            String newPassword,
                            Boolean active) {
}

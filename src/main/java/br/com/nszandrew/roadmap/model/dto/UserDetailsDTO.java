package br.com.nszandrew.roadmap.model.dto;

import br.com.nszandrew.roadmap.model.user.User;

import java.time.LocalDateTime;

public record UserDetailsDTO(Long id,
                             String name,
                             String email,
                             String planType,
                             Boolean active,
                             LocalDateTime createdAt) {

    public UserDetailsDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getPlanType().toString(), user.getActive(), user.getCreatedAt());
    }
}

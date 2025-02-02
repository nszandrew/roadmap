package br.com.nszandrew.roadmap.model.dto.roadmap;

import br.com.nszandrew.roadmap.model.dto.user.UserDetailsDTO;
import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.user.User;

import java.time.LocalDateTime;

public record RoadMapResponseDTO(Long id,
                                 String title,
                                 String description,
                                 boolean active,
                                 LocalDateTime createdAt,
                                 UserDetailsDTO user) {

    public RoadMapResponseDTO(RoadMap roadMap, User user) {
        this(roadMap.getId(), roadMap.getTitle(), roadMap.getDescription(), roadMap.getActive(), roadMap.getCreatedAt(), new UserDetailsDTO(user));
    }
}


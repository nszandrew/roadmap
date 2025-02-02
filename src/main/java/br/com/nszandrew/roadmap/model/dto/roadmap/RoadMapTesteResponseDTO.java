package br.com.nszandrew.roadmap.model.dto.roadmap;

import br.com.nszandrew.roadmap.model.roadmap.RoadMap;

import java.time.LocalDateTime;

public record RoadMapTesteResponseDTO(Long id,
                                      String title,
                                      String description,
                                      boolean active,
                                      LocalDateTime createdAt) {

    public RoadMapTesteResponseDTO (RoadMap roadMap){
        this(roadMap.getId(), roadMap.getTitle(), roadMap.getDescription(), roadMap.getActive(), roadMap.getCreatedAt());
    }
}

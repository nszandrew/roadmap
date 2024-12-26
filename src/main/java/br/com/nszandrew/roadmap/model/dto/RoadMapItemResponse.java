package br.com.nszandrew.roadmap.model.dto;

import br.com.nszandrew.roadmap.model.roadmap.MapStatus;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import br.com.nszandrew.roadmap.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public record RoadMapItemResponse(Long id,
                                  String title,
                                  String description,
                                  MapStatus status,
                                  List<String> links,
                                  Integer orderIndex,
                                  Integer dificulty,
                                  String duration,
                                  LocalDateTime createdAt,
                                  UserDetailsDTO user) {

    public RoadMapItemResponse(RoadMapItem roadMapItem, User user) {
        this(roadMapItem.getId(), roadMapItem.getTitle(),
                roadMapItem.getDescription(), roadMapItem.getStatus(),
                roadMapItem.getLinks(), roadMapItem.getOrderIndex(),
                roadMapItem.getDificulty(), roadMapItem.getDuration(),
                roadMapItem.getCreatedAt(), new UserDetailsDTO(user));
    }
}

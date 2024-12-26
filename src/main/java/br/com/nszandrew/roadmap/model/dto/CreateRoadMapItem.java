package br.com.nszandrew.roadmap.model.dto;

import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRoadMapItem(@NotNull
                                String title,
                                String description,
                                List<String> links,
                                @NotNull
                                Integer orderIndex,
                                @NotNull
                                Integer dificulty,
                                String duration,
                                @NotNull
                                RoadMap roadMap) {

}
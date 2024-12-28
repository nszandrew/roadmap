package br.com.nszandrew.roadmap.model.dto.roadmapitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRoadMapItem(@NotNull
                                @Size(min = 3, max = 20)
                                String title,
                                @Size(max = 255)
                                String description,
                                @Size(max = 20)
                                List<String> links,
                                @NotNull
                                @Size(min = 1, max = 99)
                                Integer orderIndex,
                                @NotNull
                                @Size(min = 1, max = 99)
                                Integer dificulty,
                                String duration,
                                @NotNull
                                Long roadMapId) {

}

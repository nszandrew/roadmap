package br.com.nszandrew.roadmap.model.dto.roadmapitem;

import br.com.nszandrew.roadmap.model.roadmap.LevelUser;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GPTCreateRoadMapItemRequestDTO(
        @NotNull
        @Size(min = 3, max = 20)
        String topic,
        @NotNull
        LevelUser level,
        @Size(max = 10)
        List<String> objectives
) {
}

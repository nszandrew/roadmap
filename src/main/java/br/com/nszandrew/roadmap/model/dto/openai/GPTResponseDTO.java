package br.com.nszandrew.roadmap.model.dto.openai;

import java.util.List;

public record GPTResponseDTO(List<GPTRoadMapItemDTO> roadmapitem) {
}

package br.com.nszandrew.roadmap.model.dto.openai;

import java.util.List;

public record GPTRoadMapItemDTO(String title,
                                String description,
                                List<String> links,
                                Integer orderIndex,
                                Integer dificulty,
                                String duration,
                                String comment) {
}

package br.com.nszandrew.roadmap.model.dto;

import br.com.nszandrew.roadmap.model.roadmap.MapStatus;

import java.util.List;

public record UpdateRoadMapItem(String title,
                                String description,
                                MapStatus status,
                                List<String> links,
                                Integer orderIndex,
                                Integer dificulty,
                                String duration) {
}

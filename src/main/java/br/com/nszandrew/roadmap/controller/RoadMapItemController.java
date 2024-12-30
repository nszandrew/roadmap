package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.openai.GPTCreateRoadMapItemRequestDTO;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.CreateRoadMapItem;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.RoadMapItemResponse;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.UpdateRoadMapItem;
import br.com.nszandrew.roadmap.service.RoadMapItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoadMapItemController {

    private final RoadMapItemService roadMapItemService;

    public RoadMapItemController(RoadMapItemService roadMapItemService) {
        this.roadMapItemService = roadMapItemService;
    }

    @PostMapping("/roadmap/{roadmapId}/generate/")
    public ResponseEntity<String> generateRoadMap(@RequestBody GPTCreateRoadMapItemRequestDTO dto, @PathVariable Long roadmapId) {
        return new ResponseEntity<>(roadMapItemService.generateWithAI(dto, roadmapId), HttpStatus.CREATED);
    }

        @PostMapping("/roadmapitem/create")
    public ResponseEntity<String> createRoadMapItem(@RequestBody @Valid CreateRoadMapItem data){
        return new ResponseEntity<>(roadMapItemService.createRoadMapItem(data), HttpStatus.CREATED);
    }

    @GetMapping("/roadmaps/{roadmapId}/item/{itemId}")
    public ResponseEntity<RoadMapItemResponse> getRoadMapItem(@PathVariable Long roadmapId, @PathVariable Long itemId){
        return new ResponseEntity<>(roadMapItemService.getRoadMapItem(itemId, roadmapId), HttpStatus.OK);
    }

    @GetMapping("/roadmapitens/{roadmapId}")
    public ResponseEntity<List<RoadMapItemResponse>> getAllRoadMapItem(@PathVariable Long roadmapId){
        return new ResponseEntity<>(roadMapItemService.getAllRoadMapsItensByRoadMap(roadmapId), HttpStatus.OK);
    }

    @PutMapping("/roadmapitem/{roadmapId}/item/{itemId}")
    public ResponseEntity<String> editRoadMapItem(@PathVariable Long roadmapId, @PathVariable Long itemId, @RequestBody @Valid UpdateRoadMapItem data){
        return new ResponseEntity<>(roadMapItemService.editRoadMapItem(roadmapId, itemId, data), HttpStatus.OK);
    }

    @DeleteMapping("/roadmapitem/{roadmapId}/item/{itemId}")
    public ResponseEntity<String> removeRoadMapItem(@PathVariable Long roadmapId, @PathVariable Long itemId) {
        return new ResponseEntity<>(roadMapItemService.removeRoadMapItem(roadmapId, itemId), HttpStatus.NO_CONTENT);
    }

}

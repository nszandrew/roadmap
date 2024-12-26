package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.CreateRoadMapDTO;
import br.com.nszandrew.roadmap.model.dto.RoadMapResponseDTO;
import br.com.nszandrew.roadmap.model.dto.UpdateRoadMapDTO;
import br.com.nszandrew.roadmap.service.RoadMapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoadMapController {

    private final RoadMapService roadMapService;

    public RoadMapController(RoadMapService roadMapService) {
        this.roadMapService = roadMapService;
    }

    @PostMapping("/createroadmap")
    public ResponseEntity<String> createRoadMap(@RequestBody CreateRoadMapDTO data){
        return new ResponseEntity<>(roadMapService.createRoadMap(data), HttpStatus.CREATED);
    }

    @GetMapping("/getroadmapbyid/{id}")
    public ResponseEntity<RoadMapResponseDTO> getRoadMap(@PathVariable Long id){
        return new ResponseEntity<>(roadMapService.getRoadMapById(id), HttpStatus.OK);
    }

    @GetMapping("/getallroadmaps")
    public ResponseEntity<List<RoadMapResponseDTO>> getAllRoadMapsByUser(){
        return new ResponseEntity<>(roadMapService.getAllRoadMaps(), HttpStatus.OK);
    }

    @PutMapping("/updateroadmap/{id}")
    public ResponseEntity<RoadMapResponseDTO> updateRoadMap(@PathVariable Long id, @RequestBody UpdateRoadMapDTO data){
        return new ResponseEntity<>(roadMapService.updateRoadMap(id, data), HttpStatus.OK);
    }

    @DeleteMapping("/deleteroadmap/{id}")
    public ResponseEntity<String> deleteRoadMap(@PathVariable Long id) {
        return new ResponseEntity<>(roadMapService.deleteRoadMap(id), HttpStatus.NO_CONTENT);
    }

}

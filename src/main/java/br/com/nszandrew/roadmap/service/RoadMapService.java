package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.model.dto.CreateRoadMapDTO;
import br.com.nszandrew.roadmap.model.dto.RoadMapResponseDTO;
import br.com.nszandrew.roadmap.model.dto.UpdateRoadMapDTO;
import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapItemRepository;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoadMapService {

    private final AuthenticationService authenticationService;
    private final RoadMapItemRepository roadMapItemRepository;
    private final RoadMapRepository roadMapRepository;


    public String createRoadMap(CreateRoadMapDTO data) {
        User user = authenticationService.getUserAuthenticated();
        boolean userAlreadyHaveRoadmap = roadMapRepository.existsByUserId(user.getId());
        if(userAlreadyHaveRoadmap && !authenticationService.userHasPremiumPermission(user)){
            throw new CustomException("Usuário ja excedeu o limite de roadmaps no planos dele");
        }

        RoadMap roadMap = RoadMap.builder()
                .title(data.title())
                .description(data.description())
                .createdAt(LocalDateTime.now())
                .active(true)
                .user(user)
                .build();
        roadMapRepository.save(roadMap);
        return "RoadMap criado com sucesso";
    }

    public RoadMapResponseDTO getRoadMapById(Long id) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(id)
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));

        if(!roadMap.getUser().getId().equals(user.getId())){
            throw new CustomException("RoadMap nao pertence ao usuário");
        }

        return new RoadMapResponseDTO(roadMap, user);
    }


    public List<RoadMapResponseDTO> getAllRoadMaps() {
        User user = authenticationService.getUserAuthenticated();
        List<RoadMap> roadMaps = roadMapRepository.findAllByUser(user);

        return roadMaps.stream()
                .map(roadMap -> new RoadMapResponseDTO(roadMap, user))
                .collect(Collectors.toList());
    }

    public RoadMapResponseDTO updateRoadMap(Long id, UpdateRoadMapDTO data) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findByIdAndUser(id, user);

        if(roadMap == null){
            throw new CustomException("ID do RoadMap nao encontrado ou nao pertence a esse usuario");
        }

        roadMap.setTitle(data.title());
        roadMap.setDescription(data.description());
        roadMap.setActive(data.active());
        roadMap.setUpdatedAt(LocalDateTime.now());
        roadMapRepository.save(roadMap);
        return new RoadMapResponseDTO(roadMap, user);
    }

    public String deleteRoadMap(Long id) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findByIdAndUser(id, user);

        if(roadMap == null){throw new CustomException("ID do RoadMap nao encontrado ou nao pertence a esse usuario");}

        List<RoadMapItem> roadMapItem = roadMapItemRepository.findByRoadMap(roadMap);
        if(roadMapItem != null){roadMapItemRepository.deleteAll(roadMapItem);}

        roadMapRepository.delete(roadMap);
        return "RoadMap deletado com sucesso";
    }
}

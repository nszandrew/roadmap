package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.model.dto.CreateRoadMapItem;
import br.com.nszandrew.roadmap.model.dto.RoadMapItemResponse;
import br.com.nszandrew.roadmap.model.dto.UpdateRoadMapItem;
import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapItemRepository;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RoadMapItemService {

    private final AuthenticationService authenticationService;
    private final RoadMapItemRepository roadMapItemRepository;
    private final RoadMapRepository roadMapRepository;

    @Transactional
    public String createRoadMapItem(CreateRoadMapItem data) {
        User user = authenticationService.getUserAuthenticated();
        roadMapRepository.findById(data.roadMap().getId())
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));

        boolean userHas20RMI = userHas20RoadmapItems(user);
        if(userHas20RMI && (!authenticationService.userHasPremiumPermission(user) || !authenticationService.userHasBasicPermission(user))){
            throw new CustomException("Usuário ja excedeu o limite de itens no seu roadmap no plano atual");
        }

        RoadMapItem roadMapItem = new RoadMapItem(data, user);
        roadMapItemRepository.save(roadMapItem);
        return "RoadMapItem criado com sucesso";
    }

    public boolean userHas20RoadmapItems(User user) {
        return roadMapItemRepository.countRoadmapItemsByUser(user) >= 20;
    }

    @Transactional(readOnly = true)
    public RoadMapItemResponse getRoadMapItem(Long itemId, Long roadMapId) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadMapId)
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));
        RoadMapItem roadMapItem = roadMapItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("ID do RoadMapItem nao encontrado"));

        if(!roadMap.getUser().getId().equals(user.getId()) || !roadMapItem.getRoadMap().getId().equals(roadMap.getId())){
            throw new CustomException("RoadMapItem nao pertence ao usuário ou ao roadmap informado");
        }

        return new RoadMapItemResponse(roadMapItem, user);
    }

    @Transactional(readOnly = true)
    public List<RoadMapItemResponse> getAllRoadMapsItensByRoadMap(Long roadmapId){
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));

        if(!roadMap.getUser().getId().equals(user.getId())){
            throw new CustomException("Esse RoadMap nao pertence ao usuário logado");
        }

        List<RoadMapItem> roadMapItems = roadMapItemRepository.findAllByRoadMap(roadMap);

        if(roadMapItems.isEmpty()){throw new CustomException("Nao ha roadmaps item cadastrados para esse roadmap");}

        return roadMapItems.stream()
                .map(roadMapItem -> new RoadMapItemResponse(roadMapItem, user))
                .toList();
    }

    @Transactional
    public String editRoadMapItem(Long roadmapId, Long itemId, UpdateRoadMapItem data) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));
        RoadMapItem roadMapItem = roadMapItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("ID do RoadMapItem nao encontrado"));

        if(!roadMap.getUser().getId().equals(user.getId()) || !roadMapItem.getRoadMap().getId().equals(roadMap.getId())){
            throw new CustomException("RoadMapItem nao pertence ao usuário ou ao roadmap informado");
        }

        roadMapItem.editRoadMapItem(data);
        roadMapItemRepository.save(roadMapItem);
        return "RoadMap Item Editado com sucesso";
    }

    @Transactional
    public String removeRoadMapItem(Long roadmapId, Long itemId) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));
        RoadMapItem roadMapItem = roadMapItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("ID do RoadMapItem nao encontrado"));

        if(!roadMap.getUser().getId().equals(user.getId()) || !roadMapItem.getRoadMap().getId().equals(roadMap.getId())){
            throw new CustomException("RoadMapItem nao pertence ao usuário ou ao roadmap informado");
        }

        roadMapItemRepository.delete(roadMapItem);
        return "RoadMap Item removido com sucesso";
    }
}

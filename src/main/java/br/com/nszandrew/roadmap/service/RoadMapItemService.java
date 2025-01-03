package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.model.dto.openai.GPTCreateRoadMapItemRequestDTO;
import br.com.nszandrew.roadmap.model.dto.openai.GPTResponseDTO;
import br.com.nszandrew.roadmap.model.dto.openai.GPTRoadMapItemDTO;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.CreateRoadMapItem;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.RoadMapItemResponse;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.UpdateRoadMapItem;
import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapItemRepository;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapRepository;
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
    private final OpenAiService openAiService;
    private final PlanLimitService planLimitService;

    @Transactional
    public String createRoadMapItem(CreateRoadMapItem data) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap rdm = roadMapRepository.findById(data.roadMapId())
                .orElseThrow(() -> new CustomException("Roadmap id not found"));

        planLimitService.getRoadMapItems(user.getId());

        RoadMapItem roadMapItem = new RoadMapItem(data, user, rdm);
        roadMapItemRepository.save(roadMapItem);
        return "RoadMapItem created successfully";
    }

    @Transactional(readOnly = true)
    public RoadMapItemResponse getRoadMapItem(Long itemId, Long roadMapId) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadMapId)
                .orElseThrow(() -> new CustomException("Roadmap id not found"));
        RoadMapItem roadMapItem = roadMapItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Roadmapitem id not found"));

        if(!roadMap.getUser().getId().equals(user.getId()) || !roadMapItem.getRoadMap().getId().equals(roadMap.getId())){
            throw new CustomException("RoadMapItem nao pertence ao usuário ou ao roadmap informado");
        }

        return new RoadMapItemResponse(roadMapItem, user);
    }

    @Transactional(readOnly = true)
    public List<RoadMapItemResponse> getAllRoadMapsItensByRoadMap(Long roadmapId){
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException("Roadmap id not found"));

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
                .orElseThrow(() -> new CustomException("Roadmap id not found"));
        RoadMapItem roadMapItem = roadMapItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Roadmapitem id not found"));

        if(!roadMap.getUser().getId().equals(user.getId()) || !roadMapItem.getRoadMap().getId().equals(roadMap.getId())){
            throw new CustomException("RoadMapItem nao pertence ao usuário ou ao roadmap informado");
        }

        roadMapItem.editRoadMapItem(data);
        roadMapItemRepository.save(roadMapItem);
        return "RoadMap Item edited successfully";
    }

    @Transactional
    public String removeRoadMapItem(Long roadmapId, Long itemId) {
        User user = authenticationService.getUserAuthenticated();
        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException("Roadmap id not found"));
        RoadMapItem roadMapItem = roadMapItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Roadmapitem id not found"));

        if(!roadMap.getUser().getId().equals(user.getId()) || !roadMapItem.getRoadMap().getId().equals(roadMap.getId())){
            throw new CustomException("RoadMapItem nao pertence ao usuário ou ao roadmap informado");
        }

        roadMapItemRepository.delete(roadMapItem);
        return "RoadMap Item deleted successfully";
    }

    @Transactional
    public String generateWithAI(GPTCreateRoadMapItemRequestDTO dto, Long roadmapId) {
        User user = authenticationService.getUserAuthenticated();

        if (!planLimitService.canMakeAICall(user)) {
            throw new CustomException("Usuario não pode fazer mais chamadas com a IA - Limite Mensal Atingido");
        }

        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException("ID do RoadMap nao encontrado"));

        if(!roadMap.getUser().getId().equals(user.getId())){
            throw new CustomException("RoadMap informado nao pertence ao usuário");
        }



        try {
            GPTResponseDTO response = openAiService.generateRoadMap(dto);

            for (GPTRoadMapItemDTO itemDTO : response.roadmapitem()) {
                RoadMapItem entity = new RoadMapItem(itemDTO, user, roadMap);
                roadMapItemRepository.save(entity);
            }

            planLimitService.recordAICall(user);
            return "Gerado e salvo com sucesso";

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Erro ao gerar RoadMapItem com IA: " + e.getMessage());
        }
    }
}

package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.infra.exceptions.PlanLimitException;
import br.com.nszandrew.roadmap.model.AI.AIUsage;
import br.com.nszandrew.roadmap.model.AI.PlanLimit;
import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import br.com.nszandrew.roadmap.model.user.PlanType;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.AI.AIUsageRepository;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapItemRepository;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapRepository;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PlanLimitService {

    private final UserRepository userRepository;
    private final RoadMapItemRepository roadMapItemRepository;
    private final RoadMapRepository roadMapRepository;
    private final AIUsageRepository aiUsageRepository;


    @Transactional(readOnly = true)
    public void getQuantityRoadMapByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Usuario nao encontrado"));

        Integer roadMap = roadMapRepository.findAllByUserCount(user);

        if(user.getPlanType() == PlanType.FREE_TIER && roadMap >= 4){
            throw new PlanLimitException("Limite do Plano Gratuito atingido, se quiser mais espaços para criar os RoadMap experimente os Planos Premium e Basic!");
        }

        if(user.getPlanType() == PlanType.BASIC_TIER && roadMap >= 11){
            throw new PlanLimitException("Limite do Plano Básico atingido, se quiser mais espaços para criar os RoadMap experimente o Planos Premium!");
        }

        if(user.getPlanType() == PlanType.PREMIUM_TIER && roadMap >= 51){
            throw new PlanLimitException("Limite de Roadmaps por usuário atingido, se quiser mais espaços para criar mais RoadMaps entre em contato para um Plano Executivo!");
        }
    }

    @Transactional(readOnly = true)
    public void getRoadMapItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Usuário nao encontrado"));

        long roadMapItems = roadMapItemRepository.countRoadmapItemsByUser(user);

        if(user.getPlanType() == PlanType.FREE_TIER && roadMapItems >= 31){
            throw new PlanLimitException("Limite do Plano Gratuito atingido, se quiser mais espaços para criar os Itens experimente os Planos Premium e Basic!");
        }

        if(user.getPlanType() == PlanType.BASIC_TIER && roadMapItems >= 101){
            throw new PlanLimitException("Limite do Plano Básico atingido, se quiser mais espaços para criar os Itens experimente o Planos Premium!");
        }

        if(user.getPlanType() == PlanType.PREMIUM_TIER && roadMapItems >= 101){
            throw new PlanLimitException("Limite de Roadmaps por usuário atingido, se quiser mais espaços para criar mais Itens entre em contato para um Plano Executivo!");
        }
    }

    public boolean canMakeAICall(User user) {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        AIUsage usage = aiUsageRepository.findByUserIdAndMonth(user.getId(), currentMonth)
                .orElseGet(() -> createNewUsage(user, currentMonth));

        int maxCalls = usage.getPlanLimit().getMaxCalls();
        return usage.getUsageCount() < maxCalls;
    }

    @Transactional
    public void recordAICall(User user) {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        AIUsage usage = aiUsageRepository.findByUserIdAndMonth(user.getId(), currentMonth)
                .orElseGet(() -> createNewUsage(user, currentMonth));

        if (usage.getUsageCount() >= usage.getPlanLimit().getMaxCalls()) {
            throw new CustomException("Limite de chamadas atingido para este mês.");
        }

        usage.setUsageCount(usage.getUsageCount() + 1);
        aiUsageRepository.save(usage);
    }

    private AIUsage createNewUsage(User user, LocalDate currentMonth) {
        AIUsage usage = new AIUsage();
        usage.setUser(user);
        if(user.getPlanType() == PlanType.FREE_TIER){
            usage.setPlanLimit(PlanLimit.FREE);
        }
        if(user.getPlanType() == PlanType.BASIC_TIER){
            usage.setPlanLimit(PlanLimit.BASIC);
        }
        if(user.getPlanType() == PlanType.PREMIUM_TIER){
            usage.setPlanLimit(PlanLimit.PREMIUM);
        }
        usage.setUsageCount(0);
        usage.setUsageMonth(currentMonth);
        aiUsageRepository.save(usage);
        return usage;
    }

    @Scheduled(cron = "0 0 0 7 * ?") // Todo primeiro dia do mês
    public void resetUsage() {
        aiUsageRepository.resetAllUsage();
    }

}

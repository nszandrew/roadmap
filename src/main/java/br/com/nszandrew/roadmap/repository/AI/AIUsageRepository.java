package br.com.nszandrew.roadmap.repository.AI;

import br.com.nszandrew.roadmap.model.AI.AIUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface AIUsageRepository extends JpaRepository<AIUsage, Long> {

    @Query("SELECT a FROM AIUsage a WHERE a.user.id = :id AND a.usageMonth = :currentMonth")
    Optional<AIUsage> findByUserIdAndMonth(Long id, LocalDate currentMonth);

    @Modifying
    @Query("UPDATE AIUsage u SET u.usageCount = 0")
    void resetAllUsage();
}

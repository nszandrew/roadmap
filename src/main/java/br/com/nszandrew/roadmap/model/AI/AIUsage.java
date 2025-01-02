package br.com.nszandrew.roadmap.model.AI;

import br.com.nszandrew.roadmap.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tb_ai_usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private PlanLimit planLimit;

    @Column(nullable = false)
    private Integer usageCount;

    @Column(nullable = false)
    private LocalDate usageMonth;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

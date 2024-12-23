package br.com.nszandrew.roadmap.model.roadmap;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_roadmap_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoadMapItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ElementCollection
    @CollectionTable(name = "tb_roadmapitem_links", joinColumns = @JoinColumn(name = "roadmapitem_id"))
    @Column(name = "link")
    private List<String> links;

    @Column(nullable = false)
    private Integer orderIndex;

    private Integer dificulty;
    private String duration;

    @Enumerated(EnumType.STRING)
    private MapStatus status;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private RoadMap roadMap;
}

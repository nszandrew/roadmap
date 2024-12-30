package br.com.nszandrew.roadmap.model.roadmap;

import br.com.nszandrew.roadmap.model.dto.openai.GPTRoadMapItemDTO;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.CreateRoadMapItem;
import br.com.nszandrew.roadmap.model.dto.roadmapitem.UpdateRoadMapItem;
import br.com.nszandrew.roadmap.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(nullable = false)
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

    private String comment;

    @Column(nullable = false, updatable = false)
    private Boolean generateByIA;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private RoadMap roadMap;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public RoadMapItem(CreateRoadMapItem data, User user, RoadMap roadMap) {
        this.createdAt = LocalDateTime.now();
        this.title = data.title();
        this.description = data.description();
        this.links = new ArrayList<>(data.links());
        this.orderIndex = data.orderIndex();
        this.dificulty = data.dificulty();
        this.duration = data.duration();
        this.status = MapStatus.NOT_STARTED;
        this.generateByIA = false;
        this.user = user;
        this.roadMap = roadMap;
    }

    public RoadMapItem(GPTRoadMapItemDTO data, User user, RoadMap roadMap) {
        this.createdAt = LocalDateTime.now();
        this.title = data.title();
        this.description = data.description();
        this.links = new ArrayList<>(data.links());
        this.orderIndex = data.orderIndex();
        this.dificulty = data.dificulty();
        this.comment = data.comment();
        this.duration = data.duration();
        this.generateByIA = true;
        this.status = MapStatus.NOT_STARTED;
        this.user = user;
        this.roadMap = roadMap;
    }

    public void editRoadMapItem(UpdateRoadMapItem data) {
        this.title = data.title();
        this.description = data.description();
        this.links = new ArrayList<>(data.links());
        this.orderIndex = data.orderIndex();
        this.status = data.status();
        this.dificulty = data.dificulty();
        this.generateByIA = false;
        this.duration = data.duration();
        this.updatedAt = LocalDateTime.now();
    }
}

package br.com.nszandrew.roadmap.repository.Roadmap;

import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import br.com.nszandrew.roadmap.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoadMapItemRepository extends JpaRepository<RoadMapItem, Long> {

    List<RoadMapItem> findByRoadMap(RoadMap roadMap);

    @Query("SELECT COUNT(r) FROM RoadMapItem r WHERE r.user = :user")
    long countRoadmapItemsByUser(@Param("user") User user);

    List<RoadMapItem> findAllByRoadMap(RoadMap roadMap);
}

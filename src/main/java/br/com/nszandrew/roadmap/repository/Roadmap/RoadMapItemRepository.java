package br.com.nszandrew.roadmap.repository.Roadmap;

import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadMapItemRepository extends JpaRepository<RoadMapItem, Long> {

    List<RoadMapItem> findByRoadMap(RoadMap roadMap);
}

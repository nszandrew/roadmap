package br.com.nszandrew.roadmap.repository.Roadmap;

import br.com.nszandrew.roadmap.model.roadmap.RoadMapItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadMapItemRepository extends JpaRepository<RoadMapItem, Long> {
}

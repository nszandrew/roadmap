package br.com.nszandrew.roadmap.repository.Roadmap;

import br.com.nszandrew.roadmap.model.roadmap.RoadMap;
import br.com.nszandrew.roadmap.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {

    boolean existsByUserId(Long userId);

    List<RoadMap> findAllByUser(User user);

    RoadMap findByUser(User user);

    RoadMap findByIdAndUser(Long id, User user);
}

package hu.loylos.loylbot.repository;

import hu.loylos.loylbot.model.Pin;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PinRepository extends CrudRepository<Pin, Long> {

    List<Pin> findByUrl(String url);

}

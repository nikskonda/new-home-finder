package by.nikshkonda.newhomefinder.dataaccess;

import by.nikshkonda.newhomefinder.entity.Apartment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ApartmentRepository extends CrudRepository<Apartment, Long> {

    List<Apartment> findAllByResource(Apartment.Resource resource);

    List<Apartment> findAllByResourceIdInAndResource(Collection<Long> ids, Apartment.Resource resource);


}

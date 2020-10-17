package by.nikshkonda.newhomefinder.service.rw;

import by.nikshkonda.newhomefinder.dataaccess.ApartmentRepository;
import by.nikshkonda.newhomefinder.entity.Apartment;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ApartmentService {

    @Autowired
    private ApartmentRepository repository;

    public Apartment save(Apartment toSave){
        return repository.save(toSave);
    }

    public List<Apartment> saveAll(Collection<Apartment> toSave){
        return Lists.newArrayList(repository.saveAll(toSave));
    }

    public Collection<Apartment> findAll(){
        return Lists.newArrayList(repository.findAll());
    }

    public List<Apartment> findAllByResource(Apartment.Resource resource) {
        return repository.findAllByResource(resource);
    }

    public List<Apartment> findAllByResourceIdInAndResource(Collection<Long> ids, Apartment.Resource resource) {
        return repository.findAllByResourceIdInAndResource(ids, resource);
    }
}

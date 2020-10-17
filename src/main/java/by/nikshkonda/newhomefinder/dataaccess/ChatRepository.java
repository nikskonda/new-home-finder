package by.nikshkonda.newhomefinder.dataaccess;

import by.nikshkonda.newhomefinder.entity.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {

}

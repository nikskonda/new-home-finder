package by.nikshkonda.newhomefinder.service.rw;

import by.nikshkonda.newhomefinder.dataaccess.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private ChatRepository repository;


}

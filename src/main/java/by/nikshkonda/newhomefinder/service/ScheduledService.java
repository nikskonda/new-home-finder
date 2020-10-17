package by.nikshkonda.newhomefinder.service;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.resource.ServiceHelper;
import by.nikshkonda.newhomefinder.service.resource.SettingsService;
import by.nikshkonda.newhomefinder.service.resource.etagi.EtagiService;
import by.nikshkonda.newhomefinder.service.resource.onliner.OnlinerService;
import by.nikshkonda.newhomefinder.service.resource.realt.RealtService;
import by.nikshkonda.newhomefinder.service.resource.ts.TSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledService {

    @Autowired
    private OnlinerService onlinerService;

    @Autowired
    private RealtService realtService;

    @Autowired
    private TSService tsService;

    @Autowired
    private EtagiService etagiService;

    @Autowired
    private NewHomeFinderBot bot;

    @Scheduled(
            fixedRateString = "${parser.SCHEDULED_MIN}",
            initialDelay = 5000)
    public void startScheduled(){
        if ("true".equals(SettingsService.props.get(SettingsService.Properties.SHOW_ALL))) {
            loadApartments(true);
        } else {
            loadApartments(false);
        }
    }

    public void loadApartments(boolean showAll) {
        LocalDateTime start = LocalDateTime.now();
        System.out.println(String.format("Start APARTMENTS loading. Time:%s.", start.toString()));

        List<Apartment> onliner = onlinerService.saveNewOrUpdated(showAll);
        Duration duration = Duration.between(start, LocalDateTime.now());
        System.out.println(String.format("ONLINER: \tNew or Updated:%d,  \tSeconds:%d.", onliner.size(), duration.toSeconds()));
        toChat(onliner);

        LocalDateTime start1 = LocalDateTime.now();
        List<Apartment> real = realtService.saveNewOrUpdated(showAll);
        duration = Duration.between(start1, LocalDateTime.now());
        System.out.println(String.format("REALT: \tNew or Updated:%d,  \tSeconds:%d.", real.size(), duration.toSeconds()));

        LocalDateTime start2 = LocalDateTime.now();
        List<Apartment> ts = tsService.saveNewOrUpdated(showAll);
        duration = Duration.between(start2, LocalDateTime.now());
        System.out.println(String.format("T-S: \tNew or Updated:%d,  \tSeconds:%d.", ts.size(), duration.toSeconds()));

        LocalDateTime start3 = LocalDateTime.now();
        List<Apartment> etagi = etagiService.saveNewOrUpdated(showAll);
        duration = Duration.between(start3, LocalDateTime.now());
        System.out.println(String.format("ETAGI: \tNew or Updated:%d,  \tSeconds:%d.", etagi.size(), duration.toSeconds()));

        LocalDateTime finish = LocalDateTime.now();
        duration = Duration.between(start, finish);
        System.out.println(String.format("Finish APARTMENTS loading. \tTo notification:%d, \tSeconds:%d, \tRequests:%d, \tStart:%s, \tFinish:%s.", real.size()+onliner.size()+ts.size()+etagi.size(), duration.toSeconds(), ServiceHelper.COUNT, start.toString(), finish.toString()));
    }

    private void toChat(List<Apartment> result) {
        result.forEach(aprt -> bot.sendMessage(aprt.toString()));
    }

}

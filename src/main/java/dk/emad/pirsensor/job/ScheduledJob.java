package dk.emad.pirsensor.job;

import dk.emad.pirsensor.service.TradfriService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;


@Component
public class ScheduledJob {

    private static final Logger log = LoggerFactory.getLogger(ScheduledJob.class);
    private final TradfriService tradfriService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ScheduledJob(TradfriService tradfriService) {
        this.tradfriService = tradfriService;
    }

    @PostConstruct
    public void init() {
        final Runnable checker = () -> {
            log.info("checking inactive lights");
            tradfriService.turnOffInactiveLights();
        };
        scheduler.scheduleAtFixedRate(checker, 10, 60, SECONDS); }
}

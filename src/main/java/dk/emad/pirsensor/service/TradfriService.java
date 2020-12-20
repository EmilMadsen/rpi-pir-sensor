package dk.emad.pirsensor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class TradfriService {
    private static final Logger log = LoggerFactory.getLogger(TradfriService.class);
    private static final Integer INACTIVITY_DELAY_MINUTES = 30;

    private final RestTemplate restTemplate;

    @Value("${ikeahome.api}") private String ikeahomeApi;
    @Value("${ikeahome.room}") private String ikeahomeRoom;
    private Instant turnOffAt;
    private Instant lastRequest;

    public TradfriService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void turnOn() {
        // ignore request if has been sent within last 30 seconds.
        Instant cooldown = Instant.now().minusSeconds(30);
        if (lastRequest == null || lastRequest.isBefore(cooldown)) {
            String url = ikeahomeApi + "/api/name/" + ikeahomeRoom + "/on/true/brightness/255";
            String response = restTemplate.getForObject(url, String.class);
            turnOffAt = Instant.now().plusSeconds(60 * INACTIVITY_DELAY_MINUTES);
            lastRequest = Instant.now();
            log.info(response);
        } else {
            log.info("lastRequest was sent within 30 seconds: {}", lastRequest.toString());
        }
    }

    @Scheduled(fixedRate = 1000*60)
    public void turnOffInactiveLights() {
        log.info("turning off inactive lights");
        if (turnOffAt != null) {
            Instant now = Instant.now();
            if(now.isBefore(turnOffAt)) {
                String url = ikeahomeApi + "/api/name/" + ikeahomeRoom + "/on/false/brightness/0";
                String response = restTemplate.getForObject(url, String.class);
                turnOffAt = null;
                log.info(response);
            } else {
                log.info("turnOffAt is not yet - now: {} - turnOffAt: {}", now.toString(), turnOffAt.toString());
            }
        } else {
            log.info("turnOffAt was null - ignoring");
        }
    }
}

package dk.emad.pirsensor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class TradfriService {
    private static final Logger log = LoggerFactory.getLogger(TradfriService.class);
    private static final Integer INACTIVITY_TIMEOUT_MINUTES = 15;
    private static final Integer REQUEST_TIMEOUT_SECONDS = 30;

    private final RestTemplate restTemplate;

    @Value("${ikeahome.api}") private String ikeahomeApi;
    @Value("${ikeahome.room}") private String ikeahomeRoom;
    private Instant turnOffAt;
    private Instant lastRequest;

    public TradfriService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void turnOn() {
        // ignore request if has been sent within timeout window.
        Instant cooldown = Instant.now().minusSeconds(REQUEST_TIMEOUT_SECONDS);
        if (lastRequest == null || lastRequest.isBefore(cooldown)) {
            String url = ikeahomeApi + "/api/name/" + ikeahomeRoom + "/on/true/brightness/255";
            String response = restTemplate.getForObject(url, String.class);
            turnOffAt = Instant.now().plusSeconds(60 * INACTIVITY_TIMEOUT_MINUTES);
            lastRequest = Instant.now();
            log.debug(response);
        } else {
            log.info("lastRequest was sent within {} seconds: {}", REQUEST_TIMEOUT_SECONDS, lastRequest.toString());
        }
    }

    public void turnOffInactiveLights() {
        log.info("turning off inactive lights");
        if (turnOffAt != null) {
            Instant now = Instant.now();
            if(now.isAfter(turnOffAt)) {
                String url = ikeahomeApi + "/api/name/" + ikeahomeRoom + "/on/false/brightness/0";
                String response = restTemplate.getForObject(url, String.class);
                turnOffAt = null;
                log.debug(response);
            } else {
                log.info("turnOffAt is not yet - now: {} - turnOffAt: {}", now.toString(), turnOffAt.toString());
            }
        } else {
            log.info("turnOffAt was null - ignoring");
        }
    }
}

package dk.emad.pirsensor.sensor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import dk.emad.pirsensor.service.TradfriService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MotionSensor {
    private static final Logger log = LoggerFactory.getLogger(MotionSensor.class);

    @Autowired private TradfriService tradfriService;

    private GpioController gpioSensor;
    private GpioPinDigitalInput sensor;

    @PostConstruct
    public void setup() {
        log.info("setting up gpio sensor");
        gpioSensor = GpioFactory.getInstance();
        sensor = gpioSensor.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);
        run();
    }

    private void run() {
        sensor.addListener((GpioPinListenerDigital) event -> {

            if(event.getState().isHigh()){
                log.info("Movement!");
                tradfriService.turnOn();
            }

//            if(event.getState().isLow()){
//                System.out.println("All is quiet...");
//            }

        });

        try {
            while (true){
                Thread.sleep(500);
            }
        }
        catch (final Exception e) {
            log.warn(e.getMessage());
        }
    }

}

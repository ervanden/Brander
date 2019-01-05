package brander;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.time.LocalDateTime;


public class MonitorThread extends Thread {

    Pin pin;
    long p_mil = System.currentTimeMillis(); // time of last pin state change
    Long millisOn = 0l;
    boolean stable = false;
    WSServer wsServer;
    WebCommand webCommand = new WebCommand();
    BranderLogger logger;
    boolean heatingState;

    public MonitorThread(WSServer wsServer, BranderLogger logger, Pin pin) {
        this.wsServer = wsServer;
        this.logger = logger;
        this.pin = pin;
    }

    public boolean getHeatingState() {
        return heatingState;
    }

    public void run() {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput branderSensor = gpio.provisionDigitalInputPin(pin,             // PIN NUMBER
                "branderSensor",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)

        branderSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                stable = false;
                long mil = System.currentTimeMillis();
                long delta = mil - p_mil;
                p_mil = mil;
                if (event.getState().isLow()) {
                    millisOn = millisOn + delta;
                }
                System.out.println(delta + " msec  "
                        + "--> GPIO PIN " + pin
                        + " STATE CHANGE: " + event.getPin()
                        + " = " + event.getState()
                        + "    total ON= " + millisOn.intValue() / 1000
                );
            }
        });

        System.out.println(" ... Listening on " + pin.toString());
        final int SLEEPSEC = 1;
        try {
            while (true) {
                System.out.println("MONITOR LOG stable=" + stable + " isLow()=" + branderSensor.isLow() + " ONTIME=" + millisOn.intValue() / 1000);
                if (stable) { // geen transities in de laatste SLEEPSEC seconden
                    if (branderSensor.isLow()) { // de brander is al minstens SLEEPTIME seconden stabiel OFF
                        if (heatingState == true) {
                            heatingState = false;
                            System.out.println("FIRE OFF");
                            webCommand.command = "fire";
                            webCommand.arg1 = "OFF";
                            wsServer.sendToAll(webCommand.toJSON());
                            if (millisOn > 0) {
                                logOnTime(millisOn);
                                millisOn = 0l;
                            }
                        }
                    } else { // de brander is al minstens SLEEPTIME seconden stabiel ON
                        if (heatingState == false) {
                            heatingState = true;
                            System.out.println("FIRE ON");
                            webCommand.command = "fire";
                            webCommand.arg1 = "ON";
                            wsServer.sendToAll(webCommand.toJSON());
                        }
                    }
                }
                stable = true;
                Thread.sleep(SLEEPSEC * 1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Who interrupted the monitor thread???");
        }
    }

    private void logOnTime(Long millisOn) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.toString() + " " + millisOn.intValue() / 1000);
        logger.log(now, millisOn.intValue() / 1000);
    }
}


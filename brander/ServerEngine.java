package brander;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.*;

public class ServerEngine {   // intellij

    int port;
    int verbosity;
    boolean active;
    boolean test;
    WSServer wsServer;

    static GpioController gpio;
    static GpioPinDigitalOutput Gpio3;  // heating

    private Boolean state = false;

    public ServerEngine(int port, int verbosity, boolean active, boolean test) {
        this.port = port;
        this.verbosity = verbosity;
        this.active = active;
        this.test = test;
    }

    public boolean getState() {
        return state;
    }

    public void changeState(boolean newstate) {
        if (state && !newstate) {  // switch off
            state = false;
            wsServer.sendToAll("OFF");
            if (active) {
                System.out.println("GPIO 3 (heating) set to " + state);
                Gpio3.setState(state);
            }
        } else if (!state && newstate) { // switch on
            state = true;
            System.out.println("Changing state to ON");
            wsServer.sendToAll("ON");
            if (active) {
                System.out.println("GPIO 3 (heating) set to " + state);
                Gpio3.setState(state);
            }
        }
    }

    public void start() {

        wsServer = new WSServer(port);
        wsServer.addListener(new ServerEngineProtocol(this));
        wsServer.start();

        List<Interval> intervals;
        if (active) {
            gpio = GpioFactory.getInstance();
            Gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);
        }

        if (test) {
            intervals = generateTest();
        } else {
            intervals = generateProduction();
        }

        while (true) {
            try {
                LocalDateTime now = LocalDateTime.now();
                System.out.println("--- " + now);
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        }
    }

    private List<Interval> generateProduction() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(new HerhalendInterval("MAANDAG", 5, 0, 6, 45));
        intervals.add(new HerhalendInterval("DINSDAG", 5, 0, 6, 45));
        intervals.add(new HerhalendInterval("WOENSDAG", 5, 0, 6, 45));
        intervals.add(new HerhalendInterval("DONDERDAG", 5, 0, 6, 45));
        intervals.add(new HerhalendInterval("VRIJDAG", 5, 0, 6, 45));
        return intervals;
    }

    final String[] WEEKDAGEN = {"MAANDAG", "DINSDAG", "WOENSDAG", "DONDERDAG", "VRIJDAG"};

    private List<Interval> generateTest() {
        List<Interval> intervals = new ArrayList<>();
        for (String dag : WEEKDAGEN) {
            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < 60; m = m + 20) {
                    intervals.add(new HerhalendInterval(dag, h, m, h, m + 10));
                }
            }
        }
        return intervals;
    }
}

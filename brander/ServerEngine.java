package brander;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.*;

public class ServerEngine {

    int port;
    int verbosity;
    boolean active;
    boolean test;
    WSServer wsServer;
    ServerEngineProtocol serverEngineProtocol;
    ServerEngineThread serverEngineThread;

    static GpioController gpio;
    static GpioPinDigitalOutput Gpio3;  // heating

    public IntervalLijst intervalLijst = new IntervalLijst();
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
            System.out.println("Changing state to OFF");
            WebCommand webCommand = new WebCommand();
            webCommand.command = "status";
            webCommand.arg = "OFF";
            wsServer.sendToAll(webCommand.toJSON());
            if (active) {
                System.out.println("GPIO 3 (heating) set to " + state);
                Gpio3.setState(state);
            }
        } else if (!state && newstate) { // switch on
            state = true;
            System.out.println("Changing state to ON");
            WebCommand webCommand = new WebCommand();
            webCommand.command = "status";
            webCommand.arg = "ON";
            wsServer.sendToAll(webCommand.toJSON());
            if (active) {
                System.out.println("GPIO 3 (heating) set to " + state);
                Gpio3.setState(state);
            }
        }
    }

    public void start() {

        readJSONFile(Brander.scheduleFileName);
        serverEngineThread = new ServerEngineThread();
        serverEngineThread.start();

        serverEngineProtocol = new ServerEngineProtocol(this);
        wsServer = new WSServer(port);
        wsServer.addListener(serverEngineProtocol);
        wsServer.start();

        LocalDate d = LocalDate.now();
        intervalLijst = new IntervalLijst();
        EenmaligInterval i1 = new EenmaligInterval(d, 0, 0, 23, 59);
        intervalLijst.add(i1);

        if (active) {
            gpio = GpioFactory.getInstance();
            Gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);
        }
        serverEngineThread = new ServerEngineThread();
        serverEngineThread.start();

    }

    public void readJSONFile(String fileName) {
        ArrayList<Object> l;
        l = JSON2Object.readJSONFile(fileName, WebCommand.class);
        IntervalLijst newIntervals = new IntervalLijst();
        for (Object o : l) {
            WebCommand webCommand = (WebCommand) o;
            System.out.println(" read from json file " + webCommand.toString());
            System.out.println("   converted to interval " + webCommand.toInterval().toString());
            newIntervals.add(webCommand.toInterval());
        }
        intervalLijst = newIntervals;
    }

    class ServerEngineThread extends Thread {
        public void run() {
            while (true) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println("--- " + now);
                    if (intervalLijst.bevat(now)) {
                        changeState(true);
                    } else {
                        changeState(false);
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    // bij het veranderen van de IntervalLijst (vanuit de web client) interrupten
                    // we de sleep zodat de toestand direct geevalueerd wordt
                    System.out.println("server engine interrrupted!!!");
                }
            }
        }
    }
}

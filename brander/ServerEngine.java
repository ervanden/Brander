package brander;

import com.pi4j.io.gpio.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServerEngine {

    int port;
    int verbosity;
    boolean active;
    boolean test;
    WSServer wsServer = null;
    ServerEngineProtocol serverEngineProtocol;
    ServerEngineThread serverEngineThread;
    MonitorThread monitorThread;

    static GpioController gpio;
    static GpioPinDigitalOutput Gpio3;  // heating

    public IntervalLijst intervalLijst = new IntervalLijst();
    private Boolean state = false;

    public ServerEngine(int port, int verbosity, boolean active) {
        this.port = port;
        this.verbosity = verbosity;
        this.active = active;
        this.test = test;
    }

    public boolean getState() {
        return state;
    }

    public void changeState(boolean newState) {
        if (state && !newState) {  // switch off
            state = false;
            System.out.println("Changing state to OFF");
            WebCommand webCommand = new WebCommand();
            webCommand.command = "status";
            webCommand.arg = "OFF";
            if (wsServer != null) wsServer.sendToAll(webCommand.toJSON());
            if (active) {
                System.out.println("GPIO 3 (heating) set to " + state);
                Gpio3.setState(state);
            }
        } else if (!state && newState) { // switch on
            state = true;
            System.out.println("Changing state to ON");
            WebCommand webCommand = new WebCommand();
            webCommand.command = "status";
            webCommand.arg = "ON";
            if (wsServer != null) wsServer.sendToAll(webCommand.toJSON());
            if (active) {
                System.out.println("GPIO 3 (heating) set to " + state);
                Gpio3.setState(state);
            }
        }
    }

    public void start() {

        readJSONFile(Brander.scheduleFileName);
        for (Interval interval : intervalLijst.getIntervals()) {
            System.out.println("schedule bij start=" + interval.toString());
        }
        serverEngineThread = new ServerEngineThread();
        serverEngineThread.start();

        serverEngineProtocol = new ServerEngineProtocol(this);
        wsServer = new WSServer(port);
        wsServer.addListener(serverEngineProtocol);
        //      wsServer.start();

        if (active) {
            gpio = GpioFactory.getInstance();
            Gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);
        }
        monitorThread = new MonitorThread(wsServer, RaspiPin.GPIO_02);
        monitorThread.start();
    }

    public void readJSONFile(String fileName) {
        ArrayList<Object> l;
        l = JSON2Object.readJSONFile(fileName, WebCommand.class);
        IntervalLijst newIntervals = new IntervalLijst();
        for (Object o : l) {
            WebCommand webCommand = (WebCommand) o;
            newIntervals.add(webCommand.toInterval());
        }
        intervalLijst = newIntervals;
    }

    class ServerEngineThread extends Thread {
        public void run() {
            while (true) {
                try {
                    LocalDateTime now = LocalDateTime.now();
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
                    for (Interval interval : intervalLijst.getIntervals()) {
                        System.out.println("schedule bij interrupt=" + interval.toString());
                    }
                }
            }
        }
    }


}

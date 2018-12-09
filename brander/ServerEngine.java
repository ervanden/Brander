package brander;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public IntervalLijst intervalLijst;
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

        LocalDate d = LocalDate.now();
        intervalLijst = new IntervalLijst();
        EenmaligInterval i1 = new EenmaligInterval(d, 0, 0, 23, 59);
        intervalLijst.add(i1);

        if (active) {
            gpio = GpioFactory.getInstance();
            Gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);
        }

        ServerEngineThread serverEngineThread = new ServerEngineThread();
        serverEngineThread.start();
        serverEngineProtocol = new ServerEngineProtocol(this);
        wsServer = new WSServer(port);
        wsServer.addListener(serverEngineProtocol);
        wsServer.start();
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
                    System.out.println("server engine interrrupted!!!");
                }
            }
        }
    }
}

package brander;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pi4j.io.gpio.*;

public class ServerEngine implements WSServerListener {   // intellij

    int port;
    int verbosity;
    boolean active;
    boolean test;
    WSServer wsServer;

    static GpioController gpio;
    static GpioPinDigitalOutput Gpio3;  // heating

    Boolean state = false;
    List<Interval> intervals = new ArrayList<>();

    public ServerEngine(int port, int verbosity, boolean active, boolean test) {
        this.port = port;
        this.verbosity = verbosity;
        this.active = active;
        this.test = test;
    }

    private void changeState(boolean newstate) {
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
        wsServer.addListener(this);
        wsServer.start();

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
                Date d = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                int day = c.get(Calendar.DAY_OF_WEEK);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                Interval interval = intervals.stream()
                        .filter(i -> (i.day == day))
                        .filter(i -> hour * 60 + minute >= i.fromHour * 60 + i.fromMinute)
                        .filter(i -> hour * 60 + minute <= i.tillHour * 60 + i.tillMinute)
                        .findAny().orElse(null);

                System.out.print("day=" + day + " hour=" + hour + " minute=" + minute);
                if (interval == null) {
                    System.out.println("  not in ON interval - state is "+state);
                    changeState(false);
                } else {
                    System.out.println("  in ON interval- state is "+state);
                    changeState(true);
                }

                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
            ;

        }
    }

    public ArrayList<String> onClientRequest(String clientID, String request) {
        ArrayList<String> reply = new ArrayList<String>();
        reply.add(new Date().toString());
        reply.add("ontvangen door de brander: <" + request + ">");
        reply.add("reply van de brander");
        return reply;
    }

    private List<Interval> generateProduction() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(new Interval(Calendar.MONDAY, 5, 0, 6, 45));
        intervals.add(new Interval(Calendar.MONDAY, 5, 0, 6, 45));
        intervals.add(new Interval(Calendar.TUESDAY, 5, 0, 6, 45));
        intervals.add(new Interval(Calendar.WEDNESDAY, 5, 0, 6, 45));
        intervals.add(new Interval(Calendar.THURSDAY, 5, 0, 6, 45));
        intervals.add(new Interval(Calendar.FRIDAY, 5, 0, 6, 45));
        return intervals;
    }

    private List<Interval> generateTest() {
        List<Interval> intervals = new ArrayList<>();
        for (int dd = Calendar.MONDAY; dd <= Calendar.MONDAY + 7; dd++) {
            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < 60; m = m + 10) {
                    intervals.add(new Interval(dd, h, m, h, m + 5));
                }
            }
        }
        return intervals;
    }
}

package brander;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.pi4j.io.gpio.*;

class Interval { // beschrijft een periode

    int day;
    int fromHour;
    int fromMinute;
    int tillHour;
    int tillMinute;

    public Interval(int day, int h1, int m1, int h2, int m2) {
        this.day = day;
        this.fromHour = h1;
        this.fromMinute = m1;
        this.tillHour = h2;
        this.tillMinute = m2;
    }
}

class BranderDoIt implements WSServerListener {

    static final boolean ACTIVE = false;
    static final boolean PRODUCTION = true;

    static GpioController gpio;
    static GpioPinDigitalOutput Gpio3;  // heating

    List<Interval> intervals = new ArrayList<>();

    public void doIt() {
        if (ACTIVE) {
            gpio = GpioFactory.getInstance();
            Gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);
        }

        if (PRODUCTION) {
            intervals = generateProduction();
        } else {
            intervals = generateTest();
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

                boolean state = false;
                System.out.print("day=" + day + " hour=" + hour + " minute=" + minute);
                if (interval == null) {
                    System.out.println(" OFF");
                    state = false;
                } else {
                    System.out.println(" ON");
                    state = true;
                }

                if (ACTIVE) {
                    System.out.println("GPIO 3 (heating) set to " + state);
                    Gpio3.setState(state);
                }

                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            };

        }
    }

    public ArrayList<String> onClientRequest(String clientID, String request) {
        ArrayList<String> reply = new ArrayList<String>();
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

public class Brander {
    public static void main(String[] args) {
        BranderDoIt branderDoIt = new BranderDoIt();
         WSServer wsServer = new WSServer(4567);
         wsServer.addListener(branderDoIt);
        branderDoIt.doIt();
    }
}

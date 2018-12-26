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


    public MonitorThread(Pin pin) {
        this.pin = pin;
    }

    public void resetMillisOn() {
        millisOn = 0l;
    }

    public int getMillisOn() {
        return millisOn.intValue();
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
//                System.out.println(delta + " msec  "
//                        + "--> GPIO PIN " + pin
//                        + " STATE CHANGE: " + event.getPin()
//                        + " = " + event.getState()
//                        + "    total ON= " + millisOn.intValue() / 1000
//                );
            }
        });

        System.out.println(" ... Listening on " + pin.toString());
        final int SLEEPSEC = 2;
        try {
            while (true) {
                //              System.out.println("MONITOR LOG stable=" + stable + " isLow()=" + branderSensor.isLow() + " ONTIME=" + millisOn.intValue() / 1000);
                if (stable && branderSensor.isLow()) {
                    // de brander is al minstens SLEEPTIME seconden stabiel OFF
                    if (millisOn > 0) {
                        logOnTime(millisOn);
                        millisOn = 0l;
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
    }
}


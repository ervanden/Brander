package brander;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MonitorThread extends Thread {
    Pin pin;
    long p_mil; // time of last pin state change

    public MonitorThread(Pin pin) {
        this.pin = pin;
    }

    public void run() {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(pin,             // PIN NUMBER
                "branderSensor",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)

        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                long mil = System.currentTimeMillis();
                long delta = mil - p_mil;
                p_mil = mil;
                System.out.println(delta + " msec  "
                        + "--> GPIO PIN " + pin
                        + " STATE CHANGE: " + event.getPin()
                        + " = " + event.getState());
            }
        });

        System.out.println(" ... Listening on " + pin.toString());

        try {
            while (true) {
                Thread.sleep(5000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Who interrupted the monitor thread???");
        }
    }
}


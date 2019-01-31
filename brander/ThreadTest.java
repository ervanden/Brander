package brander;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;


public class ThreadTest {

    static float temperature = 15;
    static float TMIN = 8;
    static float TMAX = 10;
    static int ONTIME = 6;
    static int OFFTIME = 6;
    static boolean state;


    static class Temperature extends Thread {
        public void run() {
            while (true) {
                try {
                    if (state) {
                        temperature = temperature + 0.1f;
                    } else {
                        temperature = temperature - 0.1f;
                    }
                    System.out.println(String.format("temperature=%.1f", temperature));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("interrupted while " + state);
                }
            }
        }
    }

    static class DutyCycle extends Thread {
        public void run() {
            while (true) {
                try {
                    state = true;
                    float t1 = temperature;
                    System.out.println("state=" + state);
                    Thread.sleep(ONTIME * 1000);
                    float t2 = temperature;
                    float p = (TMAX - TMIN) / (t2 - t1);
                    System.out.println(String.format(" ONTIME discrepancy=%.1f%%", p));
                    ONTIME = Math.round(ONTIME * p);
                } catch (InterruptedException e) {
                    System.out.println("interrupted while " + state);
                }
                try {
                    state = false;
                    float t1 = temperature;
                    System.out.println("state=" + state);
                    Thread.sleep(OFFTIME * 1000);
                    float t2 = temperature;
                    float p = (TMIN - TMAX) / (t2 - t1);
                    System.out.println(String.format(" OFFTIME discrepancy=%.1f%%", p));
                    OFFTIME = Math.round(OFFTIME * p);

                } catch (InterruptedException e) {
                    System.out.println("interrupted while " + state);
                }
            }

        }
    }

    public static void main(String[] args) {
        Temperature t = new Temperature();
        t.start();

        DutyCycle d = new DutyCycle();
        d.start();

        while (true) {
            try {
                Thread.sleep(1000);
                // d.interrupt();
            } catch (InterruptedException e) {
                System.out.println("main interrupted ");
            }
        }
    }
}



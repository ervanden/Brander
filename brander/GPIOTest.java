package brander;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;


public class GPIOTest {

    public static void main(String[] args) {
        System.out.println("KLAD");
        GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);

        GpioPinDigitalInput gpio2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,
                "branderSensor", PinPullResistance.PULL_DOWN);

        gpio2.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println(
                        "--> GPIO PIN " + gpio2
                                + " STATE CHANGE: " + event.getPin()
                                + " = " + event.getState()
                );
            }
        });

        System.out.println(" ... Listening on " + gpio2.toString());
        final int SLEEPSEC = 2;
        try {
            while (true) {
                gpio3.setState(true);
                Thread.sleep(SLEEPSEC * 1000);
                gpio3.setState(false);
                Thread.sleep(SLEEPSEC * 1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Who interrupted the monitor thread???");
        }
    }
}



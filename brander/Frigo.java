package brander;

import com.pi4j.io.gpio.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;


public class Frigo {

    static boolean STATE = false;
    static FrigoLogger logger;
    static GpioController gpio = GpioFactory.getInstance();
    static GpioPinDigitalOutput gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "compressor", PinState.LOW);

    static void changeState(boolean b) {
        if (b && !STATE) {
            STATE = true;
            gpio3.setState(true);
            System.out.println("SWITCHED ON");
            logger.log(LocalDateTime.now(), "ON");
        }
        if (!b && STATE) {
            STATE = false;
            gpio3.setState(false);
            System.out.println("SWITCHED OFF");
            logger.log(LocalDateTime.now(), "OFF");
        }
    }

    public static void main(String[] args) {
        final int MINTEMP = 6;
        final int MAXTEMP = 8;
        logger = new FrigoLogger("/home/pi/Frigo.log");
        while (true) {
            try {
                Process process = Runtime.getRuntime().exec("python /home/pi/dht22temp.py");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                Integer temperature = null;
                while ((line = reader.readLine()) != null) {
                    try {
                        temperature = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        System.out.println("ongeldige temperatuur : " + line);
                    }
                }
                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (temperature == null)
                        System.out.println("null");
                    else {
                        System.out.println(temperature + " grenzen: " + MINTEMP + "-" + MAXTEMP);
                        if (temperature >= MAXTEMP) {
                            changeState(true);
                        }
                        if (temperature <= MINTEMP) {
                            changeState(false);
                        }
                    }
                } else {
                    System.out.println("python /home/pi/dht22temp.py exit value : " + exitVal);
                }

                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}



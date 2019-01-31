package brander;

import com.pi4j.io.gpio.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;


public class Frigo {

    static boolean STATE;
    static FrigoLogger logger;
    static GpioController gpio = GpioFactory.getInstance();
    static GpioPinDigitalOutput gpio6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "compressor", PinState.LOW);
    static GpioPinDigitalOutput gpio5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "dht22", PinState.LOW);

    static void changeState(boolean b) {
        if (b && !STATE) {
            STATE = true;
            gpio6.setState(true);
            System.out.println("SWITCHED ON");
            logger.log("ON");
        }
        if (!b && STATE) {
            STATE = false;
            gpio6.setState(false);
            System.out.println("SWITCHED OFF");
            logger.log("OFF");
        }
    }

    public static void main(String[] args) {
        final int MINTEMP = 6;
        final int MAXTEMP = 8;
        logger = new FrigoLogger("/home/pi/Frigo.log");
        // compressor uit tot de temperatuur te hoog wordt
        STATE = false;
        gpio6.setState(false);
        // gpio5 stuurt de voedingsspanning van de dht22
        // gpio 5 false = relais gesloten = DHT22 krijgt spanning
        // gpio 5 true  = relais open     = DHT22 zonder spanning
        gpio5.setState(false);
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
                        logger.log("ongeldige temperatuur : " + line);
                    }
                }
                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (temperature == null)
                        System.out.println("null");
                    else {
                        System.out.println(temperature + " grenzen: " + MINTEMP + "-" + MAXTEMP
                                + "   state=" + STATE
                                + "  compressor=" + gpio6.isHigh());
                        if (temperature >= MAXTEMP) {
                            changeState(true);
                        }
                        if (temperature <= MINTEMP) {
                            changeState(false);
                        }
                    }
                } else {
                    System.out.println("python /home/pi/dht22temp.py exit value : " + exitVal);
                    logger.log("RESET DHT22");
                    // we zetten de dht even af om hem te resetten
                    gpio5.setState(true);
                    Thread.sleep(5000);
                    gpio5.setState(false);
                }

                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}



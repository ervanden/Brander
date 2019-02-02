package brander;

import com.pi4j.io.gpio.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;


public class Frigo {

    static boolean STATE;
    static long lastOn;
    static long lastOff;
    static FrigoLogger logger;
    static GpioController gpio = GpioFactory.getInstance();
    static GpioPinDigitalOutput gpio6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "compressor", PinState.LOW);
    static GpioPinDigitalOutput gpio5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "dht22", PinState.LOW);

    static String printTimeInMillis(long t) {
        if (t > 24 * 3600 * 1000) {
            return "-";
        } else {
            Long deltaSeconds = t / 1000;
            int totalSeconds = deltaSeconds.intValue();
            int totalMinutes = totalSeconds / 60;  // aantal volledige minuten
            int sec = totalSeconds % 60;
            int totalHours = totalMinutes / 60; // aantal volledige uren
            int min = totalMinutes % 60;
            String hourString = (totalHours > 0) ? (totalHours + "h") : "";
            String minuteString = min + "m";
            String secondString = sec + "s";
            return hourString + minuteString + secondString;
        }
    }

    static void changeState(boolean b) {
        if (b && !STATE) {
            STATE = true;
            gpio6.setState(true);
            System.out.println("SWITCHED ON");
            lastOn = System.currentTimeMillis();
            logger.log(printTimeInMillis(lastOn - lastOff) + " > ON");
        }
        if (!b && STATE) {
            STATE = false;
            gpio6.setState(false);
            System.out.println("SWITCHED OFF");
            lastOff = System.currentTimeMillis();
            logger.log(printTimeInMillis(lastOff - lastOn) + " > OFF");
        }
    }

    public static void main(String[] args) {
        int MINTEMP = 6; // temperatuur waarbij de compressor aangezet wordt
        int MAXTEMP = 8; // temperatuur waarbij de compressor afgezet wordt
        int POLLING = 20; // om de hoeveel seconden wordt de temperatuur gemeten

        for (int arg = 1; arg <= args.length; arg++) {
            String[] s = args[arg - 1].split("=");
            if (s[0].equals("min")) {
                MINTEMP = Integer.parseInt(s[1]);
            } else if (s[0].equals("max")) {
                MAXTEMP = Integer.parseInt(s[1]);
            } else if (s[0].equals("polling")) {
                POLLING = Integer.parseInt(s[1]);
            }
        }

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
                    // we resetten de dht door hem even af en aan te zetten
                    gpio5.setState(true);
                    Thread.sleep(5000);
                    gpio5.setState(false);
                }

                Thread.sleep(POLLING * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}



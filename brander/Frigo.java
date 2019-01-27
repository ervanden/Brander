package brander;

import com.pi4j.io.gpio.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Frigo {


    public static void main(String[] args) {
        final int MINTEMP = 8;
        final int MAXTEMP = 9;
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "compressor", PinState.LOW);
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
                        if (temperature > MAXTEMP) {
                            gpio3.setState(true);
                            System.out.println("SWITCHED ON");
                        }
                        if (temperature < MINTEMP) {
                            gpio3.setState(false);
                            System.out.println("SWITCHED OFF");
                        }
                    }
                } else {
                    System.out.println("python /home/pi/dht22temp.py exit value : " + exitVal);
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}



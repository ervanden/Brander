package brander;

import com.pi4j.io.gpio.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Frigo {


    public static void main(String[] args) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "compressor", PinState.LOW);
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
                    }
                }
                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (temperature == null)
                        System.out.println("null");
                    else {
                        System.out.println(temperature);
                        if (temperature > 20) {
                            gpio3.setState(false);
                            Thread.sleep(300);
                            gpio3.setState(true);
                            Thread.sleep(300);
                            gpio3.setState(false);
                            Thread.sleep(300);
                            gpio3.setState(true);
                            Thread.sleep(300);
                            gpio3.setState(false);
                        }
                        if (temperature < 18) {
                            gpio3.setState(true);
                            Thread.sleep(300);
                            gpio3.setState(false);
                            Thread.sleep(300);
                            gpio3.setState(true);
                            Thread.sleep(300);
                            gpio3.setState(false);
                            Thread.sleep(300);
                            gpio3.setState(true);
                        }
                    }
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}



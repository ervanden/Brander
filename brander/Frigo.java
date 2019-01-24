package brander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Frigo {


    public static void main(String[] args) throws InterruptedException {
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
                    else
                        System.out.println(temperature);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}



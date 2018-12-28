package brander;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BranderLogger {

    String fileNaam;

    public BranderLogger(String fileNaam) {
        this.fileNaam = fileNaam;
    }

    public void log(LocalDateTime timeStamp, int onSeconds) {
        try {
            FileWriter fw = new FileWriter(fileNaam, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(timeStamp.toString() + "|" + onSeconds);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
        }
    }

    public int onTime(LocalDateTime van, LocalDateTime tot) {
        try {
            FileReader fr = new FileReader(fileNaam);
            BufferedReader br = new BufferedReader(fr);
            int onTime = 0;
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("[|]");
                if (tokens.length == 2) {
                    System.out.println(tokens[0] + " - " + tokens[1]);
                    LocalDateTime timeStamp = LocalDateTime.parse(tokens[0], DateTimeFormatter.ISO_DATE_TIME);
                    int seconds = Integer.parseInt(tokens[1]);
                    System.out.println("timeStamp=" + timeStamp.toString() + " seconds=" + seconds);
                    if (timeStamp.isAfter(van) && timeStamp.isBefore(tot)) {
                        onTime = onTime + seconds;
                        System.out.println("adding " + seconds);
                    }

                }
            }
            br.close();
        } catch (IOException e) {
        }
        return 0;
    }
}

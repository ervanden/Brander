package brander;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class BranderLogger {

    String fileNaam;

    public BranderLogger(String fileNaam) {
        this.fileNaam = fileNaam;
    }

    public void log(LocalDateTime timeStamp, int onSeconds) {
        try {
            FileWriter fw = new FileWriter(fileNaam, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(timeStamp.toString() + ":" + onSeconds);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
        }
    }
}

package brander;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FrigoLogger {

    static String fileNaam;

    public FrigoLogger(String fileNaam) {
        this.fileNaam = fileNaam;
    }

    public void log(String message) {
        try {
            FileWriter fw = new FileWriter(fileNaam, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(LocalDateTime.now() + "|" + message);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
        }
    }

}

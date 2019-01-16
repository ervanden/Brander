package brander;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Klad {

    public static void main(String[] args) {
        System.out.println("KLAD");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E dd/MM/yyyy");
        LocalDate datum = LocalDate.parse("Fri 04/01/2019", formatter);

        List<MinuutStatus> l = new BranderLogger("C:\\Users\\erikv\\Downloads\\BranderLog.txt").statusPerMinuut(datum, 10);
        for (MinuutStatus minuutStatus : l) {
            System.out.println(minuutStatus.getUur() + ":" + minuutStatus.getMinuut() + " " + minuutStatus.isStatus());
        }
    }


}



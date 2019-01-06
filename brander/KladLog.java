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


public class KladLog {

    public static void main(String[] args) {
        System.out.println("KLAD");
        List<DagTotaal> dagTotalen = new BranderLogger(Brander.logFileName).dagTotalen();

    }


}



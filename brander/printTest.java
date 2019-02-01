package brander;

import com.pi4j.io.gpio.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class printTest {


    static String printTimeInMillis(long t) {
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

    public static void main(String[] args) {
        System.out.println(printTimeInMillis((2 * 3600 + 57 * 60 + 44) * 1000));
    }

}



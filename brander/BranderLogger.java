package brander;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BranderLogger {

    static String fileNaam;

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

    public static List<MinuutStatus> statusPerMinuut(LocalDate datum) {
        List<MinuutStatus> MinuutStatusLijst = new ArrayList<>();
        // de array 'minuten' houdt de status bij van elke minuut op 'datum'
        // 13:44 komt overeen met minuten[13*60+44]  uren gaan van 0-23 minuten van 0-59
        boolean[] minuten = new boolean[24 * 60];
        for (int i = 0; i < 24 * 60; i++) {
            minuten[i] = false;
        }
        // lees de log file en zoek de lijnen met deze datum
        try {
            System.out.println("Opening " + fileNaam);
            File file = new File(fileNaam);
            InputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader inputStream = new BufferedReader(isr);

            String line;
            while ((line = inputStream.readLine()) != null) {
                String[] words = line.split("[|]");
                if (words.length == 2) {
                    String dateString = words[0];
                    String secondsString = words[1];
                    Integer seconds = Integer.parseInt(secondsString);
                    LocalDateTime dt = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
                    LocalDate d = dt.toLocalDate();
                    if (d.equals(datum)) {
                        System.out.println("On " + dt + " seconds=" + seconds);
                        int uur = dt.getHour();
                        int minuut = dt.getMinute();
                        int index = uur * 60 + minuut;
                        Long lminutesON = Math.round(seconds / 60.0);
                        int minutesON = lminutesON.intValue();
                        for (int i = 0; (i < minutesON) && (i <= index); i++) {
                            minuten[index - i] = true;
                        }
                    }
                }
            }

            for (int uur = 0; uur < 24; uur++) {
                for (int minuut = 0; minuut < 60; minuut++) {
                    MinuutStatusLijst.add(new MinuutStatus(uur, minuut, minuten[uur * 60 + minuut]));
                }
            }

            inputStream.close();
        } catch (FileNotFoundException fnf) {
            System.out.println("file not found");
        } catch (IOException io) {
            System.out.println("io exception");
        }

        return MinuutStatusLijst;
    }

    public static List<DagTotaal> dagTotalen(int laatsteZoveelDagen) {
        // maak een lijst met de gevraagde dagen
        List<DagTotaal> dagTotalen = new ArrayList<>();
        LocalDate vandaag = LocalDate.now();
        LocalDate dag = vandaag.minusDays(laatsteZoveelDagen - 1);
        while (!dag.equals(vandaag)) {
            System.out.println("adding tot dagtotalen " + dag.toString());
            dagTotalen.add(new DagTotaal(dag, 0));
            dag = dag.plusDays(1);
        }
        System.out.println("adding tot dagtotalen " + vandaag.toString());
        dagTotalen.add(new DagTotaal(vandaag, 0));

        // lees de log file en vul de waarde voor deze dagen in de lijst dagTotalen
        try {
            System.out.println("Opening " + fileNaam);
            File file = new File(fileNaam);
            InputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader inputStream = new BufferedReader(isr);

            String line;
            LocalDate sumDate = null;
            int sumSeconds = 0;
            while ((line = inputStream.readLine()) != null) {
                String[] words = line.split("[|]");
                if (words.length == 2) {
                    String dateString = words[0];
                    String secondsString = words[1];
                    LocalDate date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
                    Integer seconds = Integer.parseInt(secondsString);
                    if (sumDate == null) {
                        sumDate = date;
                    }
                    if (date.equals(sumDate)) {
                        sumSeconds = sumSeconds + seconds;
                    } else {
                        System.out.println("Total on " + sumDate + " seconds=" + sumSeconds);
                        DagTotaal.setDagTotaal(dagTotalen, sumDate, sumSeconds);
                        sumDate = date;
                        sumSeconds = seconds;
                    }
                    System.out.println("On " + date + " seconds=" + seconds);
                }
            }
            System.out.println("Total on " + sumDate + " seconds=" + sumSeconds);
            DagTotaal.setDagTotaal(dagTotalen, sumDate, sumSeconds);
            inputStream.close();
        } catch (FileNotFoundException fnf) {
            System.out.println("file not found");
        } catch (IOException io) {
            System.out.println("io exception");
        } catch (IllegalArgumentException io) {
            System.out.println("niet alle datums in de log file zijn <= vandaag");
        }

        return dagTotalen;
    }
}

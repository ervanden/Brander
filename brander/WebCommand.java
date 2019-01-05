package brander;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class WebCommand {
    public String command;
    public String arg1;
    public String arg2;
    public String dag;
    public Integer vanuur;
    public Integer vanmin;
    public Integer totuur;
    public Integer totmin;

    public WebCommand() {

    }

    public WebCommand(Interval interval) {
        dag = interval.getDag();
        vanuur = interval.getVanUur();
        totuur = interval.getTotUur();
        totmin = interval.getTotMinuut();
        vanmin = interval.getVanMinuut();
    }

    public Interval toInterval() {
        Interval interval;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
            LocalDate datum = LocalDate.parse(dag, formatter);
            interval = new EenmaligInterval(datum, vanuur, vanmin, totuur, totmin);
        } catch (DateTimeParseException e) {
            interval = new HerhalendInterval(dag, vanuur, vanmin, totuur, totmin);
        }
        return interval;
    }

    public String toString() {
        String s = "";
        s = s + "{\n";
        s = s + "command : " + command + "\n";
        s = s + "arg : " + arg1 + "\n";
        s = s + "dag : " + dag + "\n";
        s = s + "vanuur : " + vanuur + "\n";
        s = s + "vanmin : " + vanmin + "\n";
        s = s + "totuur : " + totuur + "\n";
        s = s + "totmin : " + totmin + "\n";
        s = s + "}";
        return s;
    }

    public String toJSON() {
        return "{"
                + "\"command\":\"" + command + "\","
                + "\"arg\":\"" + arg1 + "\","
                + "\"dag\":\"" + dag + "\","
                + "\"vanuur\":\"" + vanuur + "\","
                + "\"vanmin\":\"" + vanmin + "\","
                + "\"totuur\":\"" + totuur + "\","
                + "\"totmin\":\"" + totmin + "\""
                + "}";
    }
}

package brander;


import javax.swing.text.DateFormatter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EenmaligInterval extends TijdsInterval implements Interval {

    public LocalDate datum;

    public String toString() {
        LocalDateTime van = datum.atTime(vanUur, vanMinuut);
        LocalDateTime tot = datum.atTime(totUur, totMinuut);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y HH:mm");
        return "VAN " + van.format(formatter) + " TOT " + tot.format(formatter);
    }

    public EenmaligInterval(LocalDate datum, int vanUur, int vanMinuut, int totUur, int totMinuut) {
        super(vanUur, vanMinuut, totUur, totMinuut);
        this.datum = datum;
    }


    public boolean bevat(LocalDateTime dt) {
        int uur = dt.getHour();
        int minuut = dt.getMinute();
        LocalDate d = dt.toLocalDate();
        return this.bevat(uur, minuut) && datum.isEqual(d);
    }

    public String getDag() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
        return datum.format(formatter);
    }

    @Override
    public int getVanUur() {
        return vanUur;
    }

    @Override
    public int getTotUur() {
        return totUur;
    }

    @Override
    public int getVanMinuut() {
        return vanMinuut;
    }

    @Override
    public int getTotMinuut() {
        return totMinuut;
    }


}

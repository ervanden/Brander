package brander;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class Interval extends TijdsInterval {

    public Interval(int vanUur,
                    int vanMinuut,
                    int totUur,
                    int totMinuut) {
        this.vanMinuut = vanMinuut;
        this.vanUur = vanUur;
        this.totMinuut = totMinuut;
        this.totUur = totUur;
    }

    public abstract boolean bevat(LocalDateTime dt);

    public abstract String getDag();

    public int getVanUur() {
        return vanUur;
    }

    public int getTotUur() {
        return totUur;
    }

    public int getVanMinuut() {
        return vanMinuut;
    }

    public int getTotMinuut() {
        return totMinuut;
    }

}

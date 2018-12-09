package brander;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HerhalendInterval extends TijdsInterval implements Interval {

    public DayOfWeek dag;

    public HerhalendInterval(String dagNaam, int vanUur, int vanMinuut, int totUur, int totMinuut) {
        super(vanUur, vanMinuut, totUur, totMinuut);
        this.dag = toDayOfWeek(dagNaam);
    }

    public String toString() {
        return "ELKE " + dag + " " + tijdsIntervalString();
    }

    public boolean bevat(LocalDateTime dt) {
        int uur = dt.getHour();
        int minuut = dt.getMinute();
        DayOfWeek dtdag = dt.getDayOfWeek();
        return this.bevat(uur, minuut) && dag == dtdag;
    }

    public String getDag() {
        return dag.toString();
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

    public static DayOfWeek toDayOfWeek(String dayName) {
        DayOfWeek day;
        switch (dayName) {
            case "MONDAY":
                day = DayOfWeek.MONDAY;
                break;
            case "TUESDAY":
                day = DayOfWeek.TUESDAY;
                break;
            case "WEDNESDAY":
                day = DayOfWeek.WEDNESDAY;
                break;
            case "THURSDAY":
                day = DayOfWeek.THURSDAY;
                break;
            case "FRIDAY":
                day = DayOfWeek.FRIDAY;
                break;
            case "SATURDAY":
                day = DayOfWeek.SATURDAY;
                break;
            case "SUNDAY":
                day = DayOfWeek.SUNDAY;
                break;
            default:
                throw new RuntimeException("onbekende dag " + dayName);
        }
        return day;
    }

}

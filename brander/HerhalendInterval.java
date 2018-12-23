package brander;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HerhalendInterval extends Interval {

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

    @Override
    public boolean isVoorbij() {
        return false;
    } // herhalend interval is nooit voorbij

    @Override
    public String getDag() {
        return dag.toString();
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

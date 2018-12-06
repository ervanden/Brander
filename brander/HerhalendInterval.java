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

    public static DayOfWeek toDayOfWeek(String dayName) {
        DayOfWeek day;
        switch (dayName.toLowerCase()) {
            case "maandag":
                day = DayOfWeek.MONDAY;
                break;
            case "dinsdag":
                day = DayOfWeek.TUESDAY;
                break;
            case "woensdag":
                day = DayOfWeek.WEDNESDAY;
                break;
            case "donderdag":
                day = DayOfWeek.THURSDAY;
                break;
            case "vrijdag":
                day = DayOfWeek.FRIDAY;
                break;
            case "zaterdag":
                day = DayOfWeek.SATURDAY;
                break;
            case "zondag":
                day = DayOfWeek.SUNDAY;
                break;
            default:
                throw new RuntimeException("onbekende dag " + dayName);
        }
        return day;
    }

}

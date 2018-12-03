package brander;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Interval {

    public boolean elkeWeek;
    public DayOfWeek dag;
    public LocalDateTime van;  // only on this date
    public LocalDateTime tot;


    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y H:m");
        return "VAN " + van.format(formatter) + " TOT " + tot.format(formatter);
    }

    public Interval(LocalDateTime van, LocalDateTime tot) {
        this.elkeWeek = false;
        this.van = van;
        this.tot = tot;
    }

    public Interval(String dag, LocalDateTime van, LocalDateTime tot) {
        this.elkeWeek = true;
        this.dag = toDayOfWeek(dag);
        this.van = van;
        this.tot = tot;
    }

    public boolean contains(LocalDateTime d) {
        if (d != null) {
          if (!elkeWeek){
              return d.isAfter(van) && d.isBefore(tot);
          } else {
              LocalDateTime d1=d.withDayOfYear(d.getDayOfYear());
              return d1.isAfter(van) && d1.isBefore(tot);
          }
        }
        return false;
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

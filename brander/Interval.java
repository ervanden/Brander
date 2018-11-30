package brander;

import java.util.Calendar;
import java.util.Date;

public class Interval {

    public Integer day;  //  every week on this day (if date == null)
    public Date date;  // only on this date
    public Integer fromHour;
    public Integer fromMinute;
    public Integer tillHour;
    public Integer tillMinute;


    public String toString(){
        if (date==null) {
            return String.format("%s  %d:%d - %d:%d", day, fromHour, fromMinute, tillHour, tillMinute);
        } else {
            return String.format("%s  %d:%d - %d:%d", date.toString(), fromHour, fromMinute, tillHour, tillMinute);
        }
    }

    public Interval(String dayName, int  h1, int m1, int h2, int m2) {
        this.date=null;
        this.day = getDay(dayName);
        this.fromHour = h1;
        this.fromMinute = m1;
        this.tillHour = h2;
        this.tillMinute = m2;
    }

    public Interval(Date date,int  h1, int m1, int h2, int m2) {
        this.date=date;
        this.fromHour = h1;
        this.fromMinute = m1;
        this.tillHour = h2;
        this.tillMinute = m2;
    }

    public Interval(int day,int  h1, int m1, int h2, int m2) {
        this.date=null;
        this.day=day;
        this.fromHour = h1;
        this.fromMinute = m1;
        this.tillHour = h2;
        this.tillMinute = m2;
    }

    public static int getDay(String dayName) {
        int day;
        switch (dayName.toLowerCase()) {
            case  "maandag": day=Calendar.MONDAY; break;
            case  "dinsdag": day=Calendar.TUESDAY ; break;
            case  "woensdag": day=Calendar.WEDNESDAY ; break;
            case  "donderdag": day=Calendar.THURSDAY ; break;
            case  "vrijdag": day=Calendar.FRIDAY ; break;
            case  "zaterdag": day=Calendar.SATURDAY ; break;
            case  "zondag": day=Calendar.SUNDAY ; break;
            default: throw new RuntimeException("onbekende dag "+dayName);
        }
        return day;
    }

}

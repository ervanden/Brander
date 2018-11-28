package brander;

public class Interval {

    public Integer day;
    public Integer fromHour;
    public Integer fromMinute;
    public Integer tillHour;
    public Integer tillMinute;

    public Interval(int day, int h1, int m1, int h2, int m2) {
        this.day = day;
        this.fromHour = h1;
        this.fromMinute = m1;
        this.tillHour = h2;
        this.tillMinute = m2;
    }
}

package brander;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IntervalLijst {

    private List<Interval> intervals = new ArrayList<Interval>();

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public void add(Interval interval) {
        intervals.add(interval);
    }

    public boolean bevat(LocalDateTime dt) {
        for (Interval interval : intervals) {
            //           System.out.println("BEVAT? "+interval.toString()+" "+dt.toString()+ " = " +interval.bevat(dt));
            if (interval.bevat(dt)) return true;
        }
        return false;
    }
}

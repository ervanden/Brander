package brander;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IntervalLijst {
    public List<Interval> intervals = new ArrayList<Interval>();

    public void reset() {
        intervals = new ArrayList<Interval>();
    }

    public void add(Interval interval) {
        intervals.add(interval);
    }

    public boolean bevat(LocalDateTime dt) {
        for (Interval interval : intervals) {
            if (interval.bevat(dt)) return true;
        }
        return false;
    }
}

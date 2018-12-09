package brander;

import java.time.LocalDateTime;

public interface Interval {
    public boolean bevat(LocalDateTime dt);

    public String getDag();

    public int getVanUur();

    public int getTotUur();

    public int getVanMinuut();

    public int getTotMinuut();
}

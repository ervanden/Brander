package brander;

import java.time.LocalDate;

public class DagTotaal {
    private LocalDate datum;
    private int seconden;

    public DagTotaal(LocalDate datum, int seconden) {
        this.datum = datum;
        this.seconden = seconden;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public int getSeconden() {
        return seconden;
    }

    public void setSeconden(int seconden) {
        this.seconden = seconden;
    }
}

package brander;

public class TijdsInterval {
    int vanUur;
    int vanMinuut;
    int totUur;
    int totMinuut;

    public TijdsInterval() {
    }

    public TijdsInterval(int vanUur,
                         int vanMinuut,
                         int totUur,
                         int totMinuut) {
        this.vanMinuut = vanMinuut;
        this.vanUur = vanUur;
        this.totMinuut = totMinuut;
        this.totUur = totUur;
    }

    public boolean bevat(int uur, int minuut) {
        int m = 60 * uur + minuut;
        return m >= (60 * vanUur + vanMinuut) && m <= (60 * totUur + totMinuut);
    }

    public String tijdsIntervalString() {
        return "VAN " + vanUur + ":" + vanMinuut + " TOT " + totUur + ":" + totMinuut;
    }
}

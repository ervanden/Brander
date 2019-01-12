package brander;

import java.time.LocalDate;
import java.util.List;

public class MinuutStatus {
    private int uur;
    private int minuut;
    private boolean status;

    public MinuutStatus(int uur, int minuut, boolean status) {
        this.uur = uur;
        this.minuut = minuut;
        this.status = status;
    }

    public int getUur() {
        return uur;
    }

    public void setUur(int uur) {
        this.uur = uur;
    }

    public int getMinuut() {
        return minuut;
    }

    public void setMinuut(int minuut) {
        this.minuut = minuut;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

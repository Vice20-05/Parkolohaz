/**
 * Fake clock for tests and for the demo menu: time only moves when WE move it.
 */
public class LeptethetoOra implements Ora {

    private long perc;

    public LeptethetoOra() {
        this(0);
    }

    public LeptethetoOra(long kezdoPerc) {
        this.perc = kezdoPerc;
    }

    @Override
    public long most() {
        return perc;
    }

    /** Moves the time forward. Time never goes backwards. */
    public void eltol(long percek) {
        if (percek < 0) {
            throw new IllegalArgumentException("Az idő nem mehet visszafelé!");
        }
        perc += percek;
    }
}

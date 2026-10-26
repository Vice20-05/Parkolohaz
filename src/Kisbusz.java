/** Minibus: bigger, more expensive, shorter free period. 15 min free, 8 lei / h, max 60 lei / day. */
public class Kisbusz extends Jarmu {

    private static final long INGYENES_PERC = 15;
    private static final double ORADIJ = 8.0;
    private static final double NAPI_PLAFON = 60.0;

    public Kisbusz(String rendszam) {
        super(rendszam);
    }

    @Override
    public double dij(long percek) {
        return idoalapuDij(percek, INGYENES_PERC, ORADIJ, NAPI_PLAFON);
    }
}

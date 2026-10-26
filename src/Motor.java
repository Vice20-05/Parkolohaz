/** Motorbike: takes less space, so it's cheaper. 30 min free, 2 lei / h, max 12 lei / day. */
public class Motor extends Jarmu {

    private static final long INGYENES_PERC = 30;
    private static final double ORADIJ = 2.0;
    private static final double NAPI_PLAFON = 12.0;

    public Motor(String rendszam) {
        super(rendszam);
    }

    @Override
    public double dij(long percek) {
        return idoalapuDij(percek, INGYENES_PERC, ORADIJ, NAPI_PLAFON);
    }
}

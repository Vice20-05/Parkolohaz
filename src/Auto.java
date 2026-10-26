/** Normal car: first 30 min free, then 5 lei / started hour, max 40 lei / day. */
public class Auto extends Jarmu {

    // Constants instead of "magic numbers" scattered in the code
    private static final long INGYENES_PERC = 30;
    private static final double ORADIJ = 5.0;
    private static final double NAPI_PLAFON = 40.0;

    public Auto(String rendszam) {
        super(rendszam); // validation happens in Jarmu - no need to repeat it here
    }

    @Override
    public double dij(long percek) {
        return idoalapuDij(percek, INGYENES_PERC, ORADIJ, NAPI_PLAFON);
    }
}

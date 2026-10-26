/**
 * Electric car = a car (IS-A Auto) + it pays extra for charging.
 *
 * Because it extends Auto, we REUSE the parking fee with super.dij(...)
 * and only add the charging part. If Auto's prices change, this follows automatically.
 */
public class ElektromosAuto extends Auto {

    /** Charging: 3 lei for every started hour, no free period, no daily cap. */
    private static final double TOLTES_ORADIJ = 3.0;

    public ElektromosAuto(String rendszam) {
        super(rendszam);
    }

    @Override
    public double dij(long percek) {
        double parkolas = super.dij(percek);                     // also validates percek >= 0
        double toltes = Math.ceilDiv(percek, 60) * TOLTES_ORADIJ;
        return parkolas + toltes;
    }
}

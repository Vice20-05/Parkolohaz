/**
 * Common base class of every vehicle that can enter the parking garage.
 *
 * WHY ABSTRACT?
 *  - There is no "just a vehicle" in real life: every car is an Auto, a Motor, ...
 *    so `new Jarmu(...)` must be impossible.
 *  - It holds what EVERY vehicle has in common (the licence plate) exactly once.
 *  - It forces every subclass to implement dij(...) - the compiler checks it for us.
 *
 * OCP (Open/Closed Principle): a new vehicle type (e.g. Lakoauto next year)
 * = one new subclass of Jarmu. Parkolohaz does not change at all.
 */
public abstract class Jarmu {

    /** Minutes in one day - used for the daily cap (napi plafon). */
    public static final long PERC_NAPONTA = 24 * 60;

    // private + final: nobody can change the plate after the object is created (encapsulation)
    private final String rendszam;

    // protected: only subclasses may call it (through super(...)), outsiders can't
    protected Jarmu(String rendszam) {
        // Validate FIRST, assign after. An invalid object must never exist.
        if (rendszam == null || rendszam.isBlank()) {
            throw new IllegalArgumentException("A rendszám nem lehet null vagy üres!");
        }
        this.rendszam = normalizal(rendszam);
    }

    /**
     * " ms-01-abc " and "MS-01-ABC" should be the same car.
     * Package-private + static so Parkolohaz can use the same rule in kihajt(...).
     */
    static String normalizal(String rendszam) {
        return rendszam.trim().toUpperCase();
    }

    public String getRendszam() {
        return rendszam;
    }

    /** Fee in lei for the given number of parked minutes. Every subclass decides on its own. */
    public abstract double dij(long percek);

    /**
     * Shared helper so the subclasses don't copy-paste the same math 4 times.
     * Rules:
     *   - the first `ingyenesPerc` minutes are free,
     *   - after that every STARTED hour costs `oradij`,
     *   - one day never costs more than `napiPlafon`,
     *   - every full day (1440 min) costs exactly `napiPlafon`.
     *
     * Example (Auto: 30 free, 5 lei/h, 40 cap):
     *   0 min -> 0,  30 min -> 0,  31 min -> 5 (1 started hour),  91 min -> 10,  1439 min -> 40
     */
    protected static double idoalapuDij(long percek, long ingyenesPerc, double oradij, double napiPlafon) {
        if (percek < 0) {
            throw new IllegalArgumentException("A parkolási idő nem lehet negatív: " + percek);
        }
        long teljesNapok = percek / PERC_NAPONTA;   // whole days
        long maradek = percek % PERC_NAPONTA;       // minutes of the last, unfinished day

        double maradekDij = 0;
        if (maradek > ingyenesPerc) {
            // Math.ceilDiv rounds UP: 1 minute over the free time = 1 whole started hour
            long megkezdettOrak = Math.ceilDiv(maradek - ingyenesPerc, 60);
            maradekDij = Math.min(megkezdettOrak * oradij, napiPlafon);
        }
        return teljesNapok * napiPlafon + maradekDij;
    }

    @Override
    public String toString() {
        // getSimpleName() is only used for DISPLAY, never to decide the price
        return getClass().getSimpleName() + " [" + rendszam + "]";
    }
}

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The parking garage: levels x spots, entering, exiting, fees, revenue.
 *
 * IMPORTANT RULE OF THE TASK: there is NO instanceof and NO switch on the vehicle type
 * in this class. The fee is always `jarmu.dij(percek)` - Java calls the right
 * subclass method at runtime (polymorphism / dynamic dispatch).
 * That's why a new vehicle type needs zero changes here (OCP).
 */
public class Parkolohaz {

    private final Ora ora;

    // helyek[szint][hely] -> the vehicle standing there, or null if the spot is free
    private final Jarmu[][] helyek;

    // plate -> ticket. A Map gives fast lookup by plate (kihajt, duplicate check).
    private final Map<String, Parkolas> parkolasok = new HashMap<>();

    // day number (minutes / 1440) -> revenue that day
    private final Map<Long, Double> bevetelNaponta = new HashMap<>();

    /**
     * Dependency injection: the clock comes from OUTSIDE, so tests can pass a fake one.
     */
    public Parkolohaz(int szintekSzama, int helyekSzintenkent, Ora ora) {
        if (szintekSzama <= 0 || helyekSzintenkent <= 0) {
            throw new IllegalArgumentException("A szintek és helyek száma pozitív kell legyen!");
        }
        this.ora = Objects.requireNonNull(ora, "Az óra nem lehet null!");
        this.helyek = new Jarmu[szintekSzama][helyekSzintenkent];
    }

    /**
     * Parks the vehicle on the first free spot (lowest level first).
     *
     * @return the ticket (level, spot, arrival time)
     * @throws DuplikaltBehajtasException if this plate is already inside
     * @throws TeltHazException           if every spot is taken
     */
    public Parkolas behajt(Jarmu j) {
        Objects.requireNonNull(j, "A jármű nem lehet null!");

        // Check errors BEFORE changing anything, so a failed call leaves the state untouched
        if (parkolasok.containsKey(j.getRendszam())) {
            throw new DuplikaltBehajtasException(j.getRendszam());
        }

        for (int szint = 0; szint < helyek.length; szint++) {
            for (int hely = 0; hely < helyek[szint].length; hely++) {
                if (helyek[szint][hely] == null) {
                    helyek[szint][hely] = j;
                    Parkolas p = new Parkolas(j, szint, hely, ora.most());
                    parkolasok.put(j.getRendszam(), p);
                    return p;
                }
            }
        }
        throw new TeltHazException();
    }

    /**
     * Removes the vehicle, computes the fee and adds it to the day's revenue.
     *
     * @return the fee in lei
     * @throws IsmeretlenRendszamException if this plate is not inside
     */
    public double kihajt(String rendszam) {
        if (rendszam == null || rendszam.isBlank()) {
            throw new IsmeretlenRendszamException(String.valueOf(rendszam));
        }

        // remove() returns the removed value, or null if there was none -> lookup + delete in one step
        Parkolas p = parkolasok.remove(Jarmu.normalizal(rendszam));
        if (p == null) {
            throw new IsmeretlenRendszamException(rendszam);
        }
        helyek[p.szint()][p.hely()] = null;

        long most = ora.most();
        long percek = most - p.erkezes();

        double dij = p.jarmu().dij(percek); // <- THE polymorphic call. No instanceof, no switch.

        // merge: if the day has no entry yet, put dij; otherwise add dij to the old value
        bevetelNaponta.merge(most / Jarmu.PERC_NAPONTA, dij, Double::sum);
        return dij;
    }

    public boolean bentVan(String rendszam) {
        return rendszam != null && parkolasok.containsKey(Jarmu.normalizal(rendszam));
    }

    public int szintekSzama() {
        return helyek.length;
    }

    public int helyekSzintenkent() {
        return helyek[0].length;
    }

    /** Number of taken spots on a level (0-based level index). */
    public int foglalt(int szint) {
        if (szint < 0 || szint >= helyek.length) {
            throw new IllegalArgumentException("Nincs ilyen szint: " + szint);
        }
        int db = 0;
        for (Jarmu j : helyek[szint]) {
            if (j != null) {
                db++;
            }
        }
        return db;
    }

    public int osszesFoglalt() {
        return parkolasok.size();
    }

    /** Revenue of the current day (according to the injected clock). */
    public double napiBevetel() {
        return bevetelNaponta.getOrDefault(ora.most() / Jarmu.PERC_NAPONTA, 0.0);
    }

    // NOTE on encapsulation: there is no getHelyek() / getParkolasok() returning the
    // internal array or map. If we returned them, anyone could write
    // `haz.getHelyek()[0][0] = null;` and corrupt the garage. If you ever need a list,
    // return a copy: `return List.copyOf(parkolasok.values());`
}

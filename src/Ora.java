/**
 * "Where does the time come from?" - hidden behind an interface (Dependency Inversion).
 *
 * Parkolohaz only knows this interface, not System.currentTimeMillis().
 * In production we give it a RendszerOra, in tests a LeptethetoOra
 * that we can "fast forward" - so a test about 31 minutes doesn't take 31 minutes.
 */
public interface Ora {
    /** Current time in MINUTES, measured from some fixed starting point. */
    long most();
}

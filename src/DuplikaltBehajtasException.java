/** Thrown when a plate that is already inside tries to enter again. */
public class DuplikaltBehajtasException extends ParkolohazException {
    public DuplikaltBehajtasException(String rendszam) {
        super("Ez a rendszám már bent van: " + rendszam);
    }
}

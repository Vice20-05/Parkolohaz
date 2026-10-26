/** Thrown when someone wants to exit with a plate that is not inside. */
public class IsmeretlenRendszamException extends ParkolohazException {
    public IsmeretlenRendszamException(String rendszam) {
        super("Ismeretlen rendszám: " + rendszam);
    }
}

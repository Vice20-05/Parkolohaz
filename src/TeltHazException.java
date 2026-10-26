/** Thrown when there is no free spot on any level. */
public class TeltHazException extends ParkolohazException {
    public TeltHazException() {
        super("Telt ház - nincs szabad hely!");
    }
}

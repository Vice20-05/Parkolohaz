/**
 * Common parent of our own errors, so the menu can catch all of them with ONE catch.
 * RuntimeException = unchecked, callers aren't forced to write try/catch everywhere.
 */
public class ParkolohazException extends RuntimeException {
    public ParkolohazException(String uzenet) {
        super(uzenet);
    }
}

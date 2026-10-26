/** The real clock: minutes elapsed since 1970-01-01 (UTC). */
public class RendszerOra implements Ora {
    @Override
    public long most() {
        return System.currentTimeMillis() / 60_000;
    }
}

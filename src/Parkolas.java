/**
 * One parking "ticket": who, where, since when.
 *
 * A record is an immutable data class: Java generates the constructor,
 * the getters (jarmu(), szint(), ...), equals, hashCode and toString.
 * Because it's immutable, it's safe to hand out to callers - they can't break our state.
 *
 * szint and hely are 0-based inside the program; the menu shows them +1.
 */
public record Parkolas(Jarmu jarmu, int szint, int hely, long erkezes) {
}

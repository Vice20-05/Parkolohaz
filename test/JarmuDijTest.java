import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Fee boundary cases. Pattern of every test: Arrange (create) - Act (call) - Assert (check).
 * The 3rd argument of assertEquals for doubles is the allowed error (delta),
 * because double math is not always exact.
 */
class JarmuDijTest {

    private static final double DELTA = 0.0001;

    // ---- the 4 boundaries required by the task (Auto) ----

    @Test
    void auto_0perc_ingyenes() {
        assertEquals(0.0, new Auto("MS-01-ABC").dij(0), DELTA);
    }

    @Test
    void auto_30perc_meg_ingyenes() {
        assertEquals(0.0, new Auto("MS-01-ABC").dij(30), DELTA);
    }

    @Test
    void auto_31perc_egy_megkezdett_ora() {
        assertEquals(5.0, new Auto("MS-01-ABC").dij(31), DELTA);
    }

    @Test
    void auto_napi_plafon() {
        Auto a = new Auto("MS-01-ABC");
        assertEquals(40.0, a.dij(23 * 60), DELTA);           // would be 23*5=115 without the cap
        assertEquals(40.0, a.dij(Jarmu.PERC_NAPONTA), DELTA);  // exactly 1 day
        assertEquals(45.0, a.dij(Jarmu.PERC_NAPONTA + 31), DELTA); // 1 day + 1 started hour
    }

    // ---- other types: proves each subclass has its own pricing ----

    @Test
    void motor_olcsobb() {
        assertEquals(2.0, new Motor("MS-02-MOT").dij(31), DELTA);
        assertEquals(12.0, new Motor("MS-02-MOT").dij(20 * 60), DELTA);
    }

    @Test
    void kisbusz_csak_15_perc_ingyenes() {
        assertEquals(0.0, new Kisbusz("MS-03-BUS").dij(15), DELTA);
        assertEquals(8.0, new Kisbusz("MS-03-BUS").dij(16), DELTA);
    }

    @Test
    void elektromos_auto_fizet_toltest_is() {
        ElektromosAuto e = new ElektromosAuto("MS-04-EEE");
        assertEquals(0.0, e.dij(0), DELTA);
        assertEquals(3.0, e.dij(30), DELTA);   // parking free, but 1 started hour of charging
        assertEquals(8.0, e.dij(31), DELTA);   // 5 parking + 3 charging
    }

    // ---- validation ----

    @Test
    void negativ_perc_hiba() {
        assertThrows(IllegalArgumentException.class, () -> new Auto("MS-01-ABC").dij(-1));
    }

    @Test
    void ures_vagy_null_rendszam_hiba() {
        assertThrows(IllegalArgumentException.class, () -> new Auto(null));
        assertThrows(IllegalArgumentException.class, () -> new Auto(""));
        assertThrows(IllegalArgumentException.class, () -> new Auto("   "));
    }

    @Test
    void rendszam_normalizalva() {
        assertEquals("MS-01-ABC", new Auto("  ms-01-abc ").getRendszam());
    }
}

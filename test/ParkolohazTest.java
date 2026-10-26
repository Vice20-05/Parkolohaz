import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Garage behaviour. The LeptethetoOra lets us "wait" 90 minutes instantly.
 */
class ParkolohazTest {

    private LeptethetoOra ora;
    private Parkolohaz haz;

    // Runs before EVERY test -> every test starts with a fresh, empty garage
    @BeforeEach
    void setUp() {
        ora = new LeptethetoOra();
        haz = new Parkolohaz(2, 2, ora); // 2 levels x 2 spots = 4 places
    }

    @Test
    void telt_haz() {
        haz.behajt(new Auto("A-1"));
        haz.behajt(new Auto("A-2"));
        haz.behajt(new Motor("M-1"));
        haz.behajt(new Kisbusz("K-1"));

        assertThrows(TeltHazException.class, () -> haz.behajt(new Auto("A-5")));
        assertEquals(4, haz.osszesFoglalt()); // the failed attempt changed nothing
    }

    @Test
    void kihajtas_utan_ujra_van_hely() {
        haz.behajt(new Auto("A-1"));
        haz.behajt(new Auto("A-2"));
        haz.behajt(new Auto("A-3"));
        haz.behajt(new Auto("A-4"));
        haz.kihajt("A-2");

        Parkolas p = haz.behajt(new Auto("A-5"));
        assertEquals(0, p.szint()); // A-2's old spot on level 1 got reused
        assertEquals(1, p.hely());
    }

    @Test
    void ismeretlen_rendszam() {
        assertThrows(IsmeretlenRendszamException.class, () -> haz.kihajt("NINCS-ILYEN"));
        assertThrows(IsmeretlenRendszamException.class, () -> haz.kihajt(""));
    }

    @Test
    void duplikalt_behajtas() {
        haz.behajt(new Auto("MS-01-ABC"));
        // same plate, different case/spaces -> still a duplicate
        assertThrows(DuplikaltBehajtasException.class, () -> haz.behajt(new Motor(" ms-01-abc")));
    }

    @Test
    void dij_az_ora_alapjan_es_napi_bevetel() {
        haz.behajt(new Auto("A-1"));
        haz.behajt(new ElektromosAuto("E-1"));
        ora.eltol(90); // 1.5 hours later

        assertEquals(5.0, haz.kihajt("A-1"), 0.0001);   // 60 paid minutes -> 1 hour
        assertEquals(11.0, haz.kihajt("E-1"), 0.0001);  // 5 parking + 2 started hours * 3 charging
        assertEquals(16.0, haz.napiBevetel(), 0.0001);
        assertFalse(haz.bentVan("A-1"));
    }

    @Test
    void foglaltsag_szintenkent() {
        haz.behajt(new Auto("A-1"));
        haz.behajt(new Auto("A-2"));
        haz.behajt(new Auto("A-3"));

        assertEquals(2, haz.foglalt(0)); // level 1 is filled first
        assertEquals(1, haz.foglalt(1));
        assertTrue(haz.bentVan("a-3"));
        assertThrows(IllegalArgumentException.class, () -> haz.foglalt(5));
    }
}

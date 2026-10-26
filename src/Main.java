import java.util.Scanner;

/**
 * Console menu. Only talks to the user - all the logic lives in Parkolohaz (SRP).
 */
public class Main {

    public static void main(String[] args) {
        // Demo: a clock we can fast-forward from the menu (option 5), so fees can be tried out.
        // For "real" use, replace with: Ora ora = new RendszerOra();  (and drop option 5)
        LeptethetoOra ora = new LeptethetoOra();
        Parkolohaz haz = new Parkolohaz(3, 4, ora); // 3 levels, 4 spots each

        Scanner sc = new Scanner(System.in);
        boolean fut = true;

        while (fut) {
            System.out.println();
            System.out.println("=== Parkolóház (idő: " + ora.most() + ". perc) ===");
            System.out.println("1) Behajtás");
            System.out.println("2) Kihajtás");
            System.out.println("3) Foglaltság szintenként");
            System.out.println("4) Napi bevétel");
            System.out.println("5) Idő előretekerése (demo)");
            System.out.println("0) Kilépés");
            System.out.print("> ");

            if (!sc.hasNextLine()) {
                break; // input ended (e.g. Ctrl+D or piped file) -> exit cleanly
            }
            String valasz = sc.nextLine().trim();

            // One try/catch for the whole menu: every ParkolohazException and every
            // IllegalArgumentException (bad plate, bad number) ends up here as a message.
            try {
                switch (valasz) {
                    case "1" -> behajtas(sc, haz);
                    case "2" -> kihajtas(sc, haz);
                    case "3" -> foglaltsag(haz);
                    case "4" -> System.out.printf("Mai bevétel: %.2f lej%n", haz.napiBevetel());
                    case "5" -> {
                        System.out.print("Hány percet? ");
                        ora.eltol(Long.parseLong(sc.nextLine().trim()));
                    }
                    case "0" -> fut = false;
                    default -> System.out.println("Ismeretlen menüpont.");
                }
            } catch (ParkolohazException | IllegalArgumentException e) {
                // NumberFormatException is a subclass of IllegalArgumentException -> also caught
                System.out.println("HIBA: " + e.getMessage());
            }
        }
        System.out.println("Viszlát!");
    }

    private static void behajtas(Scanner sc, Parkolohaz haz) {
        System.out.print("Típus (1=Autó, 2=Motor, 3=Kisbusz, 4=Elektromos autó): ");
        String tipus = sc.nextLine().trim();
        System.out.print("Rendszám: ");
        String rendszam = sc.nextLine();

        // This switch is NOT against the rules: it only CREATES the right object from
        // user input (a tiny "factory"). The fee calculation in Parkolohaz stays switch-free.
        Jarmu j = switch (tipus) {
            case "1" -> new Auto(rendszam);
            case "2" -> new Motor(rendszam);
            case "3" -> new Kisbusz(rendszam);
            case "4" -> new ElektromosAuto(rendszam);
            default -> throw new IllegalArgumentException("Ismeretlen típus: " + tipus);
        };

        Parkolas p = haz.behajt(j);
        System.out.println("Behajtott: " + j + " -> " + (p.szint() + 1) + ". szint, "
                + (p.hely() + 1) + ". hely");
    }

    private static void kihajtas(Scanner sc, Parkolohaz haz) {
        System.out.print("Rendszám: ");
        String rendszam = sc.nextLine();
        double dij = haz.kihajt(rendszam);
        System.out.printf("Fizetendő: %.2f lej%n", dij);
    }

    private static void foglaltsag(Parkolohaz haz) {
        for (int i = 0; i < haz.szintekSzama(); i++) {
            System.out.println((i + 1) + ". szint: " + haz.foglalt(i) + " / " + haz.helyekSzintenkent());
        }
        System.out.println("Összesen foglalt: " + haz.osszesFoglalt());
    }
}

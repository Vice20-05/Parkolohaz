# Parkolóház-szimulátor

Marosvásárhelyi háromszintes parkolóház szoftvere – gyakorlófeladat az absztrakt osztályokra,
a polimorfizmusra és a nyitott/zárt elvre (OCP).

## Felépítés

```
src/
  Jarmu.java                 absztrakt ősosztály (rendszám + abstract dij)
  Auto, Motor, Kisbusz       konkrét járművek, saját díjszabással
  ElektromosAuto             Auto + töltési díj (super.dij újrahasznosítva)
  Ora, RendszerOra,          időforrás interfész + valós / "pörgethető" óra
  LeptethetoOra
  Parkolas                   record: jármű, szint, hely, érkezés
  Parkolohaz                 behajt / kihajt / foglaltság / napi bevétel
  *Exception                 telt ház, ismeretlen rendszám, duplikált behajtás
  Main                       konzolos menü
test/                        JUnit 5 tesztek
lib/                         junit-platform-console-standalone-1.11.4.jar
docs/uml.puml                UML osztálydiagram (PlantUML)
```

## Díjszabás

| Típus          | Ingyenes | Óradíj (megkezdett óra) | Napi plafon | Extra                 |
|----------------|----------|-------------------------|-------------|-----------------------|
| Auto           | 30 perc  | 5 lej                   | 40 lej      | –                     |
| Motor          | 30 perc  | 2 lej                   | 12 lej      | –                     |
| Kisbusz        | 15 perc  | 8 lej                   | 60 lej      | –                     |
| ElektromosAuto | 30 perc  | 5 lej                   | 40 lej      | + 3 lej / óra töltés  |

Minden teljes nap (1440 perc) a napi plafont kerül; a maradék percekre újra érvényes az ingyenes idő és a plafon.

## Fordítás és futtatás

Követelmény: JDK 21+ (a `Math.ceilDiv` és a `record` miatt). Maven/Gradle nem kell.

```bash
# fordítás
javac -d out/main src/*.java

# futtatás
java -cp out/main Main

# tesztek
javac -d out/test -cp out/main:lib/junit-platform-console-standalone-1.11.4.jar test/*.java
java -jar lib/junit-platform-console-standalone-1.11.4.jar execute -cp out/main:out/test --scan-classpath
```

(Windows-on a `:` helyett `;` az elválasztó a classpath-ban.)

## Példa konzolkimenet

A menü egy `LeptethetoOra`-t használ, így az 5-ös menüponttal előre lehet tekerni az időt.

```
=== Parkolóház (idő: 0. perc) ===
1) Behajtás
2) Kihajtás
3) Foglaltság szintenként
4) Napi bevétel
5) Idő előretekerése (demo)
0) Kilépés
> 1
Típus (1=Autó, 2=Motor, 3=Kisbusz, 4=Elektromos autó): 1
Rendszám: ms-01-abc
Behajtott: Auto [MS-01-ABC] -> 1. szint, 1. hely

> 1
Típus (1=Autó, 2=Motor, 3=Kisbusz, 4=Elektromos autó): 2
Rendszám: MS-01-ABC
HIBA: Ez a rendszám már bent van: MS-01-ABC

> 5
Hány percet? 95

=== Parkolóház (idő: 95. perc) ===
> 2
Rendszám: MS-01-ABC
Fizetendő: 10.00 lej

> 2
Rendszám: XX-00-XXX
HIBA: Ismeretlen rendszám: XX-00-XXX

> 3
1. szint: 1 / 4
2. szint: 0 / 4
3. szint: 0 / 4
Összesen foglalt: 1

> 4
Mai bevétel: 10.00 lej

> 0
Viszlát!
```

## Tesztek

16 JUnit 5 teszt, köztük a kötelező határesetek:

- `JarmuDijTest`: 0 perc, 30 perc, 31 perc, napi plafon (és több nap), Motor/Kisbusz/ElektromosAuto díjak,
  negatív perc, null/üres rendszám, rendszám-normalizálás
- `ParkolohazTest`: telt ház, hely újrahasznosítása, ismeretlen rendszám, duplikált behajtás,
  díj a pörgetett óra alapján + napi bevétel, foglaltság szintenként

## Új járműtípus hozzáadása (OCP)

```java
public class Lakoauto extends Jarmu {
    public Lakoauto(String rendszam) { super(rendszam); }

    @Override
    public double dij(long percek) {
        return idoalapuDij(percek, 15, 12.0, 90.0);
    }
}
```

A `Parkolohaz` osztályhoz nem kell hozzányúlni – csak a menü kap egy új opciót a létrehozáshoz.

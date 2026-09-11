# INTERLIS ili2db Plugin fuer Apache Hop installieren

## Voraussetzungen

- Apache Hop 2.19.0 installiert
- Java 21 oder neuer

Die Plugins werden als zwei getrennte ZIP-Artefakte veröffentlicht:

- `ch.so.agi:hop-action-ili2db:0.1.0-SNAPSHOT`
- `ch.so.agi:hop-transform-ili2db:0.1.0-SNAPSHOT`

Für Snapshots genügt die normale Maven-Koordinate `0.1.0-SNAPSHOT`. Maven
löst den aktuellen Snapshot über `https://jars.interlis.guru/snapshots/` auf;
Timestamp-Versionen müssen nicht eingetragen werden.

## 1. ZIP-Dateien bauen

```bash
mvn -pl assemblies/assemblies-action-ili2db,assemblies/assemblies-transform-ili2db -am package
```

## 2. ZIP-Dateien ins Hop-Verzeichnis entpacken

```bash
unzip -o ./assemblies/assemblies-action-ili2db/target/hop-action-ili2db-0.1.0-SNAPSHOT.zip -d "$HOP_HOME"
unzip -o ./assemblies/assemblies-transform-ili2db/target/hop-transform-ili2db-0.1.0-SNAPSHOT.zip -d "$HOP_HOME"
```

Ergebnis:

- `plugins/actions/ili2db/`
- `plugins/transforms/ili2db/`

## 3. Hop neu starten

Hop komplett beenden und erneut starten.

## 4. Pruefen

- Workflow: Aktion `INTERLIS ili2db`
- Pipeline: Transform `INTERLIS ili2db`

## CI-Prüfung

Die CI testet Ubuntu, macOS und Windows mit Java 21 und 25. Der kanonische
Ubuntu-/Java-21-Lauf führt `mvn -U -B -ntp clean verify`, die Paketprüfung und
den Installed-Hop-E2E aus. Die übrigen Matrixläufe führen `clean test` aus.

Der E2E-Test verwendet eine frische Hop-2.19.0-Installation und installiert
genau die geprüften ZIPs. Er importiert die deterministische Fixture
`e2e/fixtures/valid.xtf` über den Transform und die Action in getrennte
GeoPackages und prüft die erzeugten Daten. Er verwendet keine Maven-Testklassen
ausserhalb der installierten Plugin-ZIPs.

Pull Requests veröffentlichen nichts. Pushes auf `main` veröffentlichen nach
erfolgreicher kompletter Matrix exakt die geprüften ZIPs in das Snapshot-
Repository. Dafür sind die geschützten Secrets `INTERLIS_MAVEN_USERNAME` und
`INTERLIS_MAVEN_TOKEN` erforderlich. GitHub-Releases werden für die Plugins
nicht mehr erzeugt.

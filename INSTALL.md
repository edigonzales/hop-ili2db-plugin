# INTERLIS ili2db Plugin fuer Apache Hop installieren

## Voraussetzungen

- Apache Hop installiert
- Java 17+

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

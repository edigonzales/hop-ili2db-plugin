# hop-ili2db-plugin

Apache Hop 2.17 plugin suite for INTERLIS `ili2db` (Transform + Action).

## Implemented scope

- Flavors:
  - `ili2gpkg`
  - `ili2pg`
- ili2db version:
  - `ch.interlis:*:5.5.1`
- Plugins:
  - Pipeline transform `INTERLIS ili2db`
  - Workflow action `INTERLIS ili2db`
- UI:
  - Tabs `Main`, `Options`, `Dataset`
  - Flavor combobox switches between GPKG target file and PostgreSQL connection target
  - Dataset can be static or sourced from a stream field (transform)

## Modules

- `./ili2db-core`
  - Hop-independent request/executor layer with flavor adapters and option mapping.
- `./hop-action-ili2db`
  - Workflow action plugin.
- `./hop-transform-ili2db`
  - Pipeline transform plugin with pass-through status fields.
- `./assemblies/assemblies-action-ili2db`
  - Install ZIP under `plugins/actions/ili2db`.
- `./assemblies/assemblies-transform-ili2db`
  - Install ZIP under `plugins/transforms/ili2db`.
- `./assemblies/debug`
  - Debug Hop layout.

## Build

Full build:

```bash
mvn clean verify
```

Fast plugin build (skip tests):

```bash
mvn -pl ili2db-core,hop-action-ili2db,hop-transform-ili2db -am -DskipTests package
```

## Repository order

Root `pom.xml` uses:

1. Maven Central
2. `https://jars.interlis.ch/`

`jars.sogeo.services/mirror` is not configured.

## Install in Hop

Build install ZIPs:

```bash
mvn -pl assemblies/assemblies-action-ili2db,assemblies/assemblies-transform-ili2db -am package
```

Unzip into Hop home:

```bash
unzip -o ./assemblies/assemblies-action-ili2db/target/hop-action-ili2db-0.1.0-SNAPSHOT.zip -d "$HOP_HOME"
unzip -o ./assemblies/assemblies-transform-ili2db/target/hop-transform-ili2db-0.1.0-SNAPSHOT.zip -d "$HOP_HOME"
```

Plugin folders:

- `$HOP_HOME/plugins/actions/ili2db`
- `$HOP_HOME/plugins/transforms/ili2db`

## Debug workflow

Create debug layout:

```bash
mvn -pl assemblies/debug -am -DskipTests package
```

Sync local jars into debug Hop:

```bash
./scripts/dev-sync-debug.sh
```

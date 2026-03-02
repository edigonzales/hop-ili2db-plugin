#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
VERSION="$(sed -n 's|.*<version>\(.*\)</version>.*|\1|p' "${ROOT_DIR}/pom.xml" | head -n 1)"
HOP_DEBUG_DIR="${1:-${HOP_DEBUG_DIR:-${ROOT_DIR}/assemblies/debug/target/hop}}"

if [[ ! -d "${HOP_DEBUG_DIR}" ]]; then
  echo "Debug Hop layout not found at: ${HOP_DEBUG_DIR}"
  echo "Create it once with:"
  echo "  mvn -pl assemblies/debug -am -DskipTests package"
  exit 1
fi

echo "Building core + plugins (skip tests)..."
mvn -pl ili2db-core,hop-action-ili2db,hop-transform-ili2db -am -DskipTests package

ACTION_PLUGIN_DIR="${HOP_DEBUG_DIR}/plugins/actions/ili2db"
TRANSFORM_PLUGIN_DIR="${HOP_DEBUG_DIR}/plugins/transforms/ili2db"

mkdir -p "${ACTION_PLUGIN_DIR}" "${TRANSFORM_PLUGIN_DIR}"

rm -f "${ACTION_PLUGIN_DIR}/hop-action-ili2db-"*.jar
rm -f "${TRANSFORM_PLUGIN_DIR}/hop-transform-ili2db-"*.jar
rm -f "${ACTION_PLUGIN_DIR}/ili2db-core-"*.jar
rm -f "${TRANSFORM_PLUGIN_DIR}/ili2db-core-"*.jar
rm -f "${ACTION_PLUGIN_DIR}/ili2db-"*.jar
rm -f "${TRANSFORM_PLUGIN_DIR}/ili2db-"*.jar
rm -f "${ACTION_PLUGIN_DIR}/ili2pg-"*.jar
rm -f "${TRANSFORM_PLUGIN_DIR}/ili2pg-"*.jar
rm -f "${ACTION_PLUGIN_DIR}/ili2gpkg-"*.jar
rm -f "${TRANSFORM_PLUGIN_DIR}/ili2gpkg-"*.jar
rm -f "${ACTION_PLUGIN_DIR}/antlr-"*.jar
rm -f "${TRANSFORM_PLUGIN_DIR}/antlr-"*.jar

cp -f "${ROOT_DIR}/hop-action-ili2db/target/hop-action-ili2db-${VERSION}.jar" "${ACTION_PLUGIN_DIR}/"
cp -f "${ROOT_DIR}/hop-transform-ili2db/target/hop-transform-ili2db-${VERSION}.jar" "${TRANSFORM_PLUGIN_DIR}/"

echo "Synchronized plugin jars into: ${HOP_DEBUG_DIR}"
echo "Restart Hop GUI to pick up class changes."

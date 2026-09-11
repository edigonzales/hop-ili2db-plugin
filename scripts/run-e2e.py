#!/usr/bin/env python3
"""Run both installed ili2db plugins in a clean Apache Hop installation."""

from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import sqlite3
import subprocess
import urllib.request
import zipfile
from xml.sax.saxutils import escape
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
HOP_VERSION = "2.19.0"
POM_NAMESPACE = "{http://maven.apache.org/POM/4.0.0}"


def project_version() -> str:
    root = ET.parse(ROOT / "pom.xml").getroot()
    version = root.findtext(f"{POM_NAMESPACE}version") or root.findtext("version")
    if not version:
        raise SystemExit("Could not resolve project.version from pom.xml")
    return version


def download(url: str, destination: Path) -> None:
    temporary = destination.with_suffix(destination.suffix + ".part")
    with urllib.request.urlopen(url, timeout=180) as response, temporary.open("wb") as output:
        shutil.copyfileobj(response, output)
    temporary.replace(destination)


def hop_archive(cache: Path) -> Path:
    cache.mkdir(parents=True, exist_ok=True)
    name = f"apache-hop-client-{HOP_VERSION}.zip"
    archive = cache / name
    checksum_file = cache / f"{name}.sha512"
    if not checksum_file.exists():
        download(f"https://downloads.apache.org/hop/{HOP_VERSION}/{name}.sha512", checksum_file)
    if not archive.exists():
        download(f"https://mirror.init7.net/apache/hop/{HOP_VERSION}/{name}", archive)
    values = checksum_file.read_text(encoding="utf-8").split()
    expected = next((value.lower() for value in values if len(value) == 128), None)
    if expected is None:
        raise SystemExit(f"No SHA-512 value found in {checksum_file}")
    actual = hashlib.sha512(archive.read_bytes()).hexdigest()
    if actual != expected:
        raise SystemExit(f"Hop ZIP checksum mismatch: {actual} != {expected}")
    return archive


def transform(name: str, transform_type: str, body: str, x: int, y: int = 160) -> str:
    return (
        f"<transform><name>{escape(name)}</name><type>{transform_type}</type>"
        f"<description/><distribute>Y</distribute><custom_distribution/><copies>1</copies>"
        f"<partitioning><method>none</method><schema_name/></partitioning>{body}"
        f"<attributes/><GUI><xloc>{x}</xloc><yloc>{y}</yloc></GUI></transform>"
    )


def get_file_names(fixtures: Path) -> str:
    body = f"""
      <doNotFailIfNoFile>N</doNotFailIfNoFile>
      <dynamic_include_subfolders>N</dynamic_include_subfolders>
      <file>
        <file_required>Y</file_required>
        <filemask>valid\\.xtf</filemask>
        <include_subfolders>N</include_subfolders>
        <name>{escape(str(fixtures))}</name>
      </file>
      <filefield>N</filefield>
      <filter><filterfiletype>all_files</filterfiletype></filter>
      <isaddresult>Y</isaddresult>
      <limit>0</limit>
      <raiseAnExceptionIfNoFile>Y</raiseAnExceptionIfNoFile>
      <rownum>N</rownum>
    """
    return transform("INTERLIS input", "GetFileNames", body, 100)


def ili2db_transform(fixtures: Path, target: Path) -> str:
    body = f"""
      <connectionName/>
      <createLogFile>N</createLogFile>
      <datasetField/>
      <datasetMode>STATIC</datasetMode>
      <datasetName/>
      <defaultSrsCode>2056</defaultSrsCode>
      <disableValidation>N</disableValidation>
      <failOnError>Y</failOnError>
      <flavor>ILI2GPKG</flavor>
      <function>IMPORT</function>
      <gpkgFileField/>
      <gpkgFilePath>{escape(str(target))}</gpkgFilePath>
      <gpkgTargetMode>STATIC_PATH</gpkgTargetMode>
      <implicitSchemaImport>Y</implicitSchemaImport>
      <importFileField>filename</importFileField>
      <importFilePath/>
      <importSourceMode>FIELD</importSourceMode>
      <logDirectory/>
      <modelDir>{escape(str(fixtures))}</modelDir>
      <modelName>TransferInputTest</modelName>
      <nameByTopic>Y</nameByTopic>
      <outputConnectionField>ili2db_connection</outputConnectionField>
      <outputDatabaseSchemaField>ili2db_database_schema</outputDatabaseSchemaField>
      <outputDatasetField>ili2db_dataset_effective</outputDatasetField>
      <outputFlavorField>ili2db_flavor</outputFlavorField>
      <outputFunctionField>ili2db_function</outputFunctionField>
      <outputLogFilePathField>log_file_path</outputLogFilePathField>
      <outputMessageField>ili2db_message</outputMessageField>
      <outputSuccessField>ili2db_success</outputSuccessField>
      <outputTargetFileField>ili2db_target_file</outputTargetFileField>
      <outputTargetIdField>ili2db_target_id</outputTargetIdField>
      <outputTargetJdbcUrlField>ili2db_target_jdbc_url</outputTargetJdbcUrlField>
      <outputTargetTypeField>ili2db_target_type</outputTargetTypeField>
      <schemaName/>
      <serializedOptions/>
      <strokeArcs>Y</strokeArcs>
    """
    return transform("INTERLIS ili2db transform", "INTERLIS_ILI2DB_TRANSFORM", body, 380)


def text_output(output_file: Path) -> str:
    body = f"""
      <schema_definition/>
      <ignore_fields>N</ignore_fields>
      <separator>;</separator>
      <enclosure>\"</enclosure>
      <enclosure_forced>N</enclosure_forced>
      <enclosure_fix_disabled>N</enclosure_fix_disabled>
      <header>Y</header>
      <footer>N</footer>
      <format>UNIX</format>
      <compression>None</compression>
      <encoding>UTF-8</encoding>
      <endedLine/>
      <fileNameInField>N</fileNameInField>
      <fileNameField/>
      <create_parent_folder>Y</create_parent_folder>
      <file>
        <name>{escape(str(output_file))}</name>
        <servlet_output>N</servlet_output>
        <do_not_open_new_file_init>Y</do_not_open_new_file_init>
        <extention>csv</extention>
        <append>N</append>
        <split>N</split>
        <haspartno>N</haspartno>
        <add_date>N</add_date>
        <add_time>N</add_time>
        <SpecifyFormat>N</SpecifyFormat>
        <date_time_format/>
        <add_to_result_filenames>Y</add_to_result_filenames>
        <pad>N</pad>
        <fast_dump>N</fast_dump>
        <splitevery/>
      </file>
      <fields/>
    """
    return transform("Results", "TextFileOutput", body, 660)


def create_pipeline(work: Path, fixtures: Path, target: Path) -> Path:
    output_file = work / "transform-results"
    hops = """
      <hop><from>INTERLIS input</from><to>INTERLIS ili2db transform</to><enabled>Y</enabled></hop>
      <hop><from>INTERLIS ili2db transform</from><to>Results</to><enabled>Y</enabled></hop>
    """
    xml = f'''<?xml version="1.0" encoding="UTF-8"?>
<pipeline>
  <info>
    <name>ili2db-installed-transform-e2e</name>
    <name_sync_with_filename>Y</name_sync_with_filename>
    <description/>
    <extended_description/>
    <pipeline_version/>
    <pipeline_type>Normal</pipeline_type>
    <parameters></parameters>
    <capture_transform_performance>N</capture_transform_performance>
    <transform_performance_capturing_delay>1000</transform_performance_capturing_delay>
    <transform_performance_capturing_size_limit>100</transform_performance_capturing_size_limit>
    <created_user>e2e</created_user>
    <created_date>2026/09/11 00:00:00.000</created_date>
    <modified_user>e2e</modified_user>
    <modified_date>2026/09/11 00:00:00.000</modified_date>
  </info>
  <notepads></notepads>
  <order>{hops}</order>
  {get_file_names(fixtures)}
  {ili2db_transform(fixtures, target)}
  {text_output(output_file)}
  <transform_error_handling></transform_error_handling>
  <attributes/>
</pipeline>
'''
    path = work / "ili2db-installed-transform-e2e.hpl"
    path.write_text(xml, encoding="utf-8")
    return path


def action_xml(fixtures: Path, target: Path) -> Path:
    xml = f'''<?xml version="1.0" encoding="UTF-8"?>
<workflow>
  <name>ili2db-installed-action-e2e</name>
  <name_sync_with_filename>Y</name_sync_with_filename>
  <description/>
  <extended_description/>
  <workflow_version/>
  <created_user>e2e</created_user>
  <created_date>2026/09/11 00:00:00.000</created_date>
  <modified_user>e2e</modified_user>
  <modified_date>2026/09/11 00:00:00.000</modified_date>
  <parameters></parameters>
  <actions>
    <action>
      <name>Start</name>
      <description/>
      <type>SPECIAL</type>
      <attributes/>
      <DayOfMonth>1</DayOfMonth><doNotWaitOnFirstExecution>N</doNotWaitOnFirstExecution>
      <hour>12</hour><intervalMinutes>60</intervalMinutes><intervalSeconds>0</intervalSeconds>
      <minutes>0</minutes><repeat>N</repeat><schedulerType>0</schedulerType><weekDay>1</weekDay>
      <parallel>N</parallel><xloc>144</xloc><yloc>96</yloc><attributes_hac/>
    </action>
    <action>
      <name>INTERLIS ili2db action</name>
      <description/>
      <type>INTERLIS_ILI2DB_ACTION</type>
      <attributes/>
      <connectionName/>
      <datasetName/>
      <defaultSrsCode>2056</defaultSrsCode>
      <disableValidation>N</disableValidation>
      <flavor>ILI2GPKG</flavor>
      <function>IMPORT</function>
      <gpkgFilePath>{escape(str(target))}</gpkgFilePath>
      <implicitSchemaImport>Y</implicitSchemaImport>
      <importFilePath>{escape(str(fixtures / 'valid.xtf'))}</importFilePath>
      <modelDir>{escape(str(fixtures))}</modelDir>
      <modelName>TransferInputTest</modelName>
      <nameByTopic>Y</nameByTopic>
      <schemaName/>
      <serializedOptions/>
      <strokeArcs>Y</strokeArcs>
      <parallel>N</parallel><xloc>272</xloc><yloc>176</yloc><attributes_hac/>
    </action>
  </actions>
  <hops>
    <hop><from>Start</from><to>INTERLIS ili2db action</to><enabled>Y</enabled><evaluation>Y</evaluation><unconditional>Y</unconditional></hop>
  </hops>
  <notepads></notepads>
  <attributes/>
</workflow>
'''
    path = target.parent / "ili2db-installed-action-e2e.hwf"
    path.write_text(xml, encoding="utf-8")
    return path


def run_hop(hop: Path, definition: Path, env: dict[str, str], log: Path) -> subprocess.CompletedProcess[str]:
    command = ["bash", str(hop / "hop-run.sh"), "-f", str(definition), "-r", "local"]
    process = subprocess.run(
        command,
        cwd=hop,
        env=env,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        timeout=300,
    )
    log.write_text(process.stdout, encoding="utf-8")
    return process


def verify_gpkg(path: Path) -> dict[str, object]:
    if not path.is_file():
        raise AssertionError(f"GeoPackage was not created: {path}")
    with sqlite3.connect(path) as connection:
        tables = [
            row[0]
            for row in connection.execute(
                "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'"
            )
        ]
        user_tables = [name for name in tables if not name.startswith("t_ili2db_")]
        if not user_tables:
            raise AssertionError(f"No user tables in {path}; found {tables}")
        rows = 0
        for table in user_tables:
            quoted = '"' + table.replace('"', '""') + '"'
            rows += connection.execute(f"SELECT COUNT(*) FROM {quoted}").fetchone()[0]
        if rows < 1:
            raise AssertionError(f"User tables are empty in {path}")
        # The deterministic fixture is required to reach the database, not merely to create it.
        found_example = False
        for table in user_tables:
            quoted = '"' + table.replace('"', '""') + '"'
            columns = connection.execute(f"PRAGMA table_info({quoted})").fetchall()
            for column in columns:
                column_name = column[1]
                column_type = (column[2] or "").upper()
                if column_type.startswith(("TEXT", "CHAR", "VARCHAR")):
                    quoted_column = '"' + column_name.replace('"', '""') + '"'
                    if connection.execute(
                        f"SELECT 1 FROM {quoted} WHERE {quoted_column} = ? LIMIT 1", ("Example",)
                    ).fetchone():
                        found_example = True
        if not found_example:
            raise AssertionError(f"Fixture value Example not found in {path}")
    return {"path": str(path), "tables": user_tables, "rows": rows}


def main() -> int:
    parser = argparse.ArgumentParser()
    version = project_version()
    parser.add_argument(
        "--action",
        type=Path,
        default=ROOT / f"assemblies/assemblies-action-ili2db/target/hop-action-ili2db-{version}.zip",
    )
    parser.add_argument(
        "--transform",
        type=Path,
        default=ROOT / f"assemblies/assemblies-transform-ili2db/target/hop-transform-ili2db-{version}.zip",
    )
    parser.add_argument("--cache", type=Path, default=Path.home() / ".cache/hop-ili2db")
    args = parser.parse_args()

    for package in (args.action, args.transform):
        if not package.is_file():
            raise SystemExit(f"Missing verified plugin ZIP: {package}")
    archive = hop_archive(args.cache)
    work = ROOT / "target/e2e"
    shutil.rmtree(work, ignore_errors=True)
    work.mkdir(parents=True)
    with zipfile.ZipFile(archive) as distribution:
        distribution.extractall(work)
    hop = work / "hop"
    if not hop.is_dir():
        raise SystemExit(f"Unexpected Hop archive layout; missing {hop}")
    for package in (args.action, args.transform):
        with zipfile.ZipFile(package) as plugin:
            plugin.extractall(hop)
    for script in hop.glob("*.sh"):
        script.chmod(0o755)

    fixtures = work / "fixtures"
    shutil.copytree(ROOT / "e2e/fixtures", fixtures)
    config = work / "config"
    audit = work / "audit"
    metadata_root = config / "metadata"
    pipeline_metadata = metadata_root / "pipeline-run-configuration"
    workflow_metadata = metadata_root / "workflow-run-configuration"
    pipeline_metadata.mkdir(parents=True)
    workflow_metadata.mkdir(parents=True)
    audit.mkdir()
    local_configuration = json.dumps(
        {"name": "local", "engineRunConfiguration": {"Local": {"safe_mode": True}}}
    )
    (pipeline_metadata / "local.json").write_text(local_configuration, encoding="utf-8")
    (workflow_metadata / "local.json").write_text(local_configuration, encoding="utf-8")
    environment = os.environ.copy()
    environment.update(
        {
            "HOP_CONFIG_FOLDER": str(config),
            "HOP_AUDIT_FOLDER": str(audit),
            "HOP_METADATA_FOLDER": str(metadata_root),
        }
    )

    transform_target = work / "transform.gpkg"
    action_target = work / "action.gpkg"
    pipeline = create_pipeline(work, fixtures, transform_target)
    action = action_xml(fixtures, action_target)
    transform_process = run_hop(hop, pipeline, environment, work / "transform.log")
    if transform_process.returncode != 0:
        raise AssertionError(transform_process.stdout[-12000:])
    results_file = work / "transform-results.csv"
    if not results_file.is_file():
        raise AssertionError("Transform did not write its result file")
    with results_file.open(newline="", encoding="utf-8") as handle:
        content = handle.read()
    if "ili2db_success" not in content or not any(value in content.lower() for value in ("true", "y")):
        raise AssertionError(f"Transform success field was not emitted as successful: {content}")
    transform_report = verify_gpkg(transform_target)

    action_process = run_hop(hop, action, environment, work / "action.log")
    if action_process.returncode != 0:
        raise AssertionError(action_process.stdout[-12000:])
    action_report = verify_gpkg(action_target)
    report = {
        "hopVersion": HOP_VERSION,
        "transform": transform_report,
        "action": action_report,
        "usesInstalledZipOnly": True,
    }
    (work / "results.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

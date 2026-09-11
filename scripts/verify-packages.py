#!/usr/bin/env python3
"""Verify the two installable ili2db ZIPs and their plugin JARs."""

from __future__ import annotations

import argparse
import hashlib
import io
import json
from pathlib import Path
import zipfile
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
POM_NAMESPACE = "{http://maven.apache.org/POM/4.0.0}"
PLUGIN_ROOTS = {
    "action": "plugins/actions/ili2db",
    "transform": "plugins/transforms/ili2db",
}
PLUGIN_CLASSES = {
    "action": "ch/so/agi/ili2db/hop/action/ActionIli2db.class",
    "transform": "ch/so/agi/ili2db/hop/transform/Ili2db.class",
}
ICON_PATHS = {
    "action": "ch/so/agi/ili2db/hop/action/icons/ili2db.svg",
    "transform": "ch/so/agi/ili2db/hop/transform/icons/ili2db.svg",
}
ICON_SOURCES = {
    "action": ROOT / "hop-action-ili2db/src/main/resources/ch/so/agi/ili2db/hop/action/icons/ili2db.svg",
    "transform": ROOT / "hop-transform-ili2db/src/main/resources/ch/so/agi/ili2db/hop/transform/icons/ili2db.svg",
}


def project_version() -> str:
    root = ET.parse(ROOT / "pom.xml").getroot()
    version = root.findtext(f"{POM_NAMESPACE}version") or root.findtext("version")
    if not version:
        raise SystemExit("Could not resolve project.version from pom.xml")
    return version


def sha256_bytes(value: bytes) -> str:
    return hashlib.sha256(value).hexdigest()


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def assert_safe_path(name: str) -> None:
    path = Path(name)
    if path.is_absolute() or ".." in path.parts:
        raise AssertionError(f"Unsafe ZIP path: {name}")


def verify_jar(kind: str, name: str, content: bytes) -> dict[str, str]:
    with zipfile.ZipFile(io.BytesIO(content)) as jar:
        entries = jar.namelist()
        classes = [entry for entry in entries if entry.endswith(".class")]
        assert PLUGIN_CLASSES[kind] in entries, f"{name}: missing plugin main class"
        assert "META-INF/jandex.idx" in entries, f"{name}: missing Jandex index"
        assert ICON_PATHS[kind] in entries, f"{name}: missing {ICON_PATHS[kind]}"
        assert not any(
            entry.startswith(("org/apache/hop/", "org/eclipse/swt/")) for entry in classes
        ), f"{name}: embeds Hop or SWT classes"
        assert not any(entry.endswith("/icons/xml-validator.svg") for entry in entries), name
        assert jar.read(ICON_PATHS[kind]) == ICON_SOURCES[kind].read_bytes(), (
            f"{name}: embedded icon differs from the source icon"
        )
    return {"file": name, "sha256": sha256_bytes(content)}


def verify_zip(path: Path, kind: str, version: str) -> dict[str, object]:
    expected_name = f"hop-{kind}-ili2db-{version}.zip"
    assert path.is_file(), f"Missing ZIP: {path}"
    assert path.name == expected_name, (path.name, expected_name)
    expected_root = PLUGIN_ROOTS[kind]
    expected_jar = f"{expected_root}/hop-{kind}-ili2db-{version}.jar"

    with zipfile.ZipFile(path) as archive:
        assert archive.testzip() is None, f"Corrupt ZIP: {path}"
        names = archive.namelist()
        for name in names:
            assert_safe_path(name)
        assert not any(name.endswith("/icons/xml-validator.svg") for name in names), path

        jars = [name for name in names if name.endswith(".jar")]
        assert len(jars) == 1, f"{path}: expected exactly one plugin JAR, found {jars}"
        assert jars == [expected_jar], (path, jars, expected_jar)
        assert any(name.startswith(expected_root + "/") for name in names), expected_root
        jar_report = verify_jar(kind, jars[0], archive.read(jars[0]))

    return {
        "zipFile": str(path),
        "zipName": path.name,
        "sha256": sha256_file(path),
        "pluginRoot": expected_root,
        "jars": [jar_report],
    }


def main() -> int:
    version = project_version()
    parser = argparse.ArgumentParser()
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
    args = parser.parse_args()

    source_icons = {kind: path.read_bytes() for kind, path in ICON_SOURCES.items()}
    assert source_icons["action"] == source_icons["transform"], "Action and transform icons differ"
    report = {
        "version": version,
        "packages": {
            "action": verify_zip(args.action, "action", version),
            "transform": verify_zip(args.transform, "transform", version),
        },
    }
    output = ROOT / "target/package-verification.json"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

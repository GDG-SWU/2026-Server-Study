"""Package the bootJar for Elastic Beanstalk Java SE using only Python's stdlib."""

from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile


def main():
    root = Path(__file__).resolve().parent.parent
    jars = sorted(
        jar for jar in (root / "build" / "libs").glob("*.jar")
        if not jar.name.endswith("-plain.jar")
    )
    if len(jars) != 1:
        raise SystemExit(
            "Expected one bootJar in build/libs. Run clean test bootJar first. "
            f"Found {len(jars)} executable JAR candidates."
        )

    bundle = root / "build" / "deploy.zip"
    with ZipFile(bundle, "w", compression=ZIP_DEFLATED) as archive:
        archive.write(jars[0], "app.jar")
        archive.writestr(
            "Procfile",
            "web: java -jar app.jar --spring.profiles.active=prod\n",
        )
    print(f"Created {bundle.name}: app.jar + Procfile")


if __name__ == "__main__":
    main()

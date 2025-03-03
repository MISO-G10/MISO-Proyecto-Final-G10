import os
import subprocess
from pathlib import Path
from rich import print

import typer

project = typer.Typer()
app = typer.Typer()

app.add_typer(project, name="project")

BASE_DIR = Path(__file__).resolve().parent
APPS_DIR = BASE_DIR / "apps"
STUBS_DIR = BASE_DIR / "stubs"


@project.command()
def create(name: str = typer.Argument(..., help="The name of the project")):
    target_dir = APPS_DIR / name

    if target_dir.exists():
        print(f"[red]Project {name} already exists in {target_dir}[/red] :cry:")
        raise typer.Exit(code=1)

    print(f"[green]Creating project {name} in {target_dir}[/green] :thumbs_up:")

    try:
        print(f"[yellow]Creating project directory {target_dir}[/yellow] :package:")
        os.makedirs(target_dir)

        subprocess.run(
            [
                "uv",
                "run",
                "--",
                "cookiecutter",
                str(STUBS_DIR),
                f"--output-dir={APPS_DIR}",
                f"project_name={name}",
                "--overwrite-if-exists"
            ],
            check=True,
        )

        print(f"[green]Project {name} created in {target_dir}[/green] :tada:")

        print(f"[yellow]Setting up virtual environment for {name}[/yellow] :snake:")
        os.chdir(target_dir)

        subprocess.run(["uv", "sync"], check=True)

        print(f"[green]Virtual environment for {name} setup[/green] :snake:")
        print(f"[green]- cd apps/{name}[/green] :point_left: to start working on the project")

    except subprocess.CalledProcessError as e:
        print(f"[red]Failed to create project: {e}[/red] :cry:")
        raise typer.Exit(code=1)


if __name__ == "__main__":
    app()

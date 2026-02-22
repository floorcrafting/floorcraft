<div align="center">
    <h1>Floorcraft Advanced Modding</h1>
    <p><a href="https://boyninja1555.github.io/floorcraft/download.html">Download</a> | <a href="https://dsc.gg/floorcraft">Discord</a></p>
</div>

If you're going to mod the engine itself, there are a few steps to help you! These commands work in most Linux terminals
and will let you download and test the engine:

```shell
git clone https://github.com/boyninja1555/floorcraft.git  # Copies this repo into a directory on your machine.
./decompile-assets  # Extracts the `assets.zip` file into a directory at `assets/` you can easily modify. After editing, copy your changes to the directory opened by pressing X in-game. Otherwise the engine will download the default published assets.
./compile-assets  # Optional: Re-compiles your `assets/` directory to `assets.zip`. Useful for updating the default assets URL.
./gradlew run  # Runs Floorcraft as a development application
```

> **Note:** Floorcraft downloads some default assets from a GitHub raw URL by default. This can be any link to a direct
> file. If your fork uses custom assets, edit the `assets=` field in [src/main/resources/globals.properties](src/main/resources/globals.properties).

Once you've made your changes, you can run this command to get a binary that will run on your local machine, or anyone
with the same OS and CPU architecture as you:

```shell
./package-dev
```

Multi-platform packaging gets more involved. If you’re curious, check the publish script — it builds the downloads site
inside `docs/`.
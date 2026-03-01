package com.boyninja1555.floorcraft.cli;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.cli.lib.CLIErrorHandler;
import com.boyninja1555.floorcraft.cli.lib.CLIVisualFeedback;
import com.boyninja1555.floorcraft.cli.lib.ModMetadata;
import com.boyninja1555.floorcraft.lib.AssetManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FloorcraftCLI {

    public void defineMetadata(@NotNull Path output, int step, @Nullable ModMetadata presets) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hey there! It seems your assets do not define any mod metadata. This setup will create a file named \"" + output.toFile().getName() + "\" in the game files root. After this setup finishes, you will be required to re-run the command.");
        String name = presets == null ? "Unnamed" : presets.name();

        if (step == 1) {
            System.out.println("Please enter the name of your mod:");
            System.out.print("> ");
            name = scanner.nextLine().trim();
            step++;
        }

        String version = presets == null ? "0" : presets.version();

        if (step == 2) {
            System.out.println("Please enter the version of your mod, which can be any text:");
            System.out.print("> ");
            version = scanner.nextLine().trim();
            step++;
        }

        boolean containsKeybinds = presets != null && presets.containsKeybinds();
        if (step == 3) {
            System.out.println("Please enter whether your mod contains custom keybinds (y/n):");
            System.out.print("> ");
            String lContainsKeybinds = scanner.nextLine().trim();

            if (!lContainsKeybinds.equals("y") && !lContainsKeybinds.equals("n")) {
                defineMetadata(output, step, new ModMetadata(name, version, containsKeybinds, false));
                return;
            }

            containsKeybinds = lContainsKeybinds.equals("y");
            step++;
        }

        boolean containsAssets = presets != null && presets.containsAssets();
        if (step == 4) {
            System.out.println("Please enter whether your mod contains custom assets (y/n):");
            System.out.print("> ");
            String lContainsAssets = scanner.nextLine().trim();

            if (!lContainsAssets.equals("y") && !lContainsAssets.equals("n")) {
                defineMetadata(output, step, new ModMetadata(name, version, containsKeybinds, containsAssets));
                return;
            }

            containsAssets = lContainsAssets.equals("y");
        }

        try (Writer writer = new FileWriter(output.toFile())) {
            Floorcraft.gson.toJson(new ModMetadata(name, version, containsKeybinds, containsAssets), writer);
        } catch (IOException ex) {
            CLIErrorHandler.crash("Could not save mod metadata!\n" + ex);
        }
    }

    public void packageAssets() {
        Path root = AssetManager.storagePath();
        Path metadataPath = root.resolve("mod.json");

        if (!Files.isDirectory(root)) {
            CLIErrorHandler.crash("Game files not found:\n" + root.toAbsolutePath());
            return;
        }

        if (!Files.exists(metadataPath)) {
            defineMetadata(metadataPath, 1, null);
            return;
        }

        try {
            ModMetadata metadata = Floorcraft.gson.fromJson(Files.readString(metadataPath), ModMetadata.class);
            String fileName = metadata.name().replaceAll("[^a-zA-Z0-9._-]", "_") + ".fcraftmod";
            Path output = Path.of(System.getProperty("user.dir")).resolve(fileName);

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(output))) {
                zos.putNextEntry(new ZipEntry(".floorcraft"));
                Files.copy(metadataPath, zos);
                zos.closeEntry();

                if (metadata.containsKeybinds()) {
                    Path keybinds = root.resolve("block-keybinds.properties");
                    if (Files.exists(keybinds)) {
                        zos.putNextEntry(new ZipEntry("block-keybinds.properties"));
                        Files.copy(keybinds, zos);
                        zos.closeEntry();
                    }
                }

                if (metadata.containsAssets()) {
                    Path assets = root.resolve("assets");
                    if (Files.isDirectory(assets)) {
                        Files.walk(assets).forEach(path -> {
                            try {
                                String rel = assets.relativize(path).toString().replace("\\", "/");
                                String entryName = "assets" + (rel.isEmpty() ? "" : "/" + rel);

                                if (Files.isDirectory(path)) {
                                    zos.putNextEntry(new ZipEntry(entryName + "/"));
                                    zos.closeEntry();
                                } else {
                                    zos.putNextEntry(new ZipEntry(entryName));
                                    Files.copy(path, zos);
                                    zos.closeEntry();
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
            }

            System.out.println("Packaged assets to a mod! You can find it here:\n" + output.toAbsolutePath());
        } catch (Exception ex) {
            CLIErrorHandler.crash("Assets to mod packaging failed!\n" + ex);
        }
    }

    public void installAssets(@NotNull Path sourcePath) {
        Path rootPath = AssetManager.storagePath();

        if (!Files.exists(sourcePath)) {
            CLIVisualFeedback.crash("Mod not found!", sourcePath.toAbsolutePath());
            return;
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourcePath))) {
            ModMetadata metadata = null;
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(".floorcraft")) {
                    metadata = Floorcraft.gson.fromJson(new String(zis.readAllBytes()), ModMetadata.class);
                    break;
                }
            }

            if (metadata == null) {
                CLIVisualFeedback.crash("Invalid mod!", "Missing .floorcraft metadata");
                return;
            }

            boolean installKeybinds = metadata.containsKeybinds();
            boolean installAssets = metadata.containsAssets();
            if (installKeybinds && Files.exists(rootPath.resolve("block-keybinds.properties"))) {
                String response = CLIVisualFeedback.ask("Replace existing block keybinds? (y/n)").trim();
                if (!response.equals("y")) installKeybinds = false;
            }

            if (installAssets && Files.exists(rootPath.resolve("assets"))) {
                String response = CLIVisualFeedback.ask("Replace existing assets? (y/n)").trim();
                if (!response.equals("y")) installAssets = false;
            }

            zis.close();

            try (ZipInputStream extractStream = new ZipInputStream(Files.newInputStream(sourcePath))) {
                ZipEntry e;
                while ((e = extractStream.getNextEntry()) != null) {
                    String name = e.getName();
                    Path target;

                    if (name.equals(".floorcraft")) target = rootPath.resolve("mod.json");
                    else if (name.equals("block-keybinds.properties")) {
                        if (!installKeybinds) continue;
                        target = rootPath.resolve("block-keybinds.properties");
                    } else if (name.startsWith("assets/")) {
                        if (!installAssets) continue;
                        target = rootPath.resolve(name);
                    } else continue;

                    target = target.normalize();
                    if (!target.startsWith(rootPath.normalize())) throw new IOException("Zip slip detected");
                    if (e.isDirectory()) Files.createDirectories(target);
                    else {
                        Files.createDirectories(target.getParent());
                        Files.copy(extractStream, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            CLIVisualFeedback.info("Mod installation complete!", "It will be active the next time you launch your game. Please run the file again if you switch mods at some point, and want to use this one again.");
            System.exit(0);
        } catch (Exception ex) {
            CLIVisualFeedback.crash("Mod installation failed!", ex);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            CLIErrorHandler.crash("Missing package path! If you mean to package your own assets, try using \"-package\" as the only argument.");
            return;
        }

        FloorcraftCLI cli = new FloorcraftCLI();

        if (args[0].equals("-package")) {
            cli.packageAssets();
            return;
        }

        String packageName = args[0];
        cli.installAssets(Path.of(packageName));
    }
}

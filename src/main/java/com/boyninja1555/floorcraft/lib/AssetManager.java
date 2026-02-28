package com.boyninja1555.floorcraft.lib;

import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetManager {

    public static CompletableFuture<Void> init() {
        try {
            Path storage = storagePath();
            Path assetsPath = storage.resolve("assets");
            System.out.println("Storing resources and assets at " + storage);

            if (!Files.exists(assetsPath)) Files.createDirectories(assetsPath);
            if (Files.exists(assetsPath.resolve(".floorcraft"))) return CompletableFuture.completedFuture(null);
            return download(AppProperties.assetsUrl(), assetsPath);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private static CompletableFuture<Void> download(String url, Path targetDir) {
        return CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                Path tempZip = Files.createTempFile("floorcraft_bootstrapper", ".zip");
                client.send(request, HttpResponse.BodyHandlers.ofFile(tempZip));

                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZip.toFile()))) {
                    ZipEntry entry;

                    while ((entry = zis.getNextEntry()) != null) {
                        Path resolvedPath = targetDir.resolve(entry.getName());

                        if (entry.isDirectory()) Files.createDirectories(resolvedPath);
                        else {
                            Files.createDirectories(resolvedPath.getParent());
                            Files.copy(zis, resolvedPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }

                        zis.closeEntry();
                    }
                } finally {
                    Files.deleteIfExists(tempZip);
                }
            } catch (Exception ex) {
                String message = "Could not download assets!\n" + ex;
                System.err.println(message);
                ErrorHandler.crash(message);
            }
        });
    }

    public static Path storagePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) return Paths.get(System.getenv("LOCALAPPDATA"), "Floorcraft");
        else if (os.contains("mac")) return Paths.get(home, "Library", "Application Support", "Floorcraft");
        else return Paths.get(home, ".floorcraft");
    }

    public static Path assetsPath() {
        return storagePath().resolve("assets");
    }

    public static Path blocksPath() {
        return assetsPath().resolve("blocks");
    }

    public static Path blockScriptsPath() {
        return blocksPath().resolve("scripts");
    }

    public static Path texturesPath() {
        return assetsPath().resolve("textures");
    }

    public static Path soundsPath() {
        return assetsPath().resolve("sounds");
    }

    public static Path musicPath() {
        return assetsPath().resolve("music");
    }
}

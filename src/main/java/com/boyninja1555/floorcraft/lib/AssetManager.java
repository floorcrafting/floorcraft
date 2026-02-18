package com.boyninja1555.floorcraft.lib;

import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetManager {

    public static void init() {
        Path assetsPath = storagePath().resolve("assets");

        if (!Files.exists(assetsPath.resolve(".floorcraft"))) {
            System.out.println("Default assets not found! Fetching from remote...");

            try {
                String url = "https://github.com/boyninja1555/floorcraft/raw/refs/heads/main/assets.zip";
                System.out.println("Using URL " + url);
                System.out.println("Using output path " + assetsPath);
                download(url, storagePath());
            } catch (Exception ex) {
                ErrorHandler.crash("Could not download assets!\n" + ex);
            }
        }
    }

    public static void download(String url, Path targetDir) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        Path tempZip = Files.createTempFile("floorcraft_bootstrapper", ".zip");
        client.send(request, HttpResponse.BodyHandlers.ofFile(tempZip));

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZip.toFile()))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                int firstSlash = name.indexOf('/');
                if (firstSlash == -1 || firstSlash == name.length() - 1) continue;

                String strippedName = name.substring(firstSlash + 1);
                Path resolvedPath = targetDir.resolve(strippedName);

                if (entry.isDirectory()) Files.createDirectories(resolvedPath);
                else {
                    Files.createDirectories(resolvedPath.getParent());

                    if (!Files.exists(resolvedPath)) Files.copy(zis, resolvedPath);
                }

                zis.closeEntry();
            }
        } finally {
            Files.deleteIfExists(tempZip);
        }
    }

    public static Path storagePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) return Paths.get(System.getenv("LOCALAPPDATA"), "Floorcraft");
        else if (os.contains("mac")) return Paths.get(home, "Library", "Application Support", "Floorcraft");
        else return Paths.get(home, ".floorcraft");
    }
}

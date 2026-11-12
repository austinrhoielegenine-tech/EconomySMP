package com.archiveaustxn.managers;

import com.archiveaustxn.EconomySMP;
import com.archiveaustxn.utils.ColorUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Automatic updater driven by {@code update.yml} (bundled as {@code core/update/update.yml}).
 *
 * <p>It looks up a GitHub release by tag, finds its plugin JAR asset and, when that asset differs
 * from the JAR that is currently running, stages it in the server's update folder
 * ({@code plugins/update/}). Bukkit/Paper installs the staged JAR on the next restart.
 */
public class UpdateManager {

    private static final String REPOSITORY = "austinrhoielegenine-tech/EconomySMP";
    private static final String API_RELEASE_BY_TAG = "https://api.github.com/repos/" + REPOSITORY + "/releases/tags/";
    private static final String RELEASES_PAGE = "https://github.com/" + REPOSITORY + "/releases";
    private static final long MIN_INTERVAL_MINUTES = 5L;

    private final EconomySMP plugin;
    private volatile String latestVersion = null;
    private volatile boolean updateAvailable = false;
    private volatile boolean updateStaged = false;
    private BukkitTask task;

    public UpdateManager(EconomySMP plugin) {
        this.plugin = plugin;
    }

    /**
     * (Re)starts the periodic update check using the current update.yml values.
     * Safe to call again on reload; the previous schedule is cancelled first.
     */
    public synchronized void checkForUpdates() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        FileConfiguration config = loadConfig();
        if (!config.getBoolean("enabled", true)) {
            updateAvailable = false;
            plugin.getLogger().info("Automatic updater is disabled in update.yml.");
            return;
        }

        long minutes = Math.max(MIN_INTERVAL_MINUTES, config.getLong("check-interval-minutes", 30L));
        long periodTicks = minutes * 60L * 20L;
        task = plugin.getSpigotScheduler().runAsyncTimer(this::runCheck, 20L * 10L, periodTicks);
    }

    private void runCheck() {
        try {
            FileConfiguration config = loadConfig();
            if (!config.getBoolean("enabled", true)) {
                return;
            }
            boolean autoDownload = config.getBoolean("auto-download", true);
            String tag = resolveTag(config.getString("release-tag", "auto"));

            JsonObject release = fetchRelease(tag);
            if (release == null) {
                plugin.getLogger().info("Updater: no release found for tag " + tag + " in " + REPOSITORY + ".");
                return;
            }

            JsonObject asset = pickJarAsset(release);
            if (asset == null) {
                plugin.getLogger().warning("Updater: release " + tag + " has no .jar asset.");
                return;
            }

            File currentJar = currentJarFile();
            String jarName = currentJar != null ? currentJar.getName() : plugin.getName() + ".jar";
            File staged = new File(Bukkit.getUpdateFolderFile(), jarName);

            String assetDigest = digestOf(asset);
            long assetSize = asset.has("size") ? asset.get("size").getAsLong() : -1L;
            String currentHash = currentJar != null && currentJar.isFile() ? sha256(currentJar) : null;
            String stagedHash = staged.isFile() ? sha256(staged) : null;

            boolean matchesRunning;
            boolean matchesStaged;
            if (assetDigest != null) {
                matchesRunning = assetDigest.equalsIgnoreCase(currentHash);
                matchesStaged = assetDigest.equalsIgnoreCase(stagedHash);
            } else {
                matchesRunning = currentJar != null && assetSize >= 0 && assetSize == currentJar.length();
                matchesStaged = staged.isFile() && assetSize >= 0 && assetSize == staged.length();
            }

            latestVersion = tag;

            if (matchesRunning) {
                updateAvailable = false;
                updateStaged = false;
                plugin.getLogger().info("Updater: running the latest build of " + tag + ".");
                return;
            }

            if (matchesStaged) {
                updateAvailable = false;
                updateStaged = true;
                return;
            }

            updateAvailable = true;
            updateStaged = false;
            String downloadUrl = asset.get("browser_download_url").getAsString();

            if (!autoDownload) {
                Bukkit.getConsoleSender().sendMessage(ColorUtils.colorize(
                        "&c[" + plugin.getName() + "] A new build is available (" + tag + "). Download it from: " + RELEASES_PAGE));
                return;
            }

            if (downloadToUpdateFolder(downloadUrl, staged, assetDigest)) {
                updateAvailable = false;
                updateStaged = true;
                Bukkit.getConsoleSender().sendMessage(ColorUtils.colorize(
                        "&a[" + plugin.getName() + "] Update " + tag + " downloaded to plugins/update/" + jarName
                                + ". It will be installed on the next server restart."));
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to check for updates: " + e.getMessage());
        }
    }

    private FileConfiguration loadConfig() {
        File file = new File(plugin.getDataFolder(), "update.yml");
        if (file.isFile()) {
            return YamlConfiguration.loadConfiguration(file);
        }
        try (InputStream in = plugin.getResource(ConfigManager.bundledPath("update.yml"))) {
            if (in != null) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    return YamlConfiguration.loadConfiguration(reader);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to read bundled update.yml: " + e.getMessage());
        }
        return new YamlConfiguration();
    }

    private String resolveTag(String configured) {
        if (configured == null || configured.isBlank() || configured.equalsIgnoreCase("auto")) {
            return "v" + plugin.getDescription().getVersion() + "-pre";
        }
        return configured.trim();
    }

    private JsonObject fetchRelease(String tag) throws IOException {
        HttpURLConnection connection = open(API_RELEASE_BY_TAG + tag, 10_000, 15_000);
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_NOT_FOUND) {
            return null;
        }
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("GitHub returned HTTP " + status);
        }
        try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } finally {
            connection.disconnect();
        }
    }

    private JsonObject pickJarAsset(JsonObject release) {
        JsonElement assetsElement = release.get("assets");
        if (assetsElement == null || !assetsElement.isJsonArray()) {
            return null;
        }
        JsonArray assets = assetsElement.getAsJsonArray();
        JsonObject fallback = null;
        String pluginName = plugin.getName().toLowerCase(Locale.ROOT);
        for (JsonElement element : assets) {
            JsonObject asset = element.getAsJsonObject();
            String name = asset.has("name") ? asset.get("name").getAsString().toLowerCase(Locale.ROOT) : "";
            if (!name.endsWith(".jar") || name.endsWith("-sources.jar") || name.endsWith("-javadoc.jar")) {
                continue;
            }
            if (name.contains(pluginName)) {
                return asset;
            }
            if (fallback == null) {
                fallback = asset;
            }
        }
        return fallback;
    }

    private String digestOf(JsonObject asset) {
        if (!asset.has("digest") || asset.get("digest").isJsonNull()) {
            return null;
        }
        String digest = asset.get("digest").getAsString();
        if (digest.regionMatches(true, 0, "sha256:", 0, 7)) {
            return digest.substring(7).toLowerCase(Locale.ROOT);
        }
        return null;
    }

    private boolean downloadToUpdateFolder(String url, File target, String expectedSha256) {
        File temp = new File(target.getParentFile(), target.getName() + ".part");
        try {
            Files.createDirectories(target.getParentFile().toPath());
            HttpURLConnection connection = open(url, 10_000, 60_000);
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("download returned HTTP " + status);
            }
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } finally {
                connection.disconnect();
            }

            if (expectedSha256 != null && !expectedSha256.equalsIgnoreCase(sha256(temp))) {
                throw new IOException("downloaded file did not match the published checksum");
            }
            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Updater: failed to download the update: " + e.getMessage());
            try {
                Files.deleteIfExists(temp.toPath());
            } catch (IOException ignored) {
                // best effort cleanup
            }
            return false;
        }
    }

    private HttpURLConnection open(String url, int connectTimeoutMillis, int readTimeoutMillis) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(connectTimeoutMillis);
        connection.setReadTimeout(readTimeoutMillis);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", plugin.getName() + "-Updater");
        return connection;
    }

    private File currentJarFile() {
        try {
            return new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            return null;
        }
    }

    private static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file.toPath())) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public boolean isUpdateStaged() {
        return updateStaged;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}

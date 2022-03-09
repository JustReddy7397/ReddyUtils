package wiki.justreddy.ga.reddyutils.dependency.util;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import wiki.justreddy.ga.reddyutils.dependency.DLoader;
import wiki.justreddy.ga.reddyutils.dependency.base.Dependency;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Urls {

    private static final List<String> REPOSITORIES = new ArrayList<>();

    static {
        REPOSITORIES.add("https://repo1.maven.org/maven2/");
    }

    public static void addRepositories( List<String> repositories) {
        REPOSITORIES.addAll(repositories.stream().map(Urls::fixUrl).collect(Collectors.toList()));
    }

    public static void addRepositories( String... repositories) {
        addRepositories(Arrays.asList(repositories));
    }

    private Urls() {}

    public static String getBaseUrl( Dependency dependency) {
        return dependency.getGroupId().replace('.', '/') + '/' + dependency.getArtifactId() + '/' + dependency.getVersion() + '/';
    }

    public static String getJarUrl( Dependency dependency) {
        return getBaseUrl(dependency) + dependency.getJarName();
    }

    public static String getPomUrl( Dependency dependency) {
        return getBaseUrl(dependency) + dependency.getPomName();
    }

    public static String getMetaUrl( Dependency dependency) {
        return getBaseUrl(dependency) + "maven-metadata.xml";
    }

    public static String fixUrl( String original) {
        return original.isEmpty() || original.endsWith("/") ? original : original + '/';
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void download( Dependency dependency,  File folder,  BiConsumer<File, File> whenDone) {
        final File jarFile = new File(folder, dependency.getJarName()), pomFile = new File(folder, dependency.getPomName());

        boolean alwaysUpdate = dependency.getOptions().isAlwaysUpdate(), isSnapShot = dependency.getVersion().endsWith("-SNAPSHOT");

        if (jarFile.exists() && !isSnapShot && !alwaysUpdate) {
            whenDone.accept(jarFile, pomFile);
            return;
        }

        if (!folder.exists()) folder.mkdirs();

        try {

            final String pomUrl, jarUrl;
            final String customRepo = dependency.getOptions().getCustomRepository();

            if (isSnapShot) {
                final File metaFile = new File(folder, "meta.xml");
                tryDownload(getMetaUrl(dependency), metaFile, customRepo);

                final String latestSnapShot = Xmls.readLatestSnapshot(dependency, metaFile);
                final String latestFileName = dependency.getArtifactId() + "-" + latestSnapShot;

                final File latestFile = new File(folder, latestFileName);
                if (latestFile.exists() && !alwaysUpdate) {
                    whenDone.accept(jarFile, pomFile);
                    return;
                } else {
                    if (pomFile.exists()) FileUtils.forceDelete(pomFile);
                    if (jarFile.exists()) FileUtils.forceDelete(jarFile);
                }

                pomUrl = getBaseUrl(dependency) + latestFileName + ".pom";
                jarUrl = getBaseUrl(dependency) + latestFileName + ".jar";

                latestFile.createNewFile();
            } else {
                pomUrl = getPomUrl(dependency);
                jarUrl = getJarUrl(dependency);
            }

            tryDownload(pomUrl, pomFile, customRepo);
            tryDownload(jarUrl, jarFile, customRepo);

            whenDone.accept(jarFile, pomFile);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            "&c" + DLoader.getInstance().getPrefix() + " Failed to download the dependency " + dependency.getName())
            );        }
    }


    private static void tryDownload( String fileUrl,  File file,  String... customUrl) throws Exception {
        DLoader.debug("Attempting to download " + fileUrl);

        if (customUrl.length > 0 && !customUrl[0].isEmpty()) {
            openStream(customUrl[0] + fileUrl, (url, stream) -> pullFromStreamToFile(stream, url, file));
            return;
        }

        for (String url : REPOSITORIES) {
            final String actualUrl = url + fileUrl;
            DLoader.debug("URL is '" + actualUrl + "'");
            try {
                openStream(actualUrl, (fUrl, stream) -> pullFromStreamToFile(stream, fUrl, file));
                return;
            } catch (IOException e) {
                DLoader.log(Level.WARNING, "Failed to download from repo '" + url + "'");
            }
        }

        DLoader.log(Level.SEVERE, "Failed to download " + fileUrl);
    }

    private static void openStream( String url,  BiConsumer<String, InputStream> block) throws IOException {
        try (InputStream stream = new URL(url).openStream()) {
            block.accept(url, stream);
        }
    }

    private static void pullFromStreamToFile( InputStream stream, String url,  File file) {
        try {
            FileUtils.copyInputStreamToFile(stream, file);

            if (!file.getName().endsWith(".jar") || !DLoader.isEnforcingFileCheck()) return;

            openStream(url + ".sha1", (shaUrl, shaStream) -> {

                try {
                    final String mavenSha1 = IOUtils.toString(shaStream);
                    final String fileSha1  = Files.hash(file, Hashing.sha1()).toString();

                    DLoader.debug("Maven SHA-1: " + mavenSha1, "File SHA-1: " + fileSha1);

                    if (!mavenSha1.equals(fileSha1)) {
                        FileUtils.forceDelete(file);
                        throw new IllegalStateException("Failed to validate downloaded file " + file.getName());
                    }

                    DLoader.debug("File " + file.getName() + " passed validation");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            "&c" + DLoader.getInstance().getPrefix() + " Failed to download url to file  " + file.getName())
            );        }
    }

}

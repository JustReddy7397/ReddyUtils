package wiki.justreddy.ga.reddyutils;

import org.bukkit.plugin.java.JavaPlugin;
import wiki.justreddy.ga.reddyutils.dependency.*;
import wiki.justreddy.ga.reddyutils.old.dependency.DLoader;
import wiki.justreddy.ga.reddyutils.manager.DatabaseManager;
import wiki.justreddy.ga.reddyutils.old.dependency.base.Dependency;

import java.io.File;
import java.io.IOException;

public class Version  extends JavaPlugin {

    public static final double API_VERSION = 2.9;

    @Override
    public void onLoad() {
        new DependencyResolver(this);
        try {
            DependencyResolver.resolveArtifact("com.h2database", "h2", "1.4.200");
            DependencyResolver.download(new Artifact("com.h2database", "h2", "1.4.200"), "plugins/ReddyUtils/libs/");
        } catch (ArtifactResolveException | IOException e) {
            e.printStackTrace();
        }

/*        ParsedPom parsedPom = new ParsedPom();
        MavenDownloader downloader = new MavenDownloader(new File("pom.xml"), parsedPom, true, true);
        parsedPom.addDependency(new ParsedPom.Dependency("com.h2database", "h2", "1.4.200"));
        downloader.download();*/
    }

    @Override
    public void onEnable() {

        new DatabaseManager().connectH2(this, "plugins/" + getDescription().getName() + "/database/database.db");
    }
}

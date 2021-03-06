package wiki.justreddy.ga.reddyutils.dependency;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import wiki.justreddy.ga.reddyutils.dependency.base.Dependency;
import wiki.justreddy.ga.reddyutils.dependency.util.Urls;
import wiki.justreddy.ga.reddyutils.dependency.util.Xmls;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sxtanna
 */

public class DLoader {

    private static DLoader instance;

    private JavaPlugin javaPlugin;


    private static Method method;
    private static final CustomClassLoader classLoader = new CustomClassLoader(new URL[0], getInstance().getClass().getClassLoader());

    private static boolean working = true, showDebug = false, enforceFileCheck = true;


    private File dependencyFolder;
    private final Map<String, Dependency> dependencies = Maps.newHashMap();

    static {
        try{
            method = classLoader.getClass().getMethod("addURL", URL.class);
            method.setAccessible(true);
        }catch (NoSuchMethodException ex){
            ex.printStackTrace();
        }
    }

    /*
    Put this in your onLoad();
     */
    public void onLoad(JavaPlugin javaPlugin){
        if (!working) return;
        this.javaPlugin = javaPlugin;
        dependencyFolder = new File("plugins/"+ javaPlugin.getDataFolder().getName() +"/Dependencies");
        if (!dependencyFolder.exists()) dependencyFolder.mkdirs();
        load(new Dependency("h2", "1.4.200", "com.h2database", "h2"));
    }

    public void loadRequiredDependencies(){
        load(new Dependency("h2", "1.4.200", "com.h2database", "h2"));
        load(new Dependency("mongodb-driver", "3.12.10", "org.mongodb", "mongodb-driver"));
        load(new Dependency("mongodb-driver-core", "3.12.10", "org.mongodb", "mongodb-driver-core"));
        load(new Dependency("BSON", "4.4.0", "org.mongodb", "bson"));
    }

    public static boolean isShowingDebug() {
        return showDebug;
    }

    public static boolean isEnforcingFileCheck() {
        return enforceFileCheck;
    }

    public void load( Dependency dependency) {
        load(dependency, () -> {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + javaPlugin.getDescription().getName() + " Successfully downloaded the dependency " + dependency.getName());
        });
    }

    public void load( Dependency dependency,  Runnable whenDone) {
        debug(" ", " ", blockBar(60), " ", blockArrow(dependency, "v"));

        Urls.download(dependency, new File(dependencyFolder, dependency.getGroupId()), (jar, pom) -> {

            if (!jar.exists() || !pom.exists())
                debug("POM File Downloaded -> " + pom.exists(), "Jar File Downloaded -> " + jar.exists());
            else {
                loadJar(dependency, jar);
                loadChildren(dependency, pom, whenDone);
            }
        });
    }

    public Optional<Dependency> get( String name) {
        return Optional.ofNullable(dependencies.get(name.toLowerCase()));
    }


    private void loadChildren(Dependency dependency, File pomFile, Runnable whenDone) {
        debug("Loading child dependencies of " + dependency.getName());
        List<Dependency> children = Xmls.readDependencies(pomFile);
        if (children.isEmpty()) {
            debug("No children found in " + dependency.getName(), blockArrow(dependency, "^"), blockBar(60), " ", " ");
            whenDone.run();
            return;
        }

        final int[] loaded = {0};

        children.forEach(child -> {
            child.setParent(dependency);

            load(child, () -> {
                if (++loaded[0] == children.size()) {
                    debug("Finished loading children from " + dependency.getName(), blockArrow(dependency, "^"), " ", blockBar(60), " ", " ");
                    whenDone.run();
                }
            });
        });
    }

    private void loadJar(Dependency dependency, File jarFile) {
        try {
            method.invoke(classLoader, jarFile.toURI().toURL());
            debug("Added " + jarFile.getName() + " to ClassLoader");
            dependencies.put(dependency.getName().toLowerCase(), dependency);
        } catch (Exception e) {
            log(Level.SEVERE, "Failed to load Jar File " + jarFile.getName());
            e.printStackTrace();
        }
    }

    private String blockArrow(Dependency dependency, String c) {
        final String generated = Strings.repeat(c, dependency.getParentDepth());
        return generated.isEmpty() ? c + c + c : generated;
    }

    private String blockBar(int length) {
        return Strings.repeat("=", length);
    }


    public static void debug(String... message) {
        if (isShowingDebug()) log(Level.WARNING, message);
    }

    public static void log(Level level, String... message) {
        Logger logger = Bukkit.getLogger();
        for (String msgLine : message) logger.log(level, msgLine);
    }


    public static DLoader getInstance() {
        if(instance == null) instance = new DLoader();
        return instance;
    }
}

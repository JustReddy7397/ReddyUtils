package wiki.justreddy.ga.reddyutils.dependency;


import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * This plays the role of resolving the dependencies of an input artifact
 *      and downloads them on a given directory.
 * @author Yizhong Ding
 */
public class DependencyResolver {

    private static JavaPlugin javaPlugin;

    public DependencyResolver(JavaPlugin javaPlugin){
        DependencyResolver.javaPlugin = javaPlugin;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    /**
     * This method resolve the dependencies of a given artifact coordinate and store the Jar files on the given path.
     * @param groupId groupId of the target artifact
     * @param artifactId artifactId of the target artifact
     * @param version version of the target artifact
     * @return returns a list of dependency Artifacts
     * @throws ArtifactResolveException throw ArtifactResolveException if the inputs are not valid
     */
    public static List<Artifact> resolveArtifact(String groupId, String artifactId, String version) throws ArtifactResolveException {
        String downloadPath = "plugins/" + javaPlugin.getDescription().getName() + "/libs/";
        ensureTargetDirectoryExists(downloadPath);
        Artifact artifact = handleInputArtifact(groupId, artifactId, version);
        return resolveDependencies(artifact, downloadPath);
    }

    /**
     * Overloaded method of previous. See javadoc above.
     * @param artifact as the target artifact to collect with all transitive dependencies.
     * @return list of transitive dependency Artifacts (location on disk)
     * @throws ArtifactResolveException throw ArtifactResolveException if fail to resolve Artifact
     */
    public static List<Artifact> resolveArtifact(Artifact artifact) throws ArtifactResolveException {
        return resolveArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }


    /**
     * This method checks if the download directory exists. If not, it will create the directory.
     * @param downloadPath String of download path
     */
    private static void ensureTargetDirectoryExists(String downloadPath) {
        // Check if the directory exists.
        File directory = new File(downloadPath);
        if (!directory.exists()){
            // Create it if it does not yet exist.
            directory.mkdirs();
        }
    }

    /**
     * The methods plays the roles of traversing the dependency graph of the given artifact
     *      and downloading the artifacts as jar files. It utilizes a BFS algorithm to traverse
     *      the dependency graph.
     * @param target the target Artifact
     * @param downloadPath as the location where tha resolved JARs should be stored on disk.
     * @return Returns a list of successfully downloaded artifacts.
     * @throws ArtifactResolveException if fail to resolve dependencies.
     */
    public static List<Artifact> resolveDependencies(Artifact target, String downloadPath) throws ArtifactResolveException {
        ensureTargetDirectoryExists(downloadPath);
        Set<Artifact> downloaded = new HashSet<>();
        Set<Artifact> excludedDependencies = new HashSet<>();
        Set<Artifact> deleteSet = new HashSet<>();
        Queue<Artifact> queue = new LinkedList<>();
        queue.add(target);
        // In each iteration, removes the first node, and appends all the transitive dependencies to the queue.
        while(!queue.isEmpty()){
            traverseDependencyNode(downloaded, queue, excludedDependencies);
        }
        // Update version numbers from central POM file (if it exists)
        downloaded = updateVersionFromCentralPOM(target, downloaded);
        // Remove exclusions from the download set and replace the artifact in exclusion set for deleting
        for (Artifact artifact : excludedDependencies) {
            if(downloaded.contains(artifact)){
                deleteSet.add(Util.getFromSet(downloaded, artifact));
            }
        }
        downloaded.removeAll(excludedDependencies);
        StringBuilder res = new StringBuilder("Successfully downloaded: \n");
        for (Artifact artifact: downloaded) {
            try{
                download(artifact, downloadPath);
            }
            catch (IOException error){
                throw new ArtifactResolveException(error.getMessage());
            }
            res.append("\t" + artifact.getArtifactId() + " " + artifact.getVersion() + " " + artifact.getGroupId() + "\n");
        }
        Logger.getAnonymousLogger(res.toString());
        return new ArrayList<>(downloaded);
    }

    /**
     * Helper method that updates dependency versions from a central POM file.
     *
     * @param target target artifact
     * @param downloaded dependency set
     * @return a new set of dependencies containing updated versions
     */
    private static Set<Artifact> updateVersionFromCentralPOM(Artifact target, Set<Artifact> downloaded) {
        Artifact centralArtifact = DependencyParser.getCentralArtifact(target);
        if(centralArtifact == null) return downloaded;
        return DependencyParser.getUpdatedDependencies(target, downloaded);
    }


    /**
     * Helper method that visits dependency Graph nodes.

     * @param downloaded downloaded a set of successfully downloaded Artifacts
     * @param queue queue used for BFS traverse the dependency graph
     * @param excludedDependencies the set used to
     * @throws ArtifactResolveException if fail to resolve dependency
     */
    public static void traverseDependencyNode(Set<Artifact> downloaded, Queue<Artifact> queue, Set<Artifact> excludedDependencies) throws ArtifactResolveException {
        Artifact queueHead = queue.poll();
        if(downloaded.contains(queueHead)) { // avoid cycle in the dependency graph and update version
            downloaded.remove(queueHead);
            downloaded.add(queueHead);
            return;
        }
        // add all exclusions to the set in order to avoid downloading unnecessary artifact
        if(queueHead.getExclusions() != null){
            for (Artifact artifact : queueHead.getExclusions()) {
                //To avoid local exclusion:
                //  https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html
                if(!queue.contains(artifact) && !downloaded.contains(artifact)) excludedDependencies.add(artifact);
            }
        }
        // add the visited artifact into the set.
        downloaded.add(queueHead);
        try{
            // Fetch dependencies list of curr artifact and add them in the help queue.
            List<Artifact> dependencies = DependencyParser.fetchDependencies(queueHead);
            queue.addAll(dependencies);
        }
        catch(Error error){
            throw new ArtifactResolveException(error.getMessage());
        }
    }

    /**
     * This method validates the user entered coordinate of an artifact as well as the directory of output jar files.
     * @return An Artifact object created by the given artifact coordinate.
     * @throws ArtifactResolveException Throw an exception if the input is not valid.
     * @param groupId input groupId
     * @param artifactId input artifactId
     * @param version input version
     */
    static Artifact handleInputArtifact(String groupId, String artifactId, String version) throws ArtifactResolveException {
        if(groupId.length() == 0 || artifactId.length() == 0 || version.length() == 0){
            throw new ArtifactResolveException("Error: Input artifact is not valid");
        }
        return new Artifact(groupId, artifactId, version);
    }

    /**
     * The method plays the role of downloading Artifact.
     * @param artifact The maven artifact to be downloaded.
     * @param downloadPath The path to store Jar files.
     * @throws ArtifactResolveException Throw an exception if it fails to fetch the Jar file online.
     * @throws IOException Throw an exception if it fails to write the file.
     */
    public static void download(Artifact artifact, String downloadPath) throws ArtifactResolveException, IOException {
        OutputStream outputStream = new FileOutputStream(downloadPath + '/' + artifact + ".jar");
        FileWriter fileWriter = new FileWriter();
        fileWriter.setOutputStream(outputStream);
        OkHttpClient client = new OkHttpClient();
        ArtifactDownloader downloader = new ArtifactDownloader(client, fileWriter);
        String path = Util.getJarPath(artifact);
        downloader.download(Util.getJarURL(), path);
    }

    /**
     * The method plays the role of deleting Artifact file on a give path.
     * @param artifact target artifact.
     * @param downloadPath given path.
     * @throws ArtifactResolveException throw exception if deletion fails.
     */
    public static void removeExclusion(Artifact artifact, String downloadPath) throws ArtifactResolveException {
        File artifactFile = new File(downloadPath + '/' + artifact + ".jar");
        Boolean res = artifactFile.delete();
        if (!res) {
            throw new ArtifactResolveException("Error: Failed to delete exclusion (" + artifact + ").");
        }
    }
}

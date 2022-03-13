package wiki.justreddy.ga.reddyutils.dependency;

import java.util.Set;

/**
 * Util class providing the common methods
 *  and URL/Path configurations for the project.
 * @author Yizhong Ding
 */
public class Util {
    /**
     * This method generates the path of POM file for a given Artifact.
     * @param artifact The target artifact.
     * @return A generated Pom file path.
     */
    public static String createPomPath(Artifact artifact){
        return artifact.getGroupId().replace('.', '/') + '/'
                + artifact.getArtifactId() + '/'
                + artifact.getVersion() + '/'
                + artifact.getArtifactId() + '-' + artifact.getVersion() + ".pom";
    }

    /**
     * This method generates the reversed path of POM file for a given Artifact.
     * @param artifact The target artifact.
     * @return A generated Pom file path.
     */
    public static String createReversedPomPath(Artifact artifact){
        return artifact.getGroupId().replace('.', '/') + '/'
                + artifact.getVersion() + '/'
                + artifact.getArtifactId() + '/'
                + artifact.getVersion() + '-' + artifact.getArtifactId() + ".pom";
    }

    /**
     * This method generates the reversed path of JAr file for a given Artifact.
     * @param artifact The target artifact.
     * @return A generated Jar file path.
     */
    public static String getJarPath(Artifact artifact){
        return artifact.getGroupId().replace(".", "/")
                + "/" + artifact.getArtifactId() + "/"
                + artifact.getVersion() + "/"
                + artifact.getArtifactId() + "-"
                + artifact.getVersion() + ".jar";
    }

    /**
     * This method generates the search path of a given artifactId and groupId.
     * @param childArtifactId Target artifactId
     * @param childGroupId Target groupId
     * @return The Path of the search result (in Pom file) on maven library.
     */
    public static String getSearchPath(String childArtifactId, String childGroupId){
        return "solrsearch/select?q=g:\"" + childGroupId + "\"+AND+a:\"" + childArtifactId + "\"&core=gav&rows=20&wt=pom";
    }

    /**
     * Return the namespace of Maven Pom files.
     * @return The namespace.
     */
    public static String namespace(){
        return "http://maven.apache.org/POM/4.0.0";
    }

    /**
     * Get the URL of Pom files on Maven central library.
     * @return The URL of POM files.
     */
    public static String getPomURL(){
        return "https://search.maven.org/remotecontent?filepath=";
    }

    /**
     * Get the URL of Jar files on Maven central library.
     * @return The URL of Jar files.
     */
    public static String getJarURL(){
        return "https://search.maven.org/remotecontent?filepath=";
    }

    /**
     * Get the URL for searching artifacts on Maven central library.
     * @return The URL of searching artifacts.
     */
    public static String getSearchURL(){
        return "https://search.maven.org/";
    }

    /**
     * Get an element from a Set.
     * @param set target set.
     * @param artifact target artifact.
     * @return return the result artifact got from the set.
     */
    public static Artifact getFromSet(Set<Artifact> set, Artifact artifact) {
        for (Artifact currArtifact : set) {
            if(currArtifact.equals(artifact)) return currArtifact;
        }
        return null;
    }
}
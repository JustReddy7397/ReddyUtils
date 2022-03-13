package wiki.justreddy.ga.reddyutils.dependency;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Plays the role of parsing a list of (direct) dependency of a given Artifact. It uses HTTP based fetcher to retrieve a
 * POM file and return the Dependencies lists on the POM file.
 *
 * @author Yizhong Ding
 */
public class DependencyParser {

    /**
     * This Method fetches the dependency list by parsing the POM file.
     *
     * @param artifact The target artifact to get dependencies.
     * @return Returns a list of artifact dependencies.
     */
    public static List<Artifact> fetchDependencies(Artifact artifact) {
        List<Artifact> result = new ArrayList<>();
        Document doc = getDocument(artifact);
        // If the dependencies are not found, return the empty list
        if (doc == null) {
            Logger.getAnonymousLogger().severe("Failed to find dependencies for (No POM file): " + artifact.toString());
            return result;
        }
        Element rootNode = doc.getRootElement();
        Namespace ns = Namespace.getNamespace(Util.namespace());
        // Check if the POM file contains "dependencyManagement" element
        Element dependencies = rootNode.getChild("dependencyManagement", ns);
        // If not, check for "dependencies" element
        if (dependencies == null) {
            dependencies = rootNode.getChild("dependencies", ns);
        } else {
            dependencies = dependencies.getChild("dependencies", ns);
        }
        //If the POM file does contain "dependencyManagement" or "dependencies"  element
        if (dependencies != null) {
            getDependencyList(result, dependencies, ns, rootNode, artifact);
        }
        return result;
    }

    /**
     * Returns a list of dependency parsed from the given POM element.
     *
     * @param result       result dependency list
     * @param dependencies dependencies element of the Pom file
     * @param ns           name space of the Pom file
     * @param rootNode     rootNode of the Pom file
     * @param artifact     target Artifact object
     */
    private static void getDependencyList(List<Artifact> result, Element dependencies, Namespace ns, Element rootNode, Artifact artifact) {
        for (Element target : dependencies.getChildren("dependency", ns)) {
            if (target.getChild("scope", ns) != null) {
                String scope = target.getChild("scope", ns).getValue();
                // If the scope of the dependency is test, just skip
                if (scope.equals("test") || scope.equals("provided") || scope.equals("system") || scope.equals("import"))
                    continue;
            }
            if (target.getChild("optional", ns) != null
                    && target.getChild("optional", ns).getValue().equals("true")) {
                continue;
            }
            String childArtifactId = target.getChild("artifactId", ns).getValue();
            String childGroupId = target.getChild("groupId", ns).getValue();
            Element version = target.getChild("version", ns);
            // Check if artifactId and groupId are not variables. eg: ${pom.groupId}.
            if (checkArtifactId(childArtifactId)) {
                childArtifactId = artifact.getArtifactId();
            }
            if (checkGroupId(childGroupId)) {
                childGroupId = artifact.getGroupId();
            }
            String childVersion = parseDependencyVersion(childGroupId, childArtifactId, version, rootNode, ns, artifact);
            if (childVersion == null) continue;
            Artifact child = new Artifact(childGroupId, childArtifactId, childVersion);
            if (target.getChild("exclusions", ns) != null) {
                Element exclusions = target.getChild("exclusions", ns);
                child.setExclusions(getExclusionsList(exclusions, ns));
            }
            result.add(child);
        }
    }

    /**
     * Returns a list of exclusion artifact parsed from the given POM element.
     *
     * @param exclusions target element.
     * @param ns         namespace.
     * @return return a exclusion list.
     */
    private static List<Artifact> getExclusionsList(Element exclusions, Namespace ns) {
        List<Artifact> res = new ArrayList<>();
        for (Element exclusion : exclusions.getChildren("exclusion", ns)) {
            String artifactId = exclusion.getChild("artifactId", ns).getValue();
            String groupId = exclusion.getChild("groupId", ns).getValue();
            String version = exclusion.getChild("version", ns) == null ?
                    null : exclusion.getChild("version", ns).getValue();
            Artifact current = new Artifact(groupId, artifactId, version);
            res.add(current);
        }
        return res;
    }


    /**
     * This is a helper function to parse the version of a dependency in the Pom file
     *
     * @param childGroupId    group id of a dependency
     * @param childArtifactId artifact id of a dependency
     * @param version         version of a dependency
     * @param ns              name space of the Pom file
     * @param rootNode        rootNode of the Pom file
     * @param artifact        target Artifact object
     * @return returns the version of a given dependency. returns null if it does not exists.
     */
    private static String parseDependencyVersion(String childGroupId, String childArtifactId, Element version, Element rootNode, Namespace ns, Artifact artifact) {
        String childVersion;
        // If version is not specified in the POM file, search on maven library.
        if (version == null) {
            // If version is not specified, first find from parent version
            childVersion = getFromParentVersion(childGroupId, childArtifactId, rootNode, ns, null);
            if (childVersion == null) {
                childVersion = searchOnlineVersion(childGroupId, childArtifactId);
            }
            if (childVersion == null) {
                Logger.getAnonymousLogger().severe("Error: Version not found for: " + childArtifactId + " " + childGroupId);
            }
        }
        // Check if version is not variable. eg: ${pom.version}.
        else if (checkVersion(version)) {
            childVersion = artifact.getVersion();
        }
        // If the version is in a range, search version on Maven Central Library.
        else if (version.getValue().contains("(") || version.getValue().contains(")") || version.getValue().contains("[") || version.getValue().contains("]")) {
            childVersion = searchOnlineVersion(childGroupId, childArtifactId);
        }
        // If version is specified in property element
        else if (version.getValue().startsWith("${")) {
            childVersion = getNonSpecifiedVersion(childGroupId, childArtifactId, version, rootNode, ns);
        }
        // Normal version format. Eg: version is well defined in <dependency> element
        else {
            childVersion = version.getValue();
        }
        return childVersion;
    }

    /**
     * This is a helper function to get the non-specified version.
     *
     * @param childGroupId    child groupId.
     * @param childArtifactId child artifactId.
     * @param version         version.
     * @param rootNode        rootNode.
     * @param ns              namespace.
     * @return return the version.
     */
    private static String getNonSpecifiedVersion(String childGroupId, String childArtifactId, Element version, Element rootNode, Namespace ns) {
        String childVersion;
        childVersion = searchPropertyVersion(rootNode, version.getValue(), ns);
        // If the variable is not specified in curr pom file, find the version variable in its parents recursively
        if (childVersion == null)
            childVersion = getFromParentVersion(childGroupId, childArtifactId, rootNode, ns, childVersion);
        // If version is not specified, then search available version online.
        // Eg: https://search.maven.org/classic/remotecontent?filepath=net/bytebuddy/byte-buddy-agent/1.11.21/byte-buddy-agent-1.11.21.pom
        if (childVersion == null) childVersion = searchOnlineVersion(childGroupId, childArtifactId);
        return childVersion;
    }


    /**
     * This method will fetch the dependency version by parsing parent's Pom file.
     *
     * @param childGroupId    target groupId.
     * @param childArtifactId target artifactId.
     * @param childRootNode   the root node of the Pom file.
     * @param ns              namespace.
     * @return returns the version. If it does not exist, return null.
     */
    private static String getFromParentVersion(String childGroupId, String childArtifactId, Element childRootNode, Namespace ns, String currVersion) {
        Element parent = childRootNode.getChild("parent", ns);
        // Base case of the recursion. If there's no parent, just return null
        if (parent == null) return null;
        Artifact parentArtifact = createArtifact(ns, parent);
        Document doc = getDocument(parentArtifact);
        // If the dependencies are not found, return the empty list
        if (doc == null) return null;
        return getVersionFromDoc(childGroupId, childArtifactId, ns, parentArtifact, doc);
    }

    /**
     * Get a version string from a given document.
     *
     * @param childGroupId    groupId for the target artifact.
     * @param childArtifactId artifactId for the target artifact.
     * @param ns              namespace.
     * @param parentArtifact  parent artifact.
     * @param doc             a given document containing the version info.
     * @return searched version string.
     */
    private static String getVersionFromDoc(String childGroupId, String childArtifactId, Namespace ns, Artifact parentArtifact, Document doc) {
        //get dependencies list
        Element rootNode = doc.getRootElement();
        // Check if the POM file contains "dependencyManagement" element
        Element dependencies = rootNode.getChild("dependencyManagement", ns);
        // If not, check for "dependencies" element
        if (dependencies == null) {
            dependencies = rootNode.getChild("dependencies", ns);
        } else {
            dependencies = dependencies.getChild("dependencies", ns);
        }
        String version = null;
        //If the POM file does contain "dependencyManagement" or "dependencies"  element
        if (dependencies != null) {
            String temp = getDependencyVersion(dependencies, ns, rootNode, childArtifactId, childGroupId, parentArtifact);
            if (temp != null) version = temp;
        }
        Element property = rootNode.getChild("properties", ns);
        if (property != null && version != null && version.contains("${")) {
            version = searchPropertyVersion(rootNode, version, ns);
        }
        //if current parent does not contain the variable, go to its parent recursively
        if (version == null || version.contains("${"))
            return getFromParentVersion(childGroupId, childArtifactId, rootNode, ns, version);
        return version;
    }

    /**
     * This method updates the versions of a given dependency set by parsing the central POM file.
     *
     * @param centralArtifact the artifact containing all version info.
     * @param downloaded      the given downloaded set.
     * @return a updated set containing new versions.
     */
    public static Set<Artifact> getUpdatedDependencies(Artifact centralArtifact, Set<Artifact> downloaded) {
        Document doc = getDocument(centralArtifact);
        Namespace ns = Namespace.getNamespace(Util.namespace());
        Set<Artifact> updatedSet = new HashSet<>();
        for (Artifact artifact : downloaded) {
            String updatedVersion = getVersionFromDoc(artifact.getGroupId(), artifact.getArtifactId(), ns, centralArtifact, doc);
            if (updatedVersion != null) {
                artifact.setVersion(updatedVersion);
            }
            updatedSet.add(artifact);
        }
        return updatedSet;
    }

    /**
     * This method will get a Pom Document based of a given artifact according to parent element.
     *
     * @param parentArtifact target artifact.
     * @return returns a download POM document.
     */
    private static Document getDocument(Artifact parentArtifact) {
        String REMOTE_URL = Util.getPomURL();
        Document doc = parseDoc(REMOTE_URL, parentArtifact);
        // Try another way to construct the URL
        if (doc == null) {
            doc = parseDocReversely(REMOTE_URL, parentArtifact);
        }
        return doc;
    }

    /**
     * This method will create an artifact according to parent element.
     *
     * @param ns     namespace.
     * @param parent parent element.
     * @return a newly created artifact.
     */
    private static Artifact createArtifact(Namespace ns, Element parent) {
        String parentArtifactId = parent.getChild("artifactId", ns).getValue();
        String parentGroupId = parent.getChild("groupId", ns).getValue();
        String parentVersion = parent.getChild("version", ns).getValue();
        Artifact parentArtifact = new Artifact(parentGroupId, parentArtifactId, parentVersion);
        return parentArtifact;
    }

    /**
     * This method will get the version of a given artifact in a parent Pom file.
     *
     * @param dependencies    dependency list of parent.
     * @param ns              namespace.
     * @param rootNode        rootnode of parent Pom file.
     * @param childArtifactId target artifactID.
     * @param childGroupId    target groupId.
     * @param parentArtifact  parent artifact.
     * @return returns version if it exists. returns null if it does not exist.
     */
    private static String getDependencyVersion(Element dependencies, Namespace ns, Element rootNode, String childArtifactId, String childGroupId, Artifact parentArtifact) {
        String childVersion = null;
        for (Element target : dependencies.getChildren("dependency", ns)) {
            if (!childArtifactId.equals(target.getChild("artifactId", ns).getValue())) continue;
            Element version = target.getChild("version", ns);
            childVersion = parseDependencyVersion(childGroupId, childArtifactId, version, rootNode, ns, parentArtifact);
            if (childVersion != null) break;
        }
        return childVersion;
    }


    /**
     * It returns a Document object fetched by SAXBuilder.
     *
     * @param REMOTE_URL the URL for the Pom file
     * @param artifact   the target artifact object
     * @return returns a fetched Pom file. returns null if it does not exists.
     */
    private static Document parseDoc(String REMOTE_URL, Artifact artifact) {
        try {
            SAXBuilder sax = new SAXBuilder();
            URL url = new URL(REMOTE_URL + Util.createPomPath(artifact));
            return sax.build(url);
        } catch (JDOMException | IOException e) {
            return null;
        }
    }

    /**
     * It returns a Document object fetched by SAXBuilder. The URL is constructed in a reversed order.
     *
     * @param REMOTE_URL the URL for the Pom file
     * @param artifact   the target artifact object
     * @return returns a fetched Pom file. returns null if it does not exists.
     */
    private static Document parseDocReversely(String REMOTE_URL, Artifact artifact) {
        try {
            SAXBuilder sax = new SAXBuilder();
            URL url = new URL(REMOTE_URL + Util.createReversedPomPath(artifact));
            return sax.build(url);
        } catch (JDOMException | IOException e) {
            return null;
        }
    }

    /**
     * It searches version of a dependency which is specified as a variable such as <version>${junit.version}</version>.
     * Example can be found: https://repo1.maven.org/maven2/cn/leancloud/okhttp-parent/2.6.0/okhttp-parent-2.6.0.pom.
     *
     * @param rootNode     rootNode of the Pom file
     * @param childVersion version value in the Pom file
     * @param ns           name space of the Pom file
     * @return returns version string inside Property element of the Pom file. returns null if it does not exists.
     */
    private static String searchPropertyVersion(Element rootNode, String childVersion, Namespace ns) {
        if (childVersion == null) return null;
        Element temp = rootNode.getChild("properties", ns);
        String version = childVersion.substring(childVersion.indexOf("{") + 1, childVersion.indexOf("}"));
        try {
            return temp.getChild(version, ns).getValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * If the dependency version is not specified int the POM file, the program will search the coordinate on Maven
     * central library to fetch the first available version.
     *
     * @param childGroupId    target group id of a dependency
     * @param childArtifactId target artifact id of a dependency
     * @return returns first available version on Maven Central Library. returns null if it does not exists.
     */
    static String searchOnlineVersion(String childGroupId, String childArtifactId) {
        try {
            SAXBuilder sax = new SAXBuilder();
            String searchURL = Util.getSearchURL() + Util.getSearchPath(childArtifactId, childGroupId);
            URL url = new URL(searchURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            Document doc = sax.build(String.valueOf(uri));
            Element rootNode = doc.getRootElement();
            Element responses = rootNode.getChild("result");
            if (responses != null) {
                return fetchVersion(responses);
            }
        } catch (IOException | URISyntaxException | JDOMException e) {
            Logger.getAnonymousLogger().severe(e.getCause().getMessage());
        }

        // If no version is found, just return null;
        return null;
    }

    /**
     * Fetch available version by finding the first version in "str name='v'" element
     *
     * @param responses response of the Http request that is in the format of a Pom file
     * @return returns the available version string. returns null if it does not exists.
     */
    public static String fetchVersion(Element responses) {
        String childVersion = null;
        for (Element target : responses.getChildren()) {
            for (Element attr : target.getChildren()) {
                // Pick the first element who has "name='v" attribute.
                if (attr.getAttribute("name") != null && attr.getAttribute("name").getValue().equals("v")) {
                    childVersion = attr.getValue();
                    break;
                }
            }
            // If version is found, break the loop.
            if (childVersion != null) break;
        }
        return childVersion;
    }

    /**
     * Helper methods for checking if ArtifactId is a variable
     *
     * @param childArtifactId target artifact id
     * @return a boolean value indicating if the input is valid
     */
    public static boolean checkArtifactId(String childArtifactId) {
        return childArtifactId.equals("${pom.artifactId}")
                || childArtifactId.equals("${project.artifactId}")
                || childArtifactId.equals("${pom/artifactId}")
                || childArtifactId.equals("${project/artifactId}");
    }

    /**
     * Helper methods for checking if GroupId is a variable
     *
     * @param childGroupId target group id
     * @return a boolean value indicating if the input is valid
     */
    public static boolean checkGroupId(String childGroupId) {
        return childGroupId.equals("${pom.groupId}")
                || childGroupId.equals("${project.groupId}")
                || childGroupId.equals("${project/groupId}")
                || childGroupId.equals("${pom/groupId}");
    }

    /**
     * Helper methods for checking if Version string is a variable
     *
     * @param version target version
     * @return a boolean value indicating if the input is valid
     */
    public static boolean checkVersion(Element version) {
        return version.getValue().equals("${pom.version}")
                || version.getValue().equals("${project.version}")
                || version.getValue().equals("${pom/version}")
                || version.getValue().equals("${project/version}");
    }

    /**
     * Get an Artifact containing all version info. The mechanism implemented here searches for "-" signs in the
     * artifact name. Depending on whether one or two dashes are found, the supposed central-artifact name (containing
     * all version numbers) is constructed and searched for. If no such artifact was found this method returns null and
     * has no further effect.
     *
     * @param target as the artifact to search a central version artifact file for.
     * @return the artifact containing all version info, if it esists. Null otherwise.
     */
    public static Artifact getCentralArtifact(Artifact target) {
        String groupId = target.getGroupId();
        String artifactId = target.getArtifactId();
        String version = target.getVersion();

        // If the artifact has one dash, append "-dependencies"
        // For cases like "hst-client". The artifact is "hst-client-dependencies".
        String CentralArtifactId = artifactId + "-dependencies";
        Artifact resultArtifact = new Artifact(groupId, CentralArtifactId, version);
        if (artifactExists(resultArtifact)) {
            return resultArtifact;
        }

        // It the artifact has no dashes, return null
        int indexOfDash = artifactId.indexOf('-');
        if (indexOfDash == -1)
            return null;

        // If the artifact has two dashes, replace the 3rd word by "dependencies".
        // For cases like "spring-boot-starter". The artifact is "spring-boot-dependencies".
        indexOfDash = artifactId.indexOf('-', indexOfDash + 1);
        if (indexOfDash == -1)
            return null;
        CentralArtifactId = artifactId.substring(0, indexOfDash) + "-dependencies";
        resultArtifact.setArtifactId(CentralArtifactId);
        if (artifactExists(resultArtifact)) {
            return resultArtifact;
        }
        return null;
    }

    /**
     * This helper method check if a given artifact exists on Maven Central Library
     *
     * @param resultArtifact a given artifact
     * @return if it exists, return true
     */
    private static boolean artifactExists(Artifact resultArtifact) {
        if (parseDoc(Util.getPomURL(), resultArtifact) != null) return true;
        if (parseDocReversely(Util.getPomURL(), resultArtifact) != null) return true;
        return false;
    }


}

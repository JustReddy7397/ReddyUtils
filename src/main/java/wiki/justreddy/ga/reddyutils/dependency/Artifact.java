package wiki.justreddy.ga.reddyutils.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Artifact class represents the logical artifact coordinates of maven artifact. It contains the coordinates
 * (groupId, artifactId, and version) of an artifact.
 *
 * @author Yizhong Ding
 */
public class Artifact {
    private String groupId;
    private String artifactId;
    private String version;
    private List<Artifact> dependencies;
    private List<Artifact> exclusions;

    /**
     * Constructor of Artifact class taking an Artifact coordinates as parameters.
     *
     * @param groupId    the group id of the Artifact
     * @param artifactId the artifact id of the Artifact
     * @param version    the version of the Artifact
     */
    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependencies = new ArrayList<>();
    }

    /**
     * Getter for group id
     *
     * @return returns the group id of this Artifact
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Setter for group id
     *
     * @param groupId new group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Getter for artifact id
     *
     * @return returns the artifact id of this Artifact
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Setter for artifactId
     *
     * @param artifactId new artifact id
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Getter for version
     *
     * @return returns the version string of this Artifact
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter for version
     *
     * @param version new version string
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Getter for dependencies
     *
     * @return returns the dependencies (a List of Artifacts) of this Artifact
     */
    public List<Artifact> getDependencies() {
        return dependencies;
    }

    /**
     * Setter for dependencies
     *
     * @param dependencies new dependencies List
     */
    public void setDependencies(List<Artifact> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Getter for exclusions
     *
     * @return returns the exclusions (a List of Artifacts) of this Artifact
     */
    public List<Artifact> getExclusions() {
        return exclusions;
    }

    /**
     * Setter for exclusions
     *
     * @param exclusions new exclusions List
     */
    public void setExclusions(List<Artifact> exclusions) {
        this.exclusions = exclusions;
    }

    /**
     * Equals method compares the value of two Artifacts. Note: The override equals method only checks if values of
     * groupId and artifactId of two Artifacts are equal. Motivation: Maven does not pull the same artifact twice. If
     * already collected (in a different version) it should be considered covered, and not get re-downloaded.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(groupId, artifact.groupId) &&
                Objects.equals(artifactId, artifact.artifactId);
    }

    /**
     * This method compares if the value of two Artifacts are exactly same. This provides a more advanced comparison
     *      than the equals method, since this method also checks the artifact version (usually not a criteria for artifact
     *      comparison).
     * @param target target artifact to compare with.
     * @return true if they are same artifacts.
     */
    public boolean isSame(Artifact target) {
        return this.artifactId.equals(target.getArtifactId())
                && this.groupId.equals(target.getGroupId())
                && this.version.equals(target.getVersion());
    }

    /**
     * Override hashCode method.
     */
    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    /**
     * Override toString method. Output string would join groupId, artifactId, and version with ":" sign.
     */
    @Override
    public String toString() {
        return groupId + ':' + artifactId + ':' + version;
    }
}

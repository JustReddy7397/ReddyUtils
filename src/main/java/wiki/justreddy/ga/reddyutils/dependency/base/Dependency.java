package wiki.justreddy.ga.reddyutils.dependency.base;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import wiki.justreddy.ga.reddyutils.dependency.util.Urls;

public final class Dependency {


    private final String name, version, groupId, artifactId;

    private final DOptions options;

    private Dependency parent = null;

    public Dependency( String name,  String version,  String groupId,  String artifactId, String customRepo, boolean alwaysUpdate) {
        this.name = name;
        this.version = version;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.options = new DOptions(Urls.fixUrl(customRepo), alwaysUpdate);
    }

    public Dependency(String name, String version, String groupId, String artifactId) {
        this(name, version, groupId, artifactId, "", false);
    }


    public String getName() {
        return name;
    }


    public String getVersion() {
        return version;
    }


    public String getGroupId() {
        return groupId;
    }


    public String getArtifactId() {
        return artifactId;
    }


    public DOptions getOptions() {
        return options;
    }

    public Dependency getParent() {
        return parent;
    }

    public void setParent(Dependency parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public int getParentDepth() {
        int depth = 0;

        Dependency parent = getParent();
        while (parent != null) {
            parent = parent.getParent();
            depth++;
        }

        return depth;
    }

    public String getJarName() {
        return getArtifactId() + "-" + getVersion() + ".jar";
    }

    public String getPomName() {
        return getArtifactId() + "-" + getVersion() + ".pom";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dependency)) return false;
        Dependency that = (Dependency) o;
        return Objects.equal(getVersion(), that.getVersion()) &&
                Objects.equal(getGroupId(), that.getGroupId()) &&
                Objects.equal(getArtifactId(), that.getArtifactId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getVersion(), getGroupId(), getArtifactId());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("version", version)
                .add("groupId", groupId)
                .add("artifactId", artifactId)
                .toString();
    }

}

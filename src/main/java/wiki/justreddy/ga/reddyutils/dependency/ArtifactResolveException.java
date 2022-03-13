package wiki.justreddy.ga.reddyutils.dependency;

/**
 * Custom exception that can be thrown if anything goes wrong during artifact resolving.
 */
public class ArtifactResolveException extends Exception {

    /**
     * Constructor for ArtifactResolveExcption. Invoces parent class (Exception).
     *
     * @param cause reason for raised exception.
     */
    public ArtifactResolveException(String cause) {
        super(cause);
    }
}

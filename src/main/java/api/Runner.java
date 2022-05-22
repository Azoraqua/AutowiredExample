package api;

public interface Runner {

    /**
     * Scanning the desired class(es) for beans & injects.
     *
     * @return True if the Bean-compliant methods & Inject-compliant fields are found otherwise false.
     */
    boolean scan();

    /**
     * Processing the found beans & injects.
     *
     * @return True if the designated fields are successfully injected otherwise false.
     */
    boolean process();

    void cleanup();
}

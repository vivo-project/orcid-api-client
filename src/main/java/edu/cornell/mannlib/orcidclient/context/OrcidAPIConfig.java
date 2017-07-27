package edu.cornell.mannlib.orcidclient.context;

public class OrcidAPIConfig {
    public static OrcidAPIConfig API_1_2 = new OrcidAPIConfig(false, "v1.2");
    public static OrcidAPIConfig API_2_0 = new OrcidAPIConfig(false, "v2.0");

    public static OrcidAPIConfig SANDBOX_1_2 = new OrcidAPIConfig(true, "v1.2");
    public static OrcidAPIConfig SANDBOX_2_0 = new OrcidAPIConfig(true, "v2.0");

    public final String VERSION;
    public final String PUBLIC_URL;
    public final String MEMBER_URL;
    public final String OAUTH_URL;
    public final String TOKEN_URL;

    private OrcidAPIConfig(
            boolean sandbox,
            String version
    ) {
        this.VERSION = version;
        if (sandbox) {
            this.PUBLIC_URL = "https://pub.sandbox.orcid.org/" + version;
            this.MEMBER_URL = "https://api.sandbox.orcid.org/" + version;
            this.OAUTH_URL = "https://sandbox.orcid.org/oauth/authorize";
            this.TOKEN_URL = "https://api.sandbox.orcid.org/oauth/token";
        } else {
            this.PUBLIC_URL = "https://pub.orcid.org/" + version;
            this.MEMBER_URL = "https://api.orcid.org/" + version;
            this.OAUTH_URL = "https://orcid.org/oauth/authorize";
            this.TOKEN_URL = "https://api.orcid.org/oauth/token";
        }
    }
}

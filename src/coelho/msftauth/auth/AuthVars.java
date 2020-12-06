package coelho.msftauth.auth;

public class AuthVars {

	public static final AuthVars MINECRAFT = new AuthVars("00000000402b5328", "service::user.auth.xboxlive.com::MBI_SSL");

	private String clientId;
	private String scope;

	public AuthVars(String clientId, String scope) {
		this.clientId = clientId;
		this.scope = scope;
	}

	public String getClientId() {
		return this.clientId;
	}

	public String getScope() {
		return this.scope;
	}

}

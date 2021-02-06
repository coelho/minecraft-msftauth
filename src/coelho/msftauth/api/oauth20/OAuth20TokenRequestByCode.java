package coelho.msftauth.api.oauth20;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import com.google.gson.annotations.SerializedName;

public class OAuth20TokenRequestByCode extends APIRequest<OAuth20Token> {

	@SerializedName("client_id")
	private String clientId;
	@SerializedName("code")
	private String code;
	@SerializedName("code_verifier")
	private String codeVerifier;
	@SerializedName("grant_type")
	private String grantType;
	@SerializedName("redirect_uri")
	private String redirectURI;
	@SerializedName("scope")
	private String scope;

	public OAuth20TokenRequestByCode(String clientId, String code, String codeVerifier, String scope) {
		this.clientId = clientId;
		this.code = code;
		this.codeVerifier = codeVerifier;
		this.grantType = "authorization_code";
		this.redirectURI = OAuth20Util.REDIRECT_URI;
		this.scope = scope;
	}

	public String getClientId() {
		return this.clientId;
	}

	public String getCode() {
		return this.code;
	}

	public String getCodeVerifier() {
		return this.codeVerifier;
	}

	public String getGrantType() {
		return this.grantType;
	}

	public String getRedirectURI() {
		return this.redirectURI;
	}

	public String getScope() {
		return this.scope;
	}

	@Override
	public String getHttpURL() {
		return "https://login.live.com/oauth20_token.srf";
	}

	@Override
	public APIEncoding getRequestEncoding() {
		return APIEncoding.QUERY;
	}

	@Override
	public APIEncoding getResponseEncoding() {
		return APIEncoding.JSON;
	}

	@Override
	public Class<OAuth20Token> getResponseClass() {
		return OAuth20Token.class;
	}

}

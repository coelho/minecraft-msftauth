package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import com.google.gson.annotations.SerializedName;

public class XboxUserAuthRequest extends APIRequest<XboxToken> {

	private static class Properties {
		@SerializedName("AuthMethod")
		public String authMethod;
		@SerializedName("SiteName")
		public String siteName;
		@SerializedName("RpsTicket")
		public String rpsTicket;
	}

	@SerializedName("RelyingParty")
	private String relyingParty;
	@SerializedName("TokenType")
	private String tokenType;
	@SerializedName("Properties")
	private Properties properties = new Properties();

	public XboxUserAuthRequest(String relyingParty, String tokenType, String authMethod, String siteName, String rpsTicket) {
		this.relyingParty = relyingParty;
		this.tokenType = tokenType;
		this.properties.authMethod = authMethod;
		this.properties.siteName = siteName;
		this.properties.rpsTicket = rpsTicket;
	}

	public String getRelyingParty() {
		return this.relyingParty;
	}

	public String getTokenType() {
		return this.tokenType;
	}

	public String getAuthMethod() {
		return this.properties.authMethod;
	}

	public String getSiteName() {
		return this.properties.siteName;
	}

	public String getRpsTicket() {
		return this.properties.rpsTicket;
	}

	@Override
	public String getHttpURL() {
		return "https://user.auth.xboxlive.com/user/authenticate";
	}

	@Override
	public APIEncoding getRequestEncoding() {
		return APIEncoding.JSON;
	}

	@Override
	public APIEncoding getResponseEncoding() {
		return APIEncoding.JSON;
	}

	@Override
	public Class<XboxToken> getResponseClass() {
		return XboxToken.class;
	}

}

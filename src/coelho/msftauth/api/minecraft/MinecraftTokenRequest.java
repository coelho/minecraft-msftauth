package coelho.msftauth.api.minecraft;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import coelho.msftauth.api.xbox.XboxToken;
import com.google.gson.annotations.SerializedName;

public class MinecraftTokenRequest extends APIRequest<MinecraftToken> {

	private transient XboxToken token;
	@SerializedName("identityToken")
	private String identityToken;

	public MinecraftTokenRequest(XboxToken token) {
		this.token = token;
		this.identityToken = token.toIdentityToken();
	}

	public XboxToken getToken() {
		return this.token;
	}

	public String getIdentityToken() {
		return this.identityToken;
	}

	@Override
	public String getHttpURL() {
		return "https://api.minecraftservices.com/authentication/login_with_xbox";
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
	public Class<MinecraftToken> getResponseClass() {
		return MinecraftToken.class;
	}

}

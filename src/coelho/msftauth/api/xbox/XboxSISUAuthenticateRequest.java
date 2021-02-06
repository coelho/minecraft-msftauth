package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import coelho.msftauth.api.oauth20.OAuth20Util;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpRequest;

import java.util.Collections;
import java.util.List;

public class XboxSISUAuthenticateRequest extends APIRequest<XboxSISUAuthenticate> {

	private static class Query {
		@SerializedName("code_challenge")
		private String codeChallenge;
		@SerializedName("code_challenge_method")
		private String codeChallengeMethod;
		@SerializedName("state")
		private String state;
	}

	@SerializedName("AppId")
	private String appId;
	@SerializedName("DeviceToken")
	private String deviceToken;
	private transient XboxDeviceKey deviceKey;
	@SerializedName("Offers")
	private List<String> offers;
	@SerializedName("Query")
	private Query query = new Query();
	@SerializedName("RedirectUri")
	private String redirectURI;
	@SerializedName("Sandbox")
	private String sandbox;
	@SerializedName("TokenType")
	private String tokenType;

	public XboxSISUAuthenticateRequest(String appId, XboxDevice device, String offer, String codeChallenge, String codeChallengeMethod, String state, String sandbox) {
		this.appId = appId;
		this.deviceKey = device.getKey();
		this.deviceToken = device.getToken().getToken();
		this.offers = Collections.singletonList(offer);
		this.query.codeChallenge = codeChallenge;
		this.query.codeChallengeMethod = codeChallengeMethod;
		this.query.state = state;
		this.redirectURI = OAuth20Util.REDIRECT_URI;
		this.sandbox = sandbox;
		this.tokenType = "code";
	}

	@Override
	public void applyHeader(HttpRequest request) {
		request.setHeader("x-xbl-contract-version", "1");
		this.deviceKey.sign(request);
	}

	@Override
	public String getHttpURL() {
		return "https://sisu.xboxlive.com/authenticate";
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
	public Class<XboxSISUAuthenticate> getResponseClass() {
		return XboxSISUAuthenticate.class;
	}

}

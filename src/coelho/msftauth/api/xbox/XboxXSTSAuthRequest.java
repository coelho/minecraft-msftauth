package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpRequest;

import java.util.Collections;
import java.util.List;

public class XboxXSTSAuthRequest extends APIRequest<XboxToken> {

	private static class Properties {
		@SerializedName("SandboxId")
		public String sandboxId;
		@SerializedName("UserTokens")
		public List<String> userTokens;
		@SerializedName("DeviceToken")
		public String deviceToken;
		@SerializedName("TitleToken")
		public String titleToken;
	}

	@SerializedName("RelyingParty")
	private String relyingParty;
	@SerializedName("TokenType")
	private String tokenType;
	@SerializedName("Properties")
	private Properties properties = new Properties();
	private transient XboxDeviceKey deviceKey;

	public XboxXSTSAuthRequest(String relyingParty, String tokenType, String sandboxId, XboxToken userToken) {
		this(relyingParty, tokenType, sandboxId, Collections.singletonList(userToken));
	}

	public XboxXSTSAuthRequest(String relyingParty, String tokenType, String sandboxId, XboxToken userToken, XboxToken titleToken, XboxDevice device) {
		this(relyingParty, tokenType, sandboxId, Collections.singletonList(userToken), titleToken, device);
	}

	public XboxXSTSAuthRequest(String relyingParty, String tokenType, String sandboxId, List<XboxToken> userTokens) {
		this(relyingParty, tokenType, sandboxId, userTokens, null, null);
	}

	public XboxXSTSAuthRequest(String relyingParty, String tokenType, String sandboxId, List<XboxToken> userTokens, XboxToken titleToken, XboxDevice device) {
		this.relyingParty = relyingParty;
		this.tokenType = tokenType;
		this.properties.sandboxId = sandboxId;
		this.properties.userTokens = Lists.transform(userTokens, XboxToken::getToken);
		if (titleToken != null) {
			this.properties.titleToken = titleToken.getToken();
		}
		if (device != null) {
			this.properties.deviceToken = device.getToken().getToken();
			this.deviceKey = device.getKey();
		}
	}

	@Override
	public void applyHeader(HttpRequest request) {
		request.setHeader("x-xbl-contract-version", "1");
		if (this.deviceKey != null) {
			this.deviceKey.sign(request);
		}
	}

	public String getRelyingParty() {
		return this.relyingParty;
	}

	public String getTokenType() {
		return this.tokenType;
	}

	public String getSandboxId() {
		return this.properties.sandboxId;
	}

	public List<String> getUserTokens() {
		return Collections.unmodifiableList(this.properties.userTokens);
	}

	@Override
	public String getHttpURL() {
		return "https://xsts.auth.xboxlive.com/xsts/authorize";
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

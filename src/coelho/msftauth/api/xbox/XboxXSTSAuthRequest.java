package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class XboxXSTSAuthRequest extends APIRequest<XboxToken> {

	private static class Properties {
		@SerializedName("SandboxId")
		public String sandboxId;
		@SerializedName("UserTokens")
		public List<String> userTokens;
	}

	@SerializedName("RelyingParty")
	private String relyingParty;
	@SerializedName("TokenType")
	private String tokenType;
	@SerializedName("Properties")
	private Properties properties = new Properties();

	public XboxXSTSAuthRequest(String relyingParty, String tokenType, String sandboxId, XboxToken userToken) {
		this(relyingParty, tokenType, sandboxId, Collections.singletonList(userToken));
	}

	public XboxXSTSAuthRequest(String relyingParty, String tokenType, String sandboxId, List<XboxToken> userTokens) {
		this.relyingParty = relyingParty;
		this.tokenType = tokenType;
		this.properties.sandboxId = sandboxId;
		this.properties.userTokens = Lists.transform(userTokens, XboxToken::getToken);
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

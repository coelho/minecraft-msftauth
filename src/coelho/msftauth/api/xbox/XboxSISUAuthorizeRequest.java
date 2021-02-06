package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import coelho.msftauth.api.oauth20.OAuth20Token;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpRequest;

public class XboxSISUAuthorizeRequest extends APIRequest<XboxSISUAuthorize> {

	@SerializedName("AccessToken")
	private String accessToken;
	@SerializedName("AppId")
	private String appId;
	@SerializedName("DeviceToken")
	private String deviceToken;
	@SerializedName("ProofKey")
	private XboxProofKey proofKey;
	@SerializedName("Sandbox")
	private String sandbox;
	@SerializedName("SessionId")
	private String sessionId;
	@SerializedName("SiteName")
	private String siteName;
	private transient XboxDeviceKey deviceKey;

	public XboxSISUAuthorizeRequest(OAuth20Token token, String appId, XboxDevice device, String sandbox, String sessionId, String siteName) {
		this.accessToken = "t=" + token.getAccessToken();
		this.appId = appId;
		this.deviceToken = device.getToken().getToken();
		this.proofKey = device.getProofKey();
		this.sandbox = sandbox;
		this.sessionId = sessionId;
		this.siteName = siteName;
		this.deviceKey = device.getKey();
	}

	@Override
	public void applyHeader(HttpRequest request) {
		request.setHeader("x-xbl-contract-version", "1");
		this.deviceKey.sign(request);
	}

	@Override
	public String getHttpURL() {
		return "https://sisu.xboxlive.com/authorize";
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
	public Class<XboxSISUAuthorize> getResponseClass() {
		return XboxSISUAuthorize.class;
	}

}

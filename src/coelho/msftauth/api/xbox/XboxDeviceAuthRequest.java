package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpRequest;

import java.util.UUID;

public class XboxDeviceAuthRequest extends APIRequest<XboxDeviceToken> {

	private static class Properties {
		@SerializedName("AuthMethod")
		public String authMethod;
		@SerializedName("Id")
		public String id;
		@SerializedName("DeviceType")
		public String deviceType;
		@SerializedName("Version")
		public String version;
		@SerializedName("ProofKey")
		public XboxProofKey proofKey;
	}

	@SerializedName("RelyingParty")
	private String relyingParty;
	@SerializedName("TokenType")
	private String tokenType;
	@SerializedName("Properties")
	private Properties properties = new Properties();
	private transient XboxDeviceKey deviceKey;

	public XboxDeviceAuthRequest(String relyingParty, String tokenType, String deviceType, String version, XboxDeviceKey key) {
		this.relyingParty = relyingParty;
		this.tokenType = tokenType;
		this.properties.authMethod = "ProofOfPossession";
		this.properties.id = "{" + key.getId() + "}";
		this.properties.deviceType = deviceType;
		this.properties.version = version;
		this.properties.proofKey = key.getProofKey();
		this.deviceKey = key;
	}

	@Override
	public void applyHeader(HttpRequest request) {
		request.setHeader("x-xbl-contract-version", "1");
		this.deviceKey.sign(request);
	}

	@Override
	public String getHttpURL() {
		return "https://device.auth.xboxlive.com/device/authenticate";
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
	public Class<XboxDeviceToken> getResponseClass() {
		return XboxDeviceToken.class;
	}

}

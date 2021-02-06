package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIResponseExt;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpResponse;

public class XboxSISUAuthenticate implements APIResponseExt {

	private transient String sessionId;
	@SerializedName("MsaOauthRedirect")
	private String msaOauthRedirect;
	@SerializedName("MsaRequestParameters")
	private JsonObject msaRequestParameters;

	public XboxSISUAuthenticate(String msaOauthRedirect, JsonObject msaRequestParameters) {
		this.msaOauthRedirect = msaOauthRedirect;
		this.msaRequestParameters = msaRequestParameters;
	}

	@Override
	public void applyResponse(HttpResponse response) {
		this.sessionId = response.getFirstHeader("X-SessionId").getValue();
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public String getMsaOauthRedirect() {
		return this.msaOauthRedirect;
	}

	public JsonObject getMsaRequestParameters() {
		return this.msaRequestParameters;
	}

}

package coelho.msftauth.api.xbox;

import coelho.msftauth.api.APIResponseExt;
import coelho.msftauth.api.oauth20.OAuth20Util;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.Arrays;
import java.util.Collections;

public class XboxSISUAuthorize implements APIResponseExt {

	private transient String sessionId;
	@SerializedName("AuthorizationToken")
	private XboxToken authorizationToken;
	@SerializedName("DeviceToken")
	private String deviceToken;
	@SerializedName("Sandbox")
	private String sandbox;
	@SerializedName("TitleToken")
	private XboxToken titleToken;
	@SerializedName("UserToken")
	private XboxToken userToken;
	@SerializedName("WebPage")
	private String webPage;

	public XboxSISUAuthorize(XboxToken authorizationToken, String deviceToken, String sandbox, XboxToken titleToken, XboxToken userToken, String webPage) {
		this.authorizationToken = authorizationToken;
		this.deviceToken = deviceToken;
		this.sandbox = sandbox;
		this.titleToken = titleToken;
		this.userToken = userToken;
		this.webPage = webPage;
	}

	@Override
	public void applyResponse(HttpResponse response) {
		this.sessionId = response.getFirstHeader("X-SessionId").getValue();
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public XboxToken getAuthorizationToken() {
		return this.authorizationToken;
	}

	public String getDeviceToken() {
		return this.deviceToken;
	}

	public String getSandbox() {
		return this.sandbox;
	}

	public XboxToken getTitleToken() {
		return this.titleToken;
	}

	public XboxToken getUserToken() {
		return this.userToken;
	}

	public String getWebPage() {
		return this.webPage;
	}

	public String getRedirectURL(String sessionId) {
		return this.webPage + "?" + URLEncodedUtils.format(Arrays.asList(
				new BasicNameValuePair("redirect", OAuth20Util.REDIRECT_URI),
				new BasicNameValuePair("sid", sessionId)
		), "UTF-8");
	}

}

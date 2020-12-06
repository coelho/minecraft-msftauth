package coelho.msftauth.api.oauth20;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OAuth20Authorize {

	private String url;
	private String clientId;
	private String responseType;
	private String redirectURI;
	private String scope;

	public OAuth20Authorize(String clientId, String responseType, String scope) {
		this.clientId = clientId;
		this.responseType = responseType;
		this.redirectURI = OAuth20Util.REDIRECT_URI;
		this.scope = scope;
		List<NameValuePair> queryParams = new ArrayList<>();
		queryParams.add(new BasicNameValuePair("client_id", this.clientId));
		queryParams.add(new BasicNameValuePair("response_type", this.responseType));
		queryParams.add(new BasicNameValuePair("scope", this.scope));
		queryParams.add(new BasicNameValuePair("redirect_uri", this.redirectURI));
		this.url = OAuth20Util.AUTHORIZE_URI + "?" + URLEncodedUtils.format(queryParams, StandardCharsets.UTF_8);
	}

	public String getURL() {
		return this.url;
	}

	public String getClientId() {
		return this.clientId;
	}

	public String getResponseType() {
		return this.responseType;
	}

	public String getScope() {
		return this.scope;
	}

}

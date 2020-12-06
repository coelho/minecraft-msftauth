package coelho.msftauth.api.oauth20;

import com.google.common.base.Preconditions;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OAuth20Desktop {

	private String code;
	private String lc;

	public OAuth20Desktop(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		List<NameValuePair> queryParams = URLEncodedUtils.parse(url.getQuery(), StandardCharsets.UTF_8);
		for (NameValuePair queryParam : queryParams) {
			switch (queryParam.getName()) {
				case "code":
					this.code = queryParam.getValue();
					break;
				case "lc":
					this.lc = queryParam.getValue();
					break;
			}
		}
		Preconditions.checkNotNull(this.code);
	}

	public String getCode() {
		return this.code;
	}

	public String getLc() {
		return this.lc;
	}

}

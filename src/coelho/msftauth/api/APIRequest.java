package coelho.msftauth.api;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public abstract class APIRequest<R> {

	public R request() throws Exception {
		RequestConfig clientConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(10_000)
				.setConnectTimeout(10_000)
				.setSocketTimeout(10_000)
				.build();
		try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(clientConfig).build()) {
			HttpUriRequest request;
			if (this.getRequestEncoding() != null) {
				HttpPost post = new HttpPost(this.getHttpURL());
				this.getRequestEncoding().encode(post, this);
				request = post;
			} else {
				request = new HttpGet(this.getHttpURL());
			}
			if (this.getHttpAuthorization() != null) {
				request.setHeader("Authorization", this.getHttpAuthorization());
			}
			try (CloseableHttpResponse response = client.execute(request)) {
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new IllegalStateException("status code: " + response.getStatusLine().getStatusCode());
				}
				return this.getResponseEncoding().decode(response, this.getResponseClass());
			}
		}
	}

	public abstract String getHttpURL();

	public String getHttpAuthorization() {
		return null;
	}

	public abstract APIEncoding getRequestEncoding();

	public abstract APIEncoding getResponseEncoding();

	public abstract Class<R> getResponseClass();

}

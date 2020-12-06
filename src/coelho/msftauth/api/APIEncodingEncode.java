package coelho.msftauth.api;

import org.apache.http.client.methods.HttpPost;

public interface APIEncodingEncode {

	void encode(HttpPost post, Object object);

}

package coelho.msftauth.api;

import coelho.msftauth.util.GsonUtil;
import com.google.common.io.CharStreams;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum APIEncoding implements APIEncodingEncode, APIEncodingDecode {

	QUERY {
		@Override
		public void encode(HttpPost post, Object object) {
			String json = GsonUtil.GSON.toJson(object);
			Map<String, String> formMap = GsonUtil.GSON.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
			List<NameValuePair> form = new ArrayList<>();
			for (Map.Entry<String, String> formEntry : formMap.entrySet()) {
				form.add(new BasicNameValuePair(formEntry.getKey(), formEntry.getValue()));
			}
			post.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
		}

		@Override
		public <T> T decode(HttpResponse response, Class<T> objectClass) {
			throw new UnsupportedOperationException();
		}
	},

	JSON {
		@Override
		public void encode(HttpPost post, Object object) {
			String json = GsonUtil.GSON.toJson(object);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
		}

		@Override
		public <T> T decode(HttpResponse response, Class<T> objectClass) throws IOException {
			return GsonUtil.GSON.fromJson(new InputStreamReader(response.getEntity().getContent()), objectClass);
		}
	},

	;

}

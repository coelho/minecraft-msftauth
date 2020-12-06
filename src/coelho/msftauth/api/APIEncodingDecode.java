package coelho.msftauth.api;

import org.apache.http.HttpResponse;

import java.io.IOException;

public interface APIEncodingDecode {

	<T> T decode(HttpResponse response, Class<T> objectClass) throws IOException;

}

package coelho.msftauth.api;

import org.apache.http.HttpResponse;

public interface APIResponseExt {

	default void applyResponse(HttpResponse response) {

	}

}

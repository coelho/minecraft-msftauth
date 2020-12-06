package coelho.msftauth.api.minecraft;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;

public class MinecraftEntitlementsRequest extends APIRequest<MinecraftEntitlements> {

	private MinecraftToken token;

	public MinecraftEntitlementsRequest(MinecraftToken token) {
		this.token = token;
	}

	public MinecraftToken getToken() {
		return this.token;
	}

	@Override
	public String getHttpURL() {
		return "https://api.minecraftservices.com/entitlements/mcstore";
	}

	@Override
	public String getHttpAuthorization() {
		return this.token.toHttpAuthorization();
	}

	@Override
	public APIEncoding getRequestEncoding() {
		return null; // GET
	}

	@Override
	public APIEncoding getResponseEncoding() {
		return APIEncoding.JSON;
	}

	@Override
	public Class<MinecraftEntitlements> getResponseClass() {
		return MinecraftEntitlements.class;
	}

}

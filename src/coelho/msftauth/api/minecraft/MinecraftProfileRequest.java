package coelho.msftauth.api.minecraft;

import coelho.msftauth.api.APIEncoding;
import coelho.msftauth.api.APIRequest;
import com.google.gson.annotations.SerializedName;

public class MinecraftProfileRequest extends APIRequest<MinecraftProfile> {

	@SerializedName("token")
	private MinecraftToken token;

	public MinecraftProfileRequest(MinecraftToken token) {
		this.token = token;
	}

	public MinecraftToken getToken() {
		return this.token;
	}

	@Override
	public String getHttpURL() {
		return "https://api.minecraftservices.com/minecraft/profile";
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
	public Class<MinecraftProfile> getResponseClass() {
		return MinecraftProfile.class;
	}

}

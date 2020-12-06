package coelho.msftauth.api.minecraft;

import com.google.gson.annotations.SerializedName;

public class MinecraftToken {

	@SerializedName("username")
	private String uuid;
	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("token_type")
	private String tokenType;
	@SerializedName("expires_in")
	private long expiresIn;

	// TODO roles ? - may not be important for now, always empty it seems

	public MinecraftToken(String uuid, String accessToken, String tokenType, long expiresIn) {
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getTokenType() {
		return this.tokenType;
	}

	public long getExpiresIn() {
		return this.expiresIn;
	}

	public String toHttpAuthorization() {
		return "Bearer " + this.accessToken;
	}

}

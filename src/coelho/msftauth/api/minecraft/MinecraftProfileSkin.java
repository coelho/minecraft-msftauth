package coelho.msftauth.api.minecraft;

import com.google.gson.annotations.SerializedName;

public class MinecraftProfileSkin {

	@SerializedName("id")
	private String uuid;
	private String alias;
	private String state;
	private String url;
	private String variant;

	public MinecraftProfileSkin(String uuid, String alias, String state, String url, String variant) {
		this.uuid = uuid;
		this.alias = alias;
		this.state = state;
		this.url = url;
		this.variant = variant;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getAlias() {
		return this.alias;
	}

	public String getState() {
		return this.state;
	}

	public String getUrl() {
		return this.url;
	}

	public String getVariant() {
		return this.variant;
	}

}

package coelho.msftauth.api.minecraft;

public class MinecraftEntitlement {

	private String name;
	private String signature;

	public MinecraftEntitlement(String name, String signature) {
		this.name = name;
		this.signature = signature;
	}

	public String getName() {
		return this.name;
	}

	public String getSignature() {
		return this.signature;
	}

}

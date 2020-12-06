package coelho.msftauth.auth;

import coelho.msftauth.api.minecraft.MinecraftEntitlements;
import coelho.msftauth.api.minecraft.MinecraftEntitlementsRequest;
import coelho.msftauth.api.minecraft.MinecraftProfile;
import coelho.msftauth.api.minecraft.MinecraftProfileRequest;
import coelho.msftauth.api.minecraft.MinecraftToken;
import coelho.msftauth.api.minecraft.MinecraftTokenRequest;
import coelho.msftauth.api.oauth20.OAuth20Authorize;
import coelho.msftauth.api.oauth20.OAuth20Desktop;
import coelho.msftauth.api.oauth20.OAuth20Token;
import coelho.msftauth.api.oauth20.OAuth20TokenRequestByCode;
import coelho.msftauth.api.oauth20.OAuth20Util;
import coelho.msftauth.api.xbox.XboxToken;
import coelho.msftauth.api.xbox.XboxUserAuthRequest;
import coelho.msftauth.api.xbox.XboxXSTSAuthRequest;
import coelho.msftauth.util.GsonUtil;

public class Auther {

	private AuthVars vars;
	private boolean debug;

	private OAuth20Authorize oauth20Authorize;
	private OAuth20Desktop oauth20Desktop;
	private OAuth20Token oauth20Token;
	private XboxToken xboxUserToken;
	private XboxToken xboxXSTSToken;
	private MinecraftToken minecraftToken;
	private MinecraftProfile minecraftProfile;
	private MinecraftEntitlements minecraftEntitlements;

	public Auther(AuthVars vars, boolean debug) {
		this.vars = vars;
		this.debug = debug;
	}

	public void process() throws AuthException {
		try {
			this.oauth20Authorize = new OAuth20Authorize(this.vars.getClientId(), "code", this.vars.getScope());
			debug("oauth20Authorize", this.oauth20Authorize);

			AuthWebView webView = AuthWebView.open(this.oauth20Authorize.getURL());
			this.oauth20Desktop = new OAuth20Desktop(webView.waitForURL(OAuth20Util.REDIRECT_URI));
			debug("oauth20Desktop", this.oauth20Desktop);

			this.oauth20Token = new OAuth20TokenRequestByCode(this.vars.getClientId(), this.oauth20Desktop.getCode(), this.vars.getScope()).request();
			debug("oauth20Token", this.oauth20Token);

			this.xboxUserToken = new XboxUserAuthRequest("http://auth.xboxlive.com", "JWT", "RPS", "user.auth.xboxlive.com", this.oauth20Token.getAccessToken()).request();
			debug("xboxUserToken", this.xboxUserToken);

			this.xboxXSTSToken = new XboxXSTSAuthRequest("rp://api.minecraftservices.com/", "JWT", "RETAIL", this.xboxUserToken).request();
			debug("xboxXSTSToken", this.xboxXSTSToken);

			this.minecraftToken = new MinecraftTokenRequest(this.xboxXSTSToken).request();
			debug("minecraftToken", this.minecraftToken);

			this.minecraftProfile = new MinecraftProfileRequest(this.minecraftToken).request();
			debug("minecraftProfile", this.minecraftProfile);

			this.minecraftEntitlements = new MinecraftEntitlementsRequest(this.minecraftToken).request();
			debug("minecraftEntitlements", this.minecraftEntitlements);
		} catch(InterruptedException exception) {
			Thread.currentThread().interrupt();
		} catch(Exception exception) {
			throw new AuthException(exception);
		}
	}

	public void debug(String key, Object value) {
		if (this.debug) {
			System.out.println(key + ": " + GsonUtil.GSON.toJson(value));
		}
	}

	public OAuth20Authorize getOauth20Authorize() {
		return this.oauth20Authorize;
	}

	public OAuth20Desktop getOauth20Desktop() {
		return this.oauth20Desktop;
	}

	public OAuth20Token getOauth20Token() {
		return this.oauth20Token;
	}

	public XboxToken getXboxUserToken() {
		return this.xboxUserToken;
	}

	public XboxToken getXboxXSTSToken() {
		return this.xboxXSTSToken;
	}

	public MinecraftToken getMinecraftToken() {
		return this.minecraftToken;
	}

	public MinecraftProfile getMinecraftProfile() {
		return this.minecraftProfile;
	}

	public MinecraftEntitlements getMinecraftEntitlements() {
		return this.minecraftEntitlements;
	}

}

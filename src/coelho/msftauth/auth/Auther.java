package coelho.msftauth.auth;

import coelho.msftauth.api.minecraft.MinecraftEntitlements;
import coelho.msftauth.api.minecraft.MinecraftEntitlementsRequest;
import coelho.msftauth.api.minecraft.MinecraftProfile;
import coelho.msftauth.api.minecraft.MinecraftProfileRequest;
import coelho.msftauth.api.minecraft.MinecraftToken;
import coelho.msftauth.api.minecraft.MinecraftTokenRequest;
import coelho.msftauth.api.oauth20.OAuth20Desktop;
import coelho.msftauth.api.oauth20.OAuth20Token;
import coelho.msftauth.api.oauth20.OAuth20TokenRequestByCode;
import coelho.msftauth.api.oauth20.OAuth20Util;
import coelho.msftauth.api.xbox.XboxDevice;
import coelho.msftauth.api.xbox.XboxDeviceAuthRequest;
import coelho.msftauth.api.xbox.XboxDeviceToken;
import coelho.msftauth.api.xbox.XboxDeviceKey;
import coelho.msftauth.api.xbox.XboxSISUAuthenticate;
import coelho.msftauth.api.xbox.XboxSISUAuthenticateRequest;
import coelho.msftauth.api.xbox.XboxSISUAuthorize;
import coelho.msftauth.api.xbox.XboxSISUAuthorizeRequest;
import coelho.msftauth.api.xbox.XboxToken;
import coelho.msftauth.api.xbox.XboxXSTSAuthRequest;
import coelho.msftauth.util.GsonUtil;
import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;

public class Auther {

	private AuthVars vars;
	private boolean debug;

	private XboxDeviceKey xboxDeviceKey;
	private XboxDeviceToken xboxDeviceToken;
	private XboxDevice xboxDevice;
	private XboxSISUAuthenticate xboxSISUAuthenticate;
	private XboxSISUAuthorize xboxSISUAuthorize;
	private OAuth20Desktop xboxSISUDesktop;
	private OAuth20Desktop oauth20Desktop;
	private OAuth20Token oauth20Token;
	private XboxToken xboxXSTSToken;
	private MinecraftToken minecraftToken;
	private MinecraftProfile minecraftProfile;
	private MinecraftEntitlements minecraftEntitlements;

	public Auther(AuthVars vars, XboxDeviceKey xboxDeviceKey, boolean debug) {
		this.vars = vars;
		this.debug = debug;
		this.xboxDeviceKey = xboxDeviceKey;
	}

	public void process() throws AuthException {
		try {
			this.xboxDeviceKey = new XboxDeviceKey();
			this.xboxDeviceToken = new XboxDeviceAuthRequest("http://auth.xboxlive.com", "JWT", "Win32", "10.16.0", this.xboxDeviceKey).request();
			this.xboxDevice = new XboxDevice(this.xboxDeviceKey, this.xboxDeviceToken);
			debug("xboxDeviceToken", this.xboxDeviceToken);

			byte[] challengeBytes = new byte[32];
			new SecureRandom().nextBytes(challengeBytes);
			String challenge = Hex.encodeHexString(challengeBytes);

			this.xboxSISUAuthenticate = new XboxSISUAuthenticateRequest(this.vars.getClientId(), this.xboxDevice, this.vars.getScope(), challenge, "plain", "", "RETAIL").request();
			debug("xboxSISUAuthenticate", this.xboxSISUAuthenticate);

			AuthWebView webView = AuthWebView.open(this.xboxSISUAuthenticate.getMsaOauthRedirect());
			String webViewURL = webView.waitForURL(OAuth20Util.REDIRECT_URI);
			if (webViewURL == null) {
				debug("oauth20Desktop", "cancelled");
				//process(); // test multiple sessions
				return;
			}

			this.oauth20Desktop = new OAuth20Desktop(webViewURL);
			debug("oauth20Desktop", this.oauth20Desktop);

			this.oauth20Token = new OAuth20TokenRequestByCode(this.vars.getClientId(), this.oauth20Desktop.getCode(), challenge, this.vars.getScope()).request();
			debug("oauth20Token", this.oauth20Token);

			debug("request", new XboxSISUAuthorizeRequest(this.oauth20Token, this.vars.getClientId(), this.xboxDevice, "RETAIL", this.xboxSISUAuthenticate.getSessionId(), "user.auth.xboxlive.com"));

			this.xboxSISUAuthorize = new XboxSISUAuthorizeRequest(this.oauth20Token, this.vars.getClientId(), this.xboxDevice, "RETAIL", this.xboxSISUAuthenticate.getSessionId(), "user.auth.xboxlive.com").request();
			debug("xboxSISUAuthorize", this.xboxSISUAuthorize);

			//
			/*
			-- unnecessary
			// shows the client's current gamer tag and allows them to switch, but "sig" field in Redirect URI is not clear how to handle

			String xboxSISUAuthorizeRedirect = this.xboxSISUAuthorize.getRedirectURL(this.xboxSISUAuthenticate.getSessionId());
			debug("xboxSISUAuthorizeRedirect", xboxSISUAuthorizeRedirect);

			webView = AuthWebView.open(xboxSISUAuthorizeRedirect);
			webViewURL = webView.waitForURL(OAuth20Util.REDIRECT_URI);
			if (webViewURL == null) {
				debug("xboxSISUAuthorizeRedirect", "cancelled");
				//process(); // test multiple sessions
				return;
			}

			this.xboxSISUDesktop = new OAuth20Desktop(webViewURL);
			debug("xboxSISUDesktop", this.xboxSISUDesktop);
			if (!"success".equals(this.xboxSISUDesktop.getStatus())) {
				debug("xboxSISUAuthorizeRedirect", "cancelled");
			}
			*/

			this.xboxXSTSToken = new XboxXSTSAuthRequest("rp://api.minecraftservices.com/", "JWT", "RETAIL", this.xboxSISUAuthorize.getUserToken(), this.xboxSISUAuthorize.getTitleToken(), this.xboxDevice).request();
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

	public XboxDeviceKey getXboxDeviceKey() {
		return this.xboxDeviceKey;
	}

	public XboxDeviceToken getXboxDeviceToken() {
		return this.xboxDeviceToken;
	}

	public XboxDevice getXboxDevice() {
		return this.xboxDevice;
	}

	public XboxSISUAuthenticate getXboxSISUAuthenticate() {
		return this.xboxSISUAuthenticate;
	}

	public XboxSISUAuthorize getXboxSISUAuthorize() {
		return this.xboxSISUAuthorize;
	}

	public OAuth20Desktop getXboxSISUDesktop() {
		return this.xboxSISUDesktop;
	}

	public OAuth20Desktop getOauth20Desktop() {
		return this.oauth20Desktop;
	}

	public OAuth20Token getOauth20Token() {
		return this.oauth20Token;
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

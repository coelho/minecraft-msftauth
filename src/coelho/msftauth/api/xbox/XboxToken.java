package coelho.msftauth.api.xbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class XboxToken {

	@SerializedName("IssueInstant")
	private String issueInstant;
	@SerializedName("NotAfter")
	private String notAfter;
	@SerializedName("Token")
	private String token;
	@SerializedName("DisplayClaims")
	private Map<String, List<Map<String, String>>> displayClaims;

	public XboxToken(String issueInstant, String notAfter, String token, Map<String, List<Map<String, String>>> displayClaims) {
		this.issueInstant = issueInstant;
		this.notAfter = notAfter;
		this.token = token;
		this.displayClaims = displayClaims;
	}

	public String getIssueInstant() {
		return this.issueInstant;
	}

	public String getNotAfter() {
		return this.notAfter;
	}

	public String getToken() {
		return this.token;
	}

	public Map<String, List<Map<String, String>>> getDisplayClaims() {
		return this.displayClaims;
	}

	public String toIdentityToken() {
		return "XBL3.0 x=" + this.displayClaims.get("xui").get(0).get("uhs") + ";" + this.token;
	}

}

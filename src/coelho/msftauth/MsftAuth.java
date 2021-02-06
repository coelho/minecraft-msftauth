package coelho.msftauth;

import coelho.msftauth.api.xbox.XboxDeviceKey;
import coelho.msftauth.auth.AuthException;
import coelho.msftauth.auth.AuthVars;
import coelho.msftauth.auth.Auther;

public class MsftAuth {

	public static void main(String[] args) {
		try {
			new Auther(AuthVars.MINECRAFT, new XboxDeviceKey(), true).process();
			System.exit(0);
		} catch(AuthException exception) {
			exception.printStackTrace();
		}
	}

}

package coelho.msftauth.util;


import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;

import java.security.SecureRandom;

public class ECDSAUtil {

	public static AsymmetricCipherKeyPair generateKey() {
		ECKeyPairGenerator generator = new ECKeyPairGenerator();
		X9ECParameters curve = SECNamedCurves.getByName("secp256r1");
		ECDomainParameters params = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
		ECKeyGenerationParameters keyGenParam = new ECKeyGenerationParameters(params, new SecureRandom());
		generator.init(keyGenParam);
		return generator.generateKeyPair();
	}

}

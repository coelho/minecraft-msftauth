package coelho.msftauth.api.xbox;

import coelho.msftauth.util.ECDSAUtil;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.JCEECPrivateKey;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class XboxDeviceKey {

	private String id;
	private AsymmetricCipherKeyPair keyPair;
	private ECPublicKeyParameters publicKey;
	private ECPrivateKeyParameters privateKey;
	private XboxProofKey proofKey;

	public XboxDeviceKey() {
		this.id = UUID.randomUUID().toString();
		this.keyPair = ECDSAUtil.generateKey();
		this.publicKey = (ECPublicKeyParameters) this.keyPair.getPublic();
		this.privateKey = (ECPrivateKeyParameters) this.keyPair.getPrivate();
		this.proofKey = new XboxProofKey(this);
	}

	public XboxDeviceKey(String encoded) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(encoded));
		this.id = reader.readLine();
		this.publicKey = (ECPublicKeyParameters) PublicKeyFactory.createKey(Base64.getDecoder().decode(reader.readLine()));
		this.privateKey = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(Base64.getDecoder().decode(reader.readLine()));
		this.keyPair = new AsymmetricCipherKeyPair(this.publicKey, this.privateKey);
		this.proofKey = new XboxProofKey(this);
	}

	public String encode() throws IOException {
		StringWriter writer = new StringWriter();
		SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.publicKey);
		PrivateKeyInfo privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.privateKey);
		writer.write(this.id);
		writer.write("\n");
		writer.write(Base64.getEncoder().encodeToString(publicKeyInfo.toASN1Primitive().getEncoded()));
		writer.write("\n");
		writer.write(Base64.getEncoder().encodeToString(privateKeyInfo.toASN1Primitive().getEncoded()));
		writer.write("\n");
		return writer.toString();
	}

	public void sign(HttpRequest request) {
		try {
			if (!(request instanceof HttpPost)) {
				throw new IllegalArgumentException("request is not post");
			}

			HttpPost post = (HttpPost) request;
			Header authHeader = post.getFirstHeader("Authorization");
			String query = post.getURI().getRawQuery();
			if (query == null) {
				query = "";
			}
			byte[] uri = (post.getURI().getPath() + query).getBytes(StandardCharsets.US_ASCII);
			byte[] auth = authHeader == null ? new byte[]{} : authHeader.getValue().getBytes(StandardCharsets.US_ASCII);
			byte[] method = post.getMethod().getBytes(StandardCharsets.US_ASCII);
			byte[] body = ByteStreams.toByteArray(post.getEntity().getContent());
			long time = (Instant.now().getEpochSecond() + 11644473600L) * 10000000L;

			ByteBuffer buffer = ByteBuffer.allocate(body.length + 256);
			buffer.order(ByteOrder.BIG_ENDIAN);
			// Signature policy version (0, 0, 0, 1) + 0 byte.
			buffer.put(new byte[]{0, 0, 0, 1, 0});
			// Timestamp
			buffer.putLong(time);
			buffer.put((byte) 0);
			// HTTP Method
			buffer.put(method);
			buffer.put((byte) 0);
			// HTTP URI
			buffer.put(uri);
			buffer.put((byte) 0);
			// HTTP Authorization
			buffer.put(auth);
			buffer.put((byte) 0);
			// Body
			buffer.put(body);
			buffer.put((byte) 0);
			//
			buffer.flip();

			SHA256Digest digest = new SHA256Digest();
			digest.update(buffer.array(), 0, buffer.remaining());
			byte[] digestBytes = new byte[digest.getDigestSize()];
			digest.doFinal(digestBytes, 0);

			ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA512Digest()));
			signer.init(true, this.privateKey);
			BigInteger[] signature = signer.generateSignature(digestBytes);

			buffer.clear();
			buffer.order(ByteOrder.BIG_ENDIAN);
			buffer.put(new byte[] {0, 0, 0, 1});
			buffer.putLong(time);
			buffer.put(bigIntegerToByteArray(signature[0].abs()));
			buffer.put(bigIntegerToByteArray(signature[1].abs()));
			buffer.flip();

			String signatureHeader = Base64.getEncoder().encodeToString(Arrays.copyOf(buffer.array(), buffer.remaining()));
			request.setHeader("Signature", signatureHeader);
		} catch(Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private static byte[] bigIntegerToByteArray(BigInteger bigInteger) {
		byte[] array = bigInteger.toByteArray();
		if (array[0] == 0) {
			byte[] newArray = new byte[array.length - 1];
			System.arraycopy(array, 1, newArray, 0, newArray.length);
			return newArray;
		}
		return array;
	}

	public String getId() {
		return this.id;
	}

	public String getCrv() {
		return "P-256";
	}

	public String getAlg() {
		return "ES256";
	}

	public String getUse() {
		return "sig";
	}

	public String getKty() {
		return "EC";
	}

	public AsymmetricCipherKeyPair getKeyPair() {
		return this.keyPair;
	}

	public ECPublicKeyParameters getPublicKey() {
		return this.publicKey;
	}

	public byte[] getPublicXBytes() {
		return bigIntegerToByteArray(this.publicKey.getQ().getAffineXCoord().toBigInteger());
	}

	public byte[] getPublicYBytes() {
		return bigIntegerToByteArray(this.publicKey.getQ().getAffineYCoord().toBigInteger());
	}

	public ECPrivateKeyParameters getPrivateKey() {
		return this.privateKey;
	}

	public XboxProofKey getProofKey() {
		return this.proofKey;
	}

}

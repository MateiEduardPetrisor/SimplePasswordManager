package encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	private static Encryption instance;
	private String pwd;
	private byte[] Iv;
	public static final String SECURITY_PROVIDER = "BC";
	public static final String FILE_SIG = "P.W.D.M.G.R.V.1";
	private static final String ENCRYPTION_ALGORITHM = "AES";
	private static final String ENCRYPTION_ALGORITHM_MODE = "GCM";
	private static final String ENCRYPTION_ALGORITHM_PADDING = "NoPadding";
	private static final String DIGEST_ALGORITHM = "SHA3-512";
	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 16;

	public static Encryption getInstance() {
		if (Encryption.instance == null) {
			synchronized (Encryption.class) {
				Encryption.instance = new Encryption();
			}
		}
		return Encryption.instance;
	}

	private Encryption() {
		super();
		this.pwd = null;
		this.Iv = null;
	}

	public void configure(String pPwd, String pIv) {
		this.Iv = pIv.getBytes();
		this.pwd = pPwd;
	}

	public byte[] generateInitializationVector() {
		byte IV[] = new byte[GCM_IV_LENGTH];
		SecureRandom random = new SecureRandom();
		random.nextBytes(IV);
		return IV;
	}

	private String generateAlgorithmSettings() {
		String algorithmSettings = ENCRYPTION_ALGORITHM + "/" + ENCRYPTION_ALGORITHM_MODE + "/"
				+ ENCRYPTION_ALGORITHM_PADDING;
		return algorithmSettings;
	}

	public String hashPassword(String password) throws NoSuchAlgorithmException, NoSuchProviderException {
		MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM, SECURITY_PROVIDER);
		messageDigest.update(password.getBytes());
		byte[] pwdHash = messageDigest.digest();
		String result = encode(new String(pwdHash));
		return result;
	}

	public String encode(String text) {
		return new String(Base64.getEncoder().encode(text.getBytes()));
	}

	public String decode(String text) {
		return new String(Base64.getDecoder().decode(text.getBytes()));
	}

	public String encrypt(String text)
			throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String alg = this.generateAlgorithmSettings();
		Cipher cipher = Cipher.getInstance(alg, SECURITY_PROVIDER);
		Key key = new SecretKeySpec(this.pwd.getBytes(), ENCRYPTION_ALGORITHM);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, this.Iv);
		SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
		byte res[] = cipher.doFinal(text.getBytes());
		String result = new String(Base64.getEncoder().encode(res));
		return result;
	}

	public String decrypt(String text)
			throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String alg = this.generateAlgorithmSettings();
		byte[] enc = Base64.getDecoder().decode(text.getBytes());
		Cipher cipher = Cipher.getInstance(alg, SECURITY_PROVIDER);
		Key key = new SecretKeySpec(this.pwd.getBytes(), ENCRYPTION_ALGORITHM);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, this.Iv);
		SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
		byte res[] = cipher.doFinal(enc);
		String result = new String(res);
		return result;
	}
}
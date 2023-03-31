package io.github.sinri.carina.helper.encryption.aes;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * @since 2.8
 */
public interface CarinaAes {
    /**
     * 密钥算法
     */
    String KEY_ALGORITHM = "AES";
    /**
     * 编码格式
     */
    String ENCODING = "utf-8";

    static CarinaAes create(SupportedCipherAlgorithm cipherAlgorithm, String key) {
        Objects.requireNonNull(cipherAlgorithm);
        Objects.requireNonNull(key);
        switch (cipherAlgorithm) {
            case AesCbcPkcs7Padding:
                return new CarinaAesCbcPkcs7Padding(key);
            case AesCbcPkcs5Padding:
                return new CarinaAesCbcPkcs5Padding(key);
            case AesEcbPkcs5Padding:
                return new CarinaAesEcbPkcs5Padding(key);
            case AesEcbPkcs7Padding:
                return new CarinaAesEcbPkcs7Padding(key);
        }
        return null;
    }

    /**
     * 加密/解密算法 / 工作模式 / 填充方式
     * Java 6支持PKCS5Padding填充方式
     * Bouncy Castle支持PKCS7Padding填充方式
     */
    SupportedCipherAlgorithm getCipherAlgorithm();

    String encrypt(String source);

    String decrypt(String encryptStr);

    default SecretKey generateSecretKeyWithKeySize(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        //keyGenerator.init(256);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    default String generate128BitsSecretKey() throws NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(generateSecretKeyWithKeySize(128).getEncoded());
    }

    default String generate192BitsSecretKey() throws NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(generateSecretKeyWithKeySize(192).getEncoded());
    }

    default String generate256BitsSecretKey() throws NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(generateSecretKeyWithKeySize(256).getEncoded());
    }

    enum SupportedCipherAlgorithm {
        AesCbcPkcs5Padding("AES/CBC/PKCS5Padding"),
        AesCbcPkcs7Padding("AES/CBC/PKCS7Padding"),
        AesEcbPkcs5Padding("AES/ECB/PKCS5Padding"),
        AesEcbPkcs7Padding("AES/ECB/PKCS7Padding"),
        ;

        private final String expression;

        SupportedCipherAlgorithm(String expression) {
            this.expression = expression;
        }

        public String getExpression() {
            return expression;
        }
    }
}

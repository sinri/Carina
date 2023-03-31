package io.github.sinri.carina.helper.encryption.aes;

/**
 * @since 2.8
 */
abstract public class CarinaAesUsingPkcs5Padding extends CarinaAesBase {
    /**
     * @param key AES要求密钥长度为128位或192位或256位，java默认限制AES密钥长度最多128位
     */
    public CarinaAesUsingPkcs5Padding(String key) {
        super(key);
    }
}

package io.github.sinri.carina.helper;

import io.github.sinri.carina.helper.encryption.aes.CarinaAes;

/**
 * @since 2.8
 */
public class CarinaCryptographyHelper {
    private static final CarinaCryptographyHelper instance = new CarinaCryptographyHelper();

    private CarinaCryptographyHelper() {

    }

    static CarinaCryptographyHelper getInstance() {
        return instance;
    }

    public CarinaAes aes(CarinaAes.SupportedCipherAlgorithm cipherAlgorithm, String key) {
        return CarinaAes.create(cipherAlgorithm, key);
    }
}

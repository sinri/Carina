package io.github.sinri.carina.helper;

import io.github.sinri.carina.helper.authenticator.googleauth.GoogleAuthenticator;
import io.github.sinri.carina.helper.authenticator.googleauth.GoogleAuthenticatorConfig;
import io.github.sinri.carina.helper.encryption.bcrypt.BCrypt;
import io.vertx.core.Handler;

/**
 * @since 2.9.4
 */
public class CarinaAuthenticationHelper {
    private static final CarinaAuthenticationHelper instance = new CarinaAuthenticationHelper();

    private CarinaAuthenticationHelper() {

    }

    static CarinaAuthenticationHelper getInstance() {
        return instance;
    }

    /**
     * @since 2.8
     * @since 2.9.4 moved from digest to authentication
     */
    public String php_password_hash(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * @since 2.8
     * @since 2.9.4 moved from digest to authentication
     */
    public boolean php_password_verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    /**
     * To create an instance of Google Authenticator with default config of window size 1.
     *
     * @since 2.9.4
     */
    public GoogleAuthenticator getGoogleAuthenticator() {
        return getGoogleAuthenticator(configBuilder -> configBuilder.setWindowSize(1));
    }

    /**
     * To create an instance of Google Authenticator with certain config,
     *
     * @since 2.9.4
     */
    public GoogleAuthenticator getGoogleAuthenticator(Handler<GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder> configBuildHandler) {
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
        if (configBuildHandler != null) {
            configBuildHandler.handle(configBuilder);
        }
        return new GoogleAuthenticator(configBuilder.build());
    }

//    public String createSecretForTOTP(GoogleAuthenticator googleAuthenticator) {
//        return googleAuthenticator.createCredentials().getKey();
//    }
//
//    public int generateTOTP(GoogleAuthenticator googleAuthenticator, String shared_secret) {
//        return googleAuthenticator.getTotpPassword(shared_secret);
//    }
//
//    public int generateTOTP(GoogleAuthenticator googleAuthenticator, String shared_secret, long time) {
//        return googleAuthenticator.getTotpPassword(shared_secret, time);
//    }
//
//    public boolean validate(GoogleAuthenticator googleAuthenticator, String shared_secret, int totp) {
//        return googleAuthenticator.authorize(shared_secret, totp);
//    }

}

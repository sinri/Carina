package io.github.sinri.carina.helper;

/**
 * @since 3.0.0
 * 使用此类可以实现无需启动 VERTX 即可使用 HELPER。
 */
public interface CarinaHelpers {

    static CarinaBinaryHelper binaryHelper() {
        return CarinaBinaryHelper.getInstance();
    }

    static CarinaDateTimeHelper datetimeHelper() {
        return CarinaDateTimeHelper.getInstance();
    }

    static CarinaFileHelper fileHelper() {
        return CarinaFileHelper.getInstance();
    }

    static CarinaJsonHelper jsonHelper() {
        return CarinaJsonHelper.getInstance();
    }

    static CarinaNetHelper netHelper() {
        return CarinaNetHelper.getInstance();
    }

    static CarinaReflectionHelper reflectionHelper() {
        return CarinaReflectionHelper.getInstance();
    }

    static CarinaStringHelper stringHelper() {
        return CarinaStringHelper.getInstance();
    }

    static CarinaCryptographyHelper cryptographyHelper() {
        return CarinaCryptographyHelper.getInstance();
    }

    static CarinaDigestHelper digestHelper() {
        return CarinaDigestHelper.getInstance();
    }

    /**
     * @since 2.9.3
     */
    static CarinaRuntimeHelper runtimeHelper() {
        return CarinaRuntimeHelper.getInstance();
    }

    /**
     * @since 2.9.4
     */
    static CarinaAuthenticationHelper authenticationHelper() {
        return CarinaAuthenticationHelper.getInstance();
    }

}

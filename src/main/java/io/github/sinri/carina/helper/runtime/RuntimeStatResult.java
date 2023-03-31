package io.github.sinri.carina.helper.runtime;

import io.vertx.core.json.JsonObject;

/**
 * @param <T>
 * @since 2.9.4
 */
public interface RuntimeStatResult<T> {
    long getStatTime();

    T since(T start);

    JsonObject toJsonObject();
}

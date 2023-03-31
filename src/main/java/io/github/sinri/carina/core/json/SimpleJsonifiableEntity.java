package io.github.sinri.carina.core.json;

import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * @since 2.7
 */
public class SimpleJsonifiableEntity implements JsonifiableEntity<SimpleJsonifiableEntity> {
    protected JsonObject jsonObject;

    /**
     * @since 2.8 jsonObject initialized
     */
    public SimpleJsonifiableEntity() {
        this.jsonObject = new JsonObject();
    }

    public SimpleJsonifiableEntity(JsonObject jsonObject) {
        reloadDataFromJsonObject(jsonObject);
    }

    @Override
    public @NotNull JsonObject toJsonObject() {
        return jsonObject;
    }

    /**
     * @since 2.8 allow jsonObject as null (treated as empty json object)
     */
    @Override
    public @NotNull SimpleJsonifiableEntity reloadDataFromJsonObject(JsonObject jsonObject) {
        if (jsonObject == null) {
            this.jsonObject = new JsonObject();
        } else {
            this.jsonObject = jsonObject;
        }
        return this;
    }

    /**
     * @since 2.8
     */
    @Override
    public String toString() {
        JsonObject x = toJsonObject();
        return x.toString();
    }

    @Override
    public SimpleJsonifiableEntity copy() {
        SimpleJsonifiableEntity copied = new SimpleJsonifiableEntity();
        JsonObject copiedJsonObject = this.toJsonObject().copy();
        copied.reloadDataFromJsonObject(copiedJsonObject);
        return copied;
    }
}

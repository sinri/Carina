package io.github.sinri.carina.logger.event;

import io.github.sinri.carina.logger.CarinaLogLevel;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class CarinaEventLogImpl implements CarinaEventLog {
    private JsonObject jsonObject;
    private long timestamp;
    private String topic;
    private CarinaLogLevel level;

    public CarinaEventLogImpl(CarinaLogLevel level, String topic) {
        this.jsonObject = new JsonObject();
        this.timestamp(System.currentTimeMillis());
        this.level(level);
        this.topic(topic);
    }

    @NotNull
    @Override
    public JsonObject toJsonObject() {
        return jsonObject;
    }

    @NotNull
    @Override
    public CarinaEventLog reloadDataFromJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    @Override
    @Deprecated
    public CarinaEventLog context(String key, Object value) {
        JsonObject context = this.jsonObject.getJsonObject(CarinaEventLog.RESERVED_KEY_CONTEXT);
        if (context == null) {
            context = new JsonObject();
            this.jsonObject.put(CarinaEventLog.RESERVED_KEY_CONTEXT, context);
        }
        context.put(key, value);
        return this;
    }

    @Override
    @Deprecated
    public Object context(String key) {
        return this.readValue(CarinaEventLog.RESERVED_KEY_CONTEXT, key);
    }

    @Override
    public CarinaEventLog put(String key, Object value) {
        this.jsonObject.put(key, value);
        return this;
    }

    @Override
    public Object get(String key) {
        return this.jsonObject.getValue(key);
    }

    @Override
    public CarinaEventLog timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public long timestamp() {
        return this.timestamp;
    }

    @Override
    public CarinaEventLog level(CarinaLogLevel level) {
        this.level = level;
        return this;
        //return put(RESERVED_KEY_LEVEL, level.name());
    }

    @Override
    public CarinaLogLevel level() {
        return level;
        //return KeelLogLevel.valueOf(readString(RESERVED_KEY_LEVEL));
    }

    @Override
    public CarinaEventLog topic(String topic) {
        this.topic = topic;
        return this;
    }

    @Override
    public String topic() {
        return this.topic;
    }

    @Override
    public CarinaEventLog message(String msg) {
        return put(RESERVED_KEY_EVENT_MSG, msg);
    }

    @Override
    public String message() {
        return readString(RESERVED_KEY_EVENT_MSG);
    }

    @Override
    public String toString() {
        return timestampExpression() + " [" + level() + "] " + toJsonObject();
    }
}

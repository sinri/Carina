package io.github.sinri.carina.logger.event;

import io.github.sinri.carina.core.json.JsonifiableEntity;
import io.github.sinri.carina.helper.CarinaHelpers;
import io.github.sinri.carina.logger.CarinaLogLevel;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.Objects;

public interface CarinaEventLog extends JsonifiableEntity<CarinaEventLog> {
    @Deprecated
    String RESERVED_KEY_CONTEXT = "context";
    String RESERVED_KEY_EVENT_MSG = "msg";
    String RESERVED_KEY_EVENT_EXCEPTION = "exception";
    //    String RESERVED_KEY_TIMESTAMP = "timestamp";
    String RESERVED_KEY_LEVEL = "level";
//    String RESERVED_KEY_THREAD_ID = "thread_id";
//    String RESERVED_KEY_CLUSTER_NODE_ID = "cluster_node_id";
//    String RESERVED_KEY_CLUSTER_NODE_ADDRESS = "cluster_node_address";

//    String RESERVED_KEY_TOPIC = "topic";

    static CarinaEventLog create(CarinaLogLevel level, String topic) {
        return new CarinaEventLogImpl(level, topic);
    }

    static Future<String> render(CarinaEventLog eventLog) {
        StringBuilder sb = new StringBuilder();

        String dateExpression = CarinaHelpers.datetimeHelper().getDateExpression(new Date(eventLog.timestamp()), "yyyy-MM-dd HH:mm:ss.SSS");

        sb.append(dateExpression)
                .append(" [").append(eventLog.level()).append("]")
                .append(" <").append(eventLog.topic()).append(">")
                .append(" ").append(eventLog.message())
                .append("\n");
        JsonObject entries = new JsonObject();
        for (String k : eventLog.toJsonObject().fieldNames()) {
            //if (Objects.equals(k, RESERVED_KEY_TIMESTAMP)) continue;
            //if (Objects.equals(k, RESERVED_KEY_LEVEL)) continue;
            if (Objects.equals(k, RESERVED_KEY_EVENT_MSG)) continue;
            entries.put(k, eventLog.toJsonObject().getValue(k));
        }
        sb.append(CarinaHelpers.jsonHelper().renderJsonToStringBlock("entries", entries));
        return Future.succeededFuture(sb.toString());
    }

    @Deprecated
    CarinaEventLog context(String key, Object value);

    @Deprecated
    Object context(String key);

    CarinaEventLog put(String key, Object value);

    Object get(String key);

    CarinaEventLog timestamp(long timestamp);

    long timestamp();

    default String timestampExpression() {
        return timestampExpression("yyyy-MM-dd HH:mm:ss.SSS");
    }

    default String timestampExpression(String format) {
        return CarinaHelpers.datetimeHelper().getDateExpression(new Date(timestamp()), format);
    }

    CarinaEventLog level(CarinaLogLevel level);

    CarinaLogLevel level();

    CarinaEventLog topic(String topic);

    String topic();

    CarinaEventLog message(String msg);

    String message();
}

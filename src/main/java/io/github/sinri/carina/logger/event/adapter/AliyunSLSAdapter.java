package io.github.sinri.carina.logger.event.adapter;

import io.github.sinri.carina.facade.async.CarinaAsyncKit;
import io.github.sinri.carina.helper.CarinaHelpers;
import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.vertx.core.Future;

import java.util.*;

/**
 * @since 3.0.0
 */
public interface AliyunSLSAdapter extends CarinaEventLoggerAdapter {
    static AliyunSLSAdapter create() {
        ServiceLoader<AliyunSLSAdapter> serviceLoader = ServiceLoader.load(AliyunSLSAdapter.class);
        return serviceLoader.iterator().next();
    }

    @Override
    default Future<Void> dealWithLogs(List<CarinaEventLog> buffer) {
        Map<String, List<CarinaEventLog>> topicMap = new HashMap<>();

        buffer.forEach(eventLog -> {
            String topic = eventLog.topic();
            if (topic == null) {
                topic = "";
            }
            topicMap.computeIfAbsent(topic, x -> new ArrayList<>())
                    .add(eventLog);
        });

        return CarinaAsyncKit.iterativelyCall(topicMap.keySet(), topic -> {
            return Future.succeededFuture()
                    .compose(v -> {
                        List<CarinaEventLog> eventLogs = topicMap.get(topic);
                        return dealWithLogsForOneTopic(topic, eventLogs);
                    })
                    .compose(v -> {
                        return Future.succeededFuture();
                    }, throwable -> {
                        return Future.succeededFuture();
                    });
        });
    }

    Future<Void> dealWithLogsForOneTopic(String topic, List<CarinaEventLog> buffer);

    @Override
    default Object processThrowable(Throwable throwable) {
        return CarinaHelpers.jsonHelper().renderThrowableChain(throwable);
    }
}

package io.github.sinri.keel.logger.event.adapter;

import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.github.sinri.keel.helper.KeelHelpers;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.vertx.core.Future;

import java.util.*;

/**
 * @since 3.0.0
 */
public interface AliyunSLSAdapter extends KeelEventLoggerAdapter {
    static AliyunSLSAdapter create() {
        ServiceLoader<AliyunSLSAdapter> serviceLoader = ServiceLoader.load(AliyunSLSAdapter.class);
        Optional<AliyunSLSAdapter> first = serviceLoader.findFirst();
        return first.orElseThrow();
    }

    @Override
    default Future<Void> dealWithLogs(List<KeelEventLog> buffer) {
        Map<String, List<KeelEventLog>> topicMap = new HashMap<>();

        buffer.forEach(eventLog -> {
            String topic = eventLog.topic();
            if (topic == null) {
                topic = "";
            }
            topicMap.computeIfAbsent(topic, x -> new ArrayList<>())
                    .add(eventLog);
        });

        return KeelAsyncKit.iterativelyCall(topicMap.keySet(), topic -> {
            return Future.succeededFuture()
                    .compose(v -> {
                        List<KeelEventLog> keelEventLogs = topicMap.get(topic);
                        return dealWithLogsForOneTopic(topic, keelEventLogs);
                    })
                    .compose(v -> {
                        return Future.succeededFuture();
                    }, throwable -> {
                        return Future.succeededFuture();
                    });
        });
    }

    Future<Void> dealWithLogsForOneTopic(String topic, List<KeelEventLog> buffer);

    @Override
    default Object processThrowable(Throwable throwable) {
        return KeelHelpers.jsonHelper().renderThrowableChain(throwable);
    }
}

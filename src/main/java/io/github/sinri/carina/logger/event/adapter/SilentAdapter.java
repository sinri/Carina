package io.github.sinri.carina.logger.event.adapter;

import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.List;

/**
 * 本类无需Keel实例。
 * 单例模式。
 */
public final class SilentAdapter implements CarinaEventLoggerAdapter {
    private static final SilentAdapter instance = new SilentAdapter();

    private SilentAdapter() {

    }

    public static SilentAdapter getInstance() {
        return instance;
    }

    @Override
    public void close(Promise<Void> promise) {
        promise.complete();
    }

    @Override
    public Future<Void> dealWithLogs(List<CarinaEventLog> buffer) {
        return Future.succeededFuture();
    }

    @Override
    public Object processThrowable(Throwable throwable) {
        return null;
    }
}

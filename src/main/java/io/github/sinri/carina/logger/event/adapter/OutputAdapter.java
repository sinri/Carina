package io.github.sinri.keel.logger.event.adapter;

import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.github.sinri.keel.helper.KeelHelpers;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.List;
import java.util.function.Function;

/**
 * 本类无需Keel实例。
 * 单例模式。
 *
 * @since 3.0.0
 */
public class OutputAdapter implements KeelEventLoggerAdapter {

    private static final OutputAdapter defaultInstance = new OutputAdapter(null);
    private final Function<KeelEventLog, Future<String>> converter;

    private OutputAdapter(Function<KeelEventLog, Future<String>> converter) {
        this.converter = converter;
    }

    public static OutputAdapter getInstance() {
        return defaultInstance;
    }

    public static OutputAdapter getInstance(Function<KeelEventLog, Future<String>> converter) {
        return new OutputAdapter(converter);
    }

    @Override
    public void close(Promise<Void> promise) {
        promise.complete();
    }

    @Override
    public Future<Void> dealWithLogs(List<KeelEventLog> buffer) {
        return KeelAsyncKit.iterativelyCall(buffer, eventLog -> {
            try {
                if (converter == null) {
                    System.out.println(eventLog.toString());
                    return Future.succeededFuture();
                } else {
                    return converter.apply(eventLog)
                            .compose(s -> {
                                System.out.println(s);
                                return Future.succeededFuture();
                            });
                }
            } catch (Throwable e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                return Future.succeededFuture();
            }
        });
    }

    @Override
    public Object processThrowable(Throwable throwable) {
        return KeelHelpers.stringHelper().renderThrowableChain(throwable);
    }
}

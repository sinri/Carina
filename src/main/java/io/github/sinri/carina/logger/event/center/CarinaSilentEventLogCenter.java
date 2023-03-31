package io.github.sinri.carina.logger.event.center;

import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.github.sinri.carina.logger.event.CarinaEventLogCenter;
import io.vertx.core.Future;

public class CarinaSilentEventLogCenter implements CarinaEventLogCenter {
    private final static CarinaSilentEventLogCenter instance = new CarinaSilentEventLogCenter();

    private CarinaSilentEventLogCenter() {

    }

    public static CarinaSilentEventLogCenter getInstance() {
        return instance;
    }

    @Override
    public void log(CarinaEventLog eventLog) {

    }

    @Override
    public Object processThrowable(Throwable throwable) {
        return null;
    }

    @Override
    public Future<Void> gracefullyClose() {
        return Future.succeededFuture();
    }
}

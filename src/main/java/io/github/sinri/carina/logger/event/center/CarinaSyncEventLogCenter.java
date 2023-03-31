package io.github.sinri.carina.logger.event.center;

import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.github.sinri.carina.logger.event.CarinaEventLogCenter;
import io.github.sinri.carina.logger.event.adapter.CarinaEventLoggerAdapter;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

public class CarinaSyncEventLogCenter implements CarinaEventLogCenter {
    private final CarinaEventLoggerAdapter adapter;

    public CarinaSyncEventLogCenter(CarinaEventLoggerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void log(CarinaEventLog eventLog) {
        List<CarinaEventLog> arrayList = new ArrayList<>();
        arrayList.add(eventLog);
        adapter.dealWithLogs(arrayList);
    }

    @Override
    public Object processThrowable(Throwable throwable) {
        return this.adapter.processThrowable(throwable);
    }

    @Override
    public Future<Void> gracefullyClose() {
        return this.adapter.gracefullyClose();
    }
}

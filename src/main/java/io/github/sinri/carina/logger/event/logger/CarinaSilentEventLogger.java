package io.github.sinri.carina.logger.event.logger;

import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.github.sinri.carina.logger.event.CarinaEventLogCenter;
import io.github.sinri.carina.logger.event.CarinaEventLogger;
import io.github.sinri.carina.logger.event.center.CarinaSilentEventLogCenter;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CarinaSilentEventLogger implements CarinaEventLogger {
    private final static CarinaSilentEventLogger instance = new CarinaSilentEventLogger();

    public static CarinaSilentEventLogger getInstance() {
        return instance;
    }

    @Override
    public Supplier<CarinaEventLogCenter> getEventLogCenterSupplier() {
        return CarinaSilentEventLogCenter::getInstance;
    }

    @Override
    public String getPresetTopic() {
        return null;
    }

    @Override
    public Handler<CarinaEventLog> getPresetEventLogEditor() {
        return null;
    }

    @Override
    public CarinaEventLogger setPresetEventLogEditor(Handler<CarinaEventLog> editor) {
        return this;
    }

    @Override
    public void log(@NotNull Handler<CarinaEventLog> eventLogHandler) {
        // keep silent
    }
}

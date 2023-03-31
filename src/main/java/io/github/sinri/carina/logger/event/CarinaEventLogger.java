package io.github.sinri.carina.logger.event;

import io.github.sinri.carina.logger.CarinaLogLevel;
import io.github.sinri.carina.logger.event.logger.CarinaSilentEventLogger;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * @since 2.9.4
 */
public interface CarinaEventLogger {
    static CarinaEventLogger silentLogger() {
        return CarinaSilentEventLogger.getInstance();
    }

    /**
     * Note: it is better to keep log center stable.
     */
    Supplier<CarinaEventLogCenter> getEventLogCenterSupplier();

    /**
     * Note: if `getEventLogCenterSupplier` generate instances dynamically, the default implement would not affect.
     */
    default Future<Void> gracefullyCloseLogCenter() {
        return getEventLogCenterSupplier().get().gracefullyClose();
    }

    String getPresetTopic();

    Handler<CarinaEventLog> getPresetEventLogEditor();

    CarinaEventLogger setPresetEventLogEditor(Handler<CarinaEventLog> editor);


    default void log(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        // done debugging
//        System.out.println("KeelEventLogger::log("+eventLogHandler+") start");

        CarinaEventLog eventLog = CarinaEventLog.create(CarinaLogLevel.INFO, getPresetTopic());

//        System.out.println("KeelEventLogger::log("+eventLogHandler+") eventLog created");

        Handler<CarinaEventLog> presetEventLogEditor = getPresetEventLogEditor();
        if (presetEventLogEditor != null) {
//            System.out.println("KeelEventLogger::log("+eventLogHandler+") presetEventLogEditor is not null");
            presetEventLogEditor.handle(eventLog);
        } else {
//            System.out.println("KeelEventLogger::log("+eventLogHandler+") presetEventLogEditor is null");
        }

//        System.out.println("KeelEventLogger::log("+eventLogHandler+") presetEventLogEditor done");

        eventLogHandler.handle(eventLog);

//        System.out.println("KeelEventLogger::log("+eventLogHandler+") eventLogHandler done");

        getEventLogCenterSupplier().get().log(eventLog);
    }

    default void debug(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        log(eventLog -> {
            eventLog.level(CarinaLogLevel.DEBUG);
            eventLog.topic(getPresetTopic());
            eventLogHandler.handle(eventLog);
        });
    }

    default void info(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        // done debugging
//        System.out.println("KeelEventLogger::info("+eventLogHandler+") start");
        log(eventLog -> {
//            System.out.println("KeelEventLogger::info("+eventLogHandler+") inside handler start");
            eventLog.level(CarinaLogLevel.INFO);
            eventLog.topic(getPresetTopic());
//            System.out.println("KeelEventLogger::info("+eventLogHandler+") inside handler go");
            eventLogHandler.handle(eventLog);
//            System.out.println("KeelEventLogger::info("+eventLogHandler+") inside handler gone");
        });
//        System.out.println("KeelEventLogger::info("+eventLogHandler+") end");
    }

    default void notice(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        log(eventLog -> {
            eventLog.level(CarinaLogLevel.NOTICE);
            eventLog.topic(getPresetTopic());
            eventLogHandler.handle(eventLog);
        });
    }

    default void warning(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        log(eventLog -> {
            eventLog.level(CarinaLogLevel.WARNING);
            eventLog.topic(getPresetTopic());
            eventLogHandler.handle(eventLog);
        });
    }

    default void error(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        log(eventLog -> {
            eventLog.level(CarinaLogLevel.ERROR);
            eventLog.topic(getPresetTopic());
            eventLogHandler.handle(eventLog);
        });
    }

    default void fatal(@Nonnull Handler<CarinaEventLog> eventLogHandler) {
        log(eventLog -> {
            eventLog.level(CarinaLogLevel.FATAL);
            eventLog.topic(getPresetTopic());
            eventLogHandler.handle(eventLog);
        });
    }

    default void debug(String msg) {
        debug(eventLog -> eventLog.message(msg));
    }

    default void info(String msg) {
        // done debugging
//        System.out.println("KeelEventLogger::info("+msg+") start");
        info(eventLog -> eventLog.message(msg));
//        System.out.println("KeelEventLogger::info("+msg+") end");
    }

    default void notice(String msg) {
        notice(eventLog -> eventLog.message(msg));
    }

    default void warning(String msg) {
        warning(eventLog -> eventLog.message(msg));
    }

    default void error(String msg) {
        error(eventLog -> eventLog.message(msg));
    }

    default void fatal(String msg) {
        fatal(eventLog -> eventLog.message(msg));
    }

    default Object processThrowable(@Nonnull Throwable throwable) {
        return getEventLogCenterSupplier().get().processThrowable(throwable);
    }

    default void exception(@Nonnull Throwable throwable) {
        exception(throwable, "Exception Occurred");
    }

    default void exception(@Nonnull Throwable throwable, @Nonnull String msg) {
        exception(throwable, eventLog -> {
            eventLog.message(msg);
        });
    }

    /**
     * @since 3.0.1
     */
    default void exception(@Nonnull Throwable throwable, @Nonnull String msg, @Nullable JsonObject context) {
        exception(throwable, eventLog -> {
            eventLog.message(msg);
            if (context != null) eventLog.put("context", context);
        });
    }

    default void exception(@Nonnull Throwable throwable, @Nonnull Handler<CarinaEventLog> eventLogHandler) {
        error(eventLog -> {
            eventLog.put(CarinaEventLog.RESERVED_KEY_EVENT_EXCEPTION, this.processThrowable(throwable));
            eventLogHandler.handle(eventLog);
        });
    }

    /**
     * @since 3.0.1
     */
    default void debug(String msg, JsonObject context) {
        debug(event -> {
            event.message(msg);
            if (context != null) event.put("context", context);
        });
    }

    /**
     * @since 3.0.1
     */
    default void info(String msg, JsonObject context) {
        info(event -> {
            event.message(msg);
            if (context != null) event.put("context", context);
        });
    }

    /**
     * @since 3.0.1
     */
    default void notice(String msg, JsonObject context) {
        notice(event -> {
            event.message(msg);
            if (context != null) event.put("context", context);
        });
    }

    /**
     * @since 3.0.1
     */
    default void warning(String msg, JsonObject context) {
        warning(event -> {
            event.message(msg);
            if (context != null) event.put("context", context);
        });
    }

    /**
     * @since 3.0.1
     */
    default void error(String msg, JsonObject context) {
        error(event -> {
            event.message(msg);
            if (context != null) event.put("context", context);
        });
    }

    /**
     * @since 3.0.1
     */
    default void fatal(String msg, JsonObject context) {
        fatal(event -> {
            event.message(msg);
            if (context != null) event.put("context", context);
        });
    }
}

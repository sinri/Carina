package io.github.sinri.carina.logger.event;

import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * @since 2.9.4 实验性设计
 */
public interface CarinaEventLogCenter {

    void log(CarinaEventLog eventLog);

    Object processThrowable(Throwable throwable);

    Future<Void> gracefullyClose();


    default CarinaEventLogger createLogger(String presetTopic) {
        return createLogger(presetTopic, null);
    }

    default CarinaEventLogger createLogger(String presetTopic, Handler<CarinaEventLog> editor) {
        return new CarinaEventLoggerImpl(presetTopic, () -> this, editor);
    }

}

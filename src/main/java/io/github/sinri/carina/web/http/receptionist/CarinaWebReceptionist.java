package io.github.sinri.carina.web.http.receptionist;

import io.github.sinri.carina.helper.CarinaHelpers;
import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.github.sinri.carina.logger.event.CarinaEventLogger;
import io.github.sinri.carina.web.http.prehandler.CarinaPlatformHandler;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

/**
 * @since 2.9.2
 * @since 3.0.0 TEST PASSED
 */
public abstract class CarinaWebReceptionist {
    private final RoutingContext routingContext;
    private final CarinaEventLogger logger;

    public CarinaWebReceptionist(RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.logger = createLogger();
        Handler<CarinaEventLog> previous = this.logger.getPresetEventLogEditor();
        this.logger.setPresetEventLogEditor(eventLog -> {
            eventLog
                    .put("request", new JsonObject()
                            .put("request_id", routingContext.get(CarinaPlatformHandler.KEEL_REQUEST_ID))
                            .put("method", routingContext.request().method().name())
                            .put("path", routingContext.request().path())
                            .put("handler", this.getClass().getName())
                    );

            if (previous != null) {
                previous.handle(eventLog);
            }
        });
    }

    protected RoutingContext getRoutingContext() {
        return routingContext;
    }

    abstract protected CarinaEventLogger createLogger();

    public CarinaEventLogger getLogger() {
        return logger;
    }

    abstract public void handle();

    private void respondWithJsonObject(JsonObject resp) {
        try {
            routingContext.json(resp);
        } catch (Throwable throwable) {
            logger.exception(throwable, event -> event
                    .message("RoutingContext has been dealt by others")
                    //.put("request_id", readRequestID())
                    .put("response", new JsonObject()
                            .put("code", routingContext.response().getStatusCode())
                            .put("message", routingContext.response().getStatusMessage())
                            .put("ended", routingContext.response().ended())
                            .put("closed", routingContext.response().closed())
                    )
            );
        }
    }

    protected void respondOnSuccess(Object data) {
        JsonObject resp = new JsonObject()
                .put("code", "OK")
                .put("data", data);
        logger.info(event -> event
                        .message("RESPOND SUCCESS")
                //.put("request_id", readRequestID())
                //.put("response", resp)
        );
        respondWithJsonObject(resp);
    }

    protected void respondOnFailure(Throwable throwable) {
        JsonObject resp = new JsonObject()
                .put("code", "FAILED")
                .put("data", throwable.getMessage());
        String error = CarinaHelpers.stringHelper().renderThrowableChain(throwable);
        resp.put("throwable", error);
        logger.exception(throwable, event -> event
                        .message("RESPOND FAILURE")
                //.put("request_id", readRequestID())
        );
        respondWithJsonObject(resp);
    }

    public String readRequestID() {
        return routingContext.get(CarinaPlatformHandler.KEEL_REQUEST_ID);
    }

    public long readRequestStartTime() {
        return routingContext.get(CarinaPlatformHandler.KEEL_REQUEST_START_TIME);
    }

    public List<String> readRequestIPChain() {
        return CarinaHelpers.netHelper().parseWebClientIPChain(routingContext);
//        return routingContext.get(KeelPlatformHandler.KEEL_REQUEST_CLIENT_IP_CHAIN);
    }

    public User readRequestUser() {
        return routingContext.user();
    }
}

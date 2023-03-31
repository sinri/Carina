package io.github.sinri.carina.web.http.prehandler;

import io.github.sinri.carina.facade.Carina;
import io.github.sinri.carina.helper.CarinaHelpers;
import io.vertx.core.Future;
import io.vertx.core.shareddata.Counter;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.PlatformHandler;

import java.util.Random;
import java.util.UUID;

/**
 * @since 2.9.2
 * @since 3.0.0 TEST PASSED
 */
public class CarinaPlatformHandler implements PlatformHandler {
    public final static String KEEL_REQUEST_ID = "KEEL_REQUEST_ID"; // -> String
    public final static String KEEL_REQUEST_START_TIME = "KEEL_REQUEST_START_TIME"; // -> long * 0.001 second
    //public final static String KEEL_REQUEST_CLIENT_IP_CHAIN = "KEEL_REQUEST_CLIENT_IP_CHAIN"; // -> List<String of IP>


    @Override
    public void handle(RoutingContext routingContext) {
        // BEFORE ASYNC PAUSE
        routingContext.request().pause();
        // START !
        Carina.getVertx().sharedData()
                .getCounter("KeelPlatformHandler-RequestID-Counter")
                .compose(Counter::incrementAndGet)
                .recover(throwable -> {
                    return Future.succeededFuture(new Random().nextLong() * -1);
                })
                .compose(id -> {
                    routingContext.put(KEEL_REQUEST_ID, CarinaHelpers.netHelper().getLocalHostAddress() + "-" + id + "-" + UUID.randomUUID());

                    routingContext.put(KEEL_REQUEST_START_TIME, System.currentTimeMillis());
                    //routingContext.put(KEEL_REQUEST_CLIENT_IP_CHAIN, keel.netHelper().parseWebClientIPChain(routingContext));

                    return Future.succeededFuture();
                })
                .andThen(v -> {
                    // RESUME
                    routingContext.request().resume();
                    // NEXT !
                    routingContext.next();
                });
    }
}

package io.github.sinri.carina.web.http;

import io.github.sinri.carina.facade.Carina;
import io.github.sinri.carina.logger.event.center.CarinaOutputEventLogCenter;
import io.github.sinri.carina.verticles.CarinaVerticleBase;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

abstract public class CarinaHttpServer extends CarinaVerticleBase {
    public static final String CONFIG_HTTP_SERVER_PORT = "http_server_port";
    public static final String CONFIG_HTTP_SERVER_OPTIONS = "http_server_options";
    public static final String CONFIG_IS_MAIN_SERVICE = "is_main_service";
    protected Handler<Promise<Object>> gracefulCloseHandler;
    protected HttpServer server;

    protected int getHttpServerPort() {
        return this.config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    }

    protected HttpServerOptions getHttpServerOptions() {
        JsonObject httpServerOptions = this.config().getJsonObject(CONFIG_HTTP_SERVER_OPTIONS);
        if (httpServerOptions == null) {
            return new HttpServerOptions()
                    .setPort(getHttpServerPort());
        } else {
            return new HttpServerOptions(httpServerOptions);
        }
    }

    protected boolean isMainService() {
        return this.config().getBoolean(CONFIG_IS_MAIN_SERVICE, true);
    }

    protected abstract void configureRoutes(Router router);

//    /**
//     * @since 2.4
//     */
//    public void setWebSocketHandlerToServer(Class<? extends KeelWebSocketHandler> handlerClass, DeploymentOptions deploymentOptions) {
//        // todo 魔改中
//        //this.server.webSocketHandler(websocket -> KeelWebSocketHandler.handle(websocket, handlerClass, getLogger(), deploymentOptions));
//    }

    public CarinaHttpServer setGracefulCloseHandler(Handler<Promise<Object>> gracefulCloseHandler) {
        this.gracefulCloseHandler = gracefulCloseHandler;
        return this;
    }

    @Override
    public void start() throws Exception {
        setLogger(CarinaOutputEventLogCenter.getInstance().createLogger(getClass().getName()));

        this.server = Carina.getVertx().createHttpServer(getHttpServerOptions());

        Router router = Router.router(Carina.getVertx());
        this.configureRoutes(router);

        server.requestHandler(router)
                .exceptionHandler(throwable -> {
                    getLogger().exception(throwable, "KeelHttpServer Exception");
                })
                .listen(httpServerAsyncResult -> {
                    if (httpServerAsyncResult.succeeded()) {
                        HttpServer httpServer = httpServerAsyncResult.result();
                        getLogger().info("HTTP Server Established, Actual Port: " + httpServer.actualPort());

//                        this.getKeel().addClosePrepareHandler(this::terminate);
                    } else {
                        Throwable throwable = httpServerAsyncResult.cause();
                        getLogger().exception(throwable, "Listen failed");

                        if (this.isMainService()) {
                            Carina.gracefullyClose(Promise::complete);
                        }
                    }
                })
        ;
    }

    public void terminate(Promise<Void> promise) {
        server.close(ar -> {
            if (ar.succeeded()) {
                getLogger().info("HTTP Server Closed");
            } else {
                getLogger().error("HTTP Server Closing Failure: " + ar.cause().getMessage());
            }

            if (this.isMainService()) {
                Carina.gracefullyClose(Promise::complete);
            }
        });
    }
}

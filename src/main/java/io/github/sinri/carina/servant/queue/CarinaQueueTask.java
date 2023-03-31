package io.github.sinri.carina.servant.queue;

import io.github.sinri.carina.logger.event.CarinaEventLogger;
import io.github.sinri.carina.verticles.CarinaVerticleBase;
import io.vertx.core.Future;

/**
 * @since 2.1
 */
public abstract class CarinaQueueTask extends CarinaVerticleBase {

    abstract public String getTaskReference();

    abstract public String getTaskCategory();


    abstract protected CarinaEventLogger prepareLogger();


    /**
     * 被设计在 seeker.get 方法中调用
     */
    public Future<Void> lockTaskBeforeDeployment() {
        // 如果需要就重载此方法
        return Future.succeededFuture();
    }

    // as verticle
    public final void start() {
        setLogger(prepareLogger());
        notifyAfterDeployed();
        Future.succeededFuture()
                .compose(v -> run())
                .recover(throwable -> {
                    getLogger().exception(throwable, "KeelQueueTask Caught throwable from Method run");
                    return Future.succeededFuture();
                })
                .eventually(v -> {
                    getLogger().info("KeelQueueTask to undeploy");
                    notifyBeforeUndeploy();
                    return undeployMe();
                });
    }

    abstract protected Future<Void> run();

    protected void notifyAfterDeployed() {
        // do nothing by default
    }

    protected void notifyBeforeUndeploy() {
        // do nothing by default
    }
}

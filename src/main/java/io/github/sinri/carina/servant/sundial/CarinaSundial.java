package io.github.sinri.carina.servant.sundial;

import io.github.sinri.carina.facade.Carina;
import io.github.sinri.carina.logger.event.center.CarinaOutputEventLogCenter;
import io.github.sinri.carina.verticles.CarinaVerticleBase;
import io.vertx.core.Future;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 3.0.0
 */
public abstract class CarinaSundial extends CarinaVerticleBase {
    private final Map<String, CarinaSundialPlan> planMap = new ConcurrentHashMap<>();
    private Long timerID;
    private final AtomicInteger planFetchingSemaphore = new AtomicInteger(0);

    @Override

    public void start() throws Exception {
        super.start();
        setLogger(CarinaOutputEventLogCenter.getInstance().createLogger(getClass().getName()));

        long delaySeconds = 60 - (System.currentTimeMillis() / 1000) % 60;
        this.timerID = Carina.getVertx().setPeriodic(delaySeconds, 60_000L, timerID -> {
            Calendar calendar = Calendar.getInstance();
            handleEveryMinute(calendar);
        });
    }

    private void handleEveryMinute(Calendar now) {
        planMap.forEach((key, plan) -> {
            if (plan.cronExpression().match(now)) {
                plan.execute(now);
            }
        });

        // refresh plan, pfs {0: not-fetching, more: fetching}
        if (planFetchingSemaphore.get() == 0) {
            planFetchingSemaphore.incrementAndGet();

            // since 3.0.1
            fetchPlans()
                    .compose(plans -> {
                        Set<String> toDelete = new HashSet<>(planMap.keySet());
                        plans.forEach(plan -> {
                            toDelete.remove(plan.key());
                            planMap.put(plan.key(), plan);
                        });
                        if (!toDelete.isEmpty()) {
                            toDelete.forEach(planMap::remove);
                        }
                        return Future.succeededFuture();
                    })
                    .eventually(v -> {
                        planFetchingSemaphore.decrementAndGet();
                        return Future.succeededFuture();
                    });
        }

    }

    /**
     * @since 3.0.1
     * Before plansSupplier is removed, when plansSupplier returns non-null supplier, use that and ignore this.
     */
    abstract protected Future<Collection<CarinaSundialPlan>> fetchPlans();

    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.timerID != null) {
            Carina.getVertx().cancelTimer(this.timerID);
        }
    }

}

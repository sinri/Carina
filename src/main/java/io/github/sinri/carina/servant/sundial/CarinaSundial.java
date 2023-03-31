package io.github.sinri.keel.servant.sundial;

import io.github.sinri.keel.facade.Keel;
import io.github.sinri.keel.logger.event.center.KeelOutputEventLogCenter;
import io.github.sinri.keel.verticles.KeelVerticleBase;
import io.vertx.core.Future;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @since 3.0.0
 */
public abstract class KeelSundial extends KeelVerticleBase {
    private final Map<String, KeelSundialPlan> planMap = new ConcurrentHashMap<>();
    private Long timerID;
    private final AtomicInteger planFetchingSemaphore = new AtomicInteger(0);

    @Override

    public void start() throws Exception {
        super.start();
        setLogger(KeelOutputEventLogCenter.getInstance().createLogger(getClass().getName()));

        long delaySeconds = 60 - (System.currentTimeMillis() / 1000) % 60;
        this.timerID = Keel.getVertx().setPeriodic(delaySeconds, 60_000L, timerID -> {
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

            Supplier<Collection<KeelSundialPlan>> syncSupplier = plansSupplier();
            if (syncSupplier != null) {
                // todo since 3.0.1 to be compatible with 3.0.0, remove this code block later
                Set<String> toDelete = new HashSet<>(planMap.keySet());
                syncSupplier.get().forEach(plan -> {
                    toDelete.remove(plan.key());
                    planMap.put(plan.key(), plan);
                });
                if (!toDelete.isEmpty()) {
                    toDelete.forEach(planMap::remove);
                }

                planFetchingSemaphore.decrementAndGet();
            } else {
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

    }

    @Deprecated(since = "3.0.1", forRemoval = true)
    protected Supplier<Collection<KeelSundialPlan>> plansSupplier() {
        return null;
    }

    /**
     * @since 3.0.1
     * Before plansSupplier is removed, when plansSupplier returns non-null supplier, use that and ignore this.
     */
    abstract protected Future<Collection<KeelSundialPlan>> fetchPlans();

    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.timerID != null) {
            Keel.getVertx().cancelTimer(this.timerID);
        }
    }

}

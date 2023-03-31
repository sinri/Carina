package io.github.sinri.carina.helper.runtime;

import io.github.sinri.carina.facade.Carina;
import io.github.sinri.carina.helper.CarinaHelpers;
import io.vertx.core.Handler;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @since 3.0.0
 */
public class CarinaRuntimeMonitor {
    private final AtomicReference<GCStatResult> _lastGCRef = new AtomicReference<>();
    private final AtomicReference<CPUTimeResult> _lastCPUTimeRef = new AtomicReference<>();


    public void startRuntimeMonitor(long interval, Handler<MonitorSnapshot> handler) {
        Carina.getVertx().setPeriodic(interval, timer -> {
            MonitorSnapshot monitorSnapshot = new MonitorSnapshot();

            GCStatResult gcSnapshot = CarinaHelpers.runtimeHelper().getGCSnapshot();
            CPUTimeResult cpuTimeSnapshot = CarinaHelpers.runtimeHelper().getCPUTimeSnapshot();
            MemoryResult memorySnapshot = CarinaHelpers.runtimeHelper().getMemorySnapshot();

            GCStatResult lastGC = _lastGCRef.get();
            if (lastGC != null) {
                GCStatResult gcDiff = gcSnapshot.since(lastGC);
                monitorSnapshot.setGCStat(gcDiff);
            } else {
                monitorSnapshot.setGCStat(new GCStatResult());
            }
            _lastGCRef.set(gcSnapshot);

            CPUTimeResult lastCpuTime = this._lastCPUTimeRef.get();
            if (lastCpuTime == null) {
                _lastCPUTimeRef.set(cpuTimeSnapshot);
                monitorSnapshot.setCPUTime(new CPUTimeResult());
            } else {
                CPUTimeResult cpuTimeDiff = cpuTimeSnapshot.since(lastCpuTime);
                monitorSnapshot.setCPUTime(cpuTimeDiff);
            }

            monitorSnapshot.setMemory(memorySnapshot);

            handler.handle(monitorSnapshot);
        });
    }
}

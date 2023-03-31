package io.github.sinri.carina.logger.event.center;

import io.github.sinri.carina.helper.CarinaHelpers;
import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.github.sinri.carina.logger.event.CarinaEventLogger;
import io.github.sinri.carina.logger.event.adapter.OutputAdapter;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @since 3.0.0
 */
public class CarinaOutputEventLogCenter extends CarinaSyncEventLogCenter {
    private final static CarinaOutputEventLogCenter defaultInstance = new CarinaOutputEventLogCenter(null);
    private static Set<String> mixedStackPrefixSet;


    static {
        mixedStackPrefixSet = new HashSet<>();
        mixedStackPrefixSet.add("io.vertx.");
        mixedStackPrefixSet.add("java.");
        mixedStackPrefixSet.add("io.netty.");
    }

    private CarinaOutputEventLogCenter(Function<CarinaEventLog, Future<String>> converter) {
        super(OutputAdapter.getInstance(converter));
    }

    public static CarinaOutputEventLogCenter getInstance() {
        return defaultInstance;
    }

    public static CarinaOutputEventLogCenter getInstance(Function<CarinaEventLog, Future<String>> converter) {
        return new CarinaOutputEventLogCenter(converter);
    }

    /**
     * @param mixedStackPrefixSet name prefix of class to ignore
     * @since 3.0.0
     */
    public static void setMixedStackPrefixSet(Set<String> mixedStackPrefixSet) {
        CarinaOutputEventLogCenter.mixedStackPrefixSet = mixedStackPrefixSet;
    }

    public static CarinaEventLogger instantLogger() {
        CarinaOutputEventLogCenter logCenter = getInstance(CarinaEventLog::render);
        return logCenter.createLogger("INSTANT", eventLog -> {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length <= 5) {
                eventLog.put("stack", null);
            } else {
                JsonArray array = new JsonArray();

                StackTraceElement[] slice = Arrays.copyOfRange(stackTrace, 5, stackTrace.length);
                CarinaHelpers.jsonHelper().filterStackTrace(
                        slice,
                        mixedStackPrefixSet,
                        (currentPrefix, ps) -> array.add(currentPrefix + " × " + ps),
                        stackTraceElement -> array.add(stackTraceElement.toString())
                );

                eventLog.put("stack", array);
            }
        });
    }

    public static void main(String[] args) {
        CarinaOutputEventLogCenter.instantLogger().info("main");

        new P1().a();
    }

    private static class P1 {
        void a() {
            CarinaOutputEventLogCenter.instantLogger().info("p1::a");
        }
    }
}

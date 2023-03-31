package io.github.sinri.carina.logger.event.adapter;

import io.github.sinri.carina.facade.async.CarinaAsyncKit;
import io.github.sinri.carina.helper.CarinaHelpers;
import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * @since 3.0.0
 */
public class AsyncFilesWriterAdapter implements CarinaEventLoggerAdapter {
    private final String logDir;
    private final String dateFormat;
    private final Function<CarinaEventLog, String> eventLogComposer;

    public AsyncFilesWriterAdapter(String logDir) {
        this(logDir, "yyyy-MM-dd", null);
    }

    public AsyncFilesWriterAdapter(String logDir, String dateFormat) {
        this(logDir, dateFormat, null);
    }

    public AsyncFilesWriterAdapter(String logDir, String dateFormat, Function<CarinaEventLog, String> eventLogComposer) {
        this.logDir = logDir;
        this.dateFormat = dateFormat;
        this.eventLogComposer = eventLogComposer;
    }


    @Override
    public void close(Promise<Void> promise) {
        promise.complete();
    }

    @Override
    public Future<Void> dealWithLogs(List<CarinaEventLog> buffer) {
        Map<String, List<CarinaEventLog>> fileLogsMap = new HashMap<>();

        for (CarinaEventLog eventLog : buffer) {
            try {
                String topic = eventLog.topic();
                String[] topicComponents = topic.replaceAll("(^[.]+)|([.]+$)", "").split("[.]+");

                String finalTopic;
                File finalDir;
                if (topicComponents.length > 1) {
                    finalTopic = topicComponents[topicComponents.length - 1];
                    StringBuilder x = new StringBuilder(this.logDir);
                    for (int i = 0; i < topicComponents.length - 1; i++) {
                        x.append(File.separator).append(topicComponents[i]);
                    }
                    finalDir = new File(x.toString());
                } else {
                    finalTopic = topic;
                    finalDir = new File(this.logDir + File.separator + topic);
                }


                if (!finalDir.exists()) {
                    if (!finalDir.mkdirs()) {
                        throw new IOException("Path " + finalDir + " create dir failed");
                    }
                }
                if (!finalDir.isDirectory()) {
                    throw new IOException("Path " + finalDir + " not dir");
                }
                String finalFile = finalDir + File.separator + finalTopic + "-"
                        + CarinaHelpers.datetimeHelper().getDateExpression(new Date(eventLog.timestamp()), dateFormat)
                        + ".log";

                fileLogsMap.computeIfAbsent(finalFile, s -> new ArrayList<>()).add(eventLog);

            } catch (Throwable e) {
                System.out.println("AsyncFilesWriterAdapter::dealWithLogs ERROR: " + e);
            }
        }

        return CarinaAsyncKit.parallelForAllResult(fileLogsMap.entrySet(), entry -> {
                    return dealWithLogsForOneFile(new File(entry.getKey()), entry.getValue());
                })
                .compose(parallelResult -> {
                    return Future.succeededFuture();
                });
    }

    private Future<Void> dealWithLogsForOneFile(File file, List<CarinaEventLog> buffer) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            for (CarinaEventLog eventLog : buffer) {
                if (this.eventLogComposer == null) {
                    fileWriter.write(eventLog.toString() + "\n");
                } else {
                    fileWriter.write(this.eventLogComposer.apply(eventLog) + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("AsyncFilesWriterAdapter::dealWithLogsForOneFile(" + file + ") ERROR: " + e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Object processThrowable(Throwable throwable) {
        return CarinaHelpers.stringHelper().renderThrowableChain(throwable);
    }
}

package io.github.sinri.carina.logger.event.adapter;

import io.github.sinri.carina.helper.CarinaHelpers;
import io.github.sinri.carina.logger.event.CarinaEventLog;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SingleFileWriterAdapter implements CarinaEventLoggerAdapter {
    private FileWriter fileWriter;

    public SingleFileWriterAdapter(String filepath) {
        try {
            fileWriter = new FileWriter(filepath, true);
        } catch (IOException e) {
            fileWriter = null;
        }
    }

    @Override
    public void close(Promise<Void> promise) {
        try {
            fileWriter.close();
            promise.complete();
        } catch (IOException e) {
            promise.fail(e);
        } finally {
            fileWriter = null;
        }
    }

    @Override
    public Future<Void> dealWithLogs(List<CarinaEventLog> buffer) {
        if (fileWriter != null) {
            buffer.forEach(eventLog -> {
                try {
                    fileWriter.write(eventLog.toString());
                    fileWriter.write("\n");
                } catch (IOException e) {
                    // ignore
                }
            });
        }
        return Future.succeededFuture();
    }

    @Override
    public Object processThrowable(Throwable throwable) {
        return CarinaHelpers.stringHelper().renderThrowableChain(throwable);
    }

}

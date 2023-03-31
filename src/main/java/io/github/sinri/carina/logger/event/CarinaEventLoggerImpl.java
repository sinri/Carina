package io.github.sinri.carina.logger.event;

import io.vertx.core.Handler;

import java.util.function.Supplier;

public class CarinaEventLoggerImpl implements CarinaEventLogger {
    private final Supplier<CarinaEventLogCenter> eventLogCenterSupplier;
    private final String presetTopic;

    private Handler<CarinaEventLog> presetEventLogEditor = null;

    public CarinaEventLoggerImpl(
            String presetTopic,
            Supplier<CarinaEventLogCenter> eventLogCenterSupplier
    ) {
        this(presetTopic, eventLogCenterSupplier, null);
    }

    public CarinaEventLoggerImpl(
            String presetTopic,
            Supplier<CarinaEventLogCenter> eventLogCenterSupplier,
            Handler<CarinaEventLog> presetEventLogEditor
    ) {
        this.presetTopic = presetTopic;
        this.eventLogCenterSupplier = eventLogCenterSupplier;
        this.presetEventLogEditor = presetEventLogEditor;
    }

    @Override
    public Supplier<CarinaEventLogCenter> getEventLogCenterSupplier() {
        return eventLogCenterSupplier;
    }

    @Override
    public String getPresetTopic() {
        return presetTopic;
    }

    @Override
    public Handler<CarinaEventLog> getPresetEventLogEditor() {
        return presetEventLogEditor;
    }

    @Override
    public CarinaEventLogger setPresetEventLogEditor(Handler<CarinaEventLog> editor) {
        this.presetEventLogEditor = editor;
        return this;
    }
}

package io.github.sinri.carina.verticles;

import io.github.sinri.carina.logger.event.CarinaEventLogger;
import io.vertx.core.AbstractVerticle;

import javax.annotation.Nonnull;

abstract public class CarinaVerticleBase extends AbstractVerticle implements CarinaVerticle {
    private CarinaEventLogger logger;

    public CarinaVerticleBase() {
        this.logger = CarinaEventLogger.silentLogger();
    }

    @Override
    final public @Nonnull CarinaEventLogger getLogger() {
        return logger;
    }

    final public void setLogger(@Nonnull CarinaEventLogger logger) {
        this.logger = logger;
    }
}

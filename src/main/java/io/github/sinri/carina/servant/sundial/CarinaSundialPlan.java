package io.github.sinri.carina.servant.sundial;

import io.github.sinri.carina.core.CronExpression;

import java.util.Calendar;

/**
 * @since 3.0.0
 */
public interface CarinaSundialPlan {
    String key();

    CronExpression cronExpression();

    void execute(Calendar now);
}

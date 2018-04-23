package com.digitalascent.flogger.logback;

import ch.qos.logback.classic.Logger;
import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.SimpleMessageFormatter;
import com.google.common.flogger.util.CallerFinder;
import com.google.common.flogger.util.StackBasedLogSite;

import javax.annotation.Nullable;
import java.util.logging.Level;


final class LogbackLoggerBackend extends LoggerBackend implements SimpleMessageFormatter.SimpleLogHandler {
    private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();
    private static final int ERROR_LEVEL_THRESHOLD = Level.SEVERE.intValue();
    private static final int ALL_THRESHOLD = Level.ALL.intValue();

    private final Logger logger;

    LogbackLoggerBackend(Logger logger) {
        if( logger == null) {
            throw new NullPointerException("logger is required");
        }

        this.logger = logger;
    }

    @Override
    public String getLoggerName() {
        return logger.getName();
    }

    @Override
    public boolean isLoggable(Level lvl) {
        return logger.isEnabledFor( null, mapToLogbackLevel(lvl) );
    }

    @Override
    public void log(LogData data) {
        SimpleMessageFormatter.format(data, this);
    }

    @Override
    public void handleError(RuntimeException error, LogData badData) {
        // log at WARN or higher to ensure visibility
        int logbackLevel = mapToLogbackLevel(badData.getLevel()).toInt();
        if( logbackLevel < ch.qos.logback.classic.Level.WARN.toInt() ) {
            logbackLevel = ch.qos.logback.classic.Level.WARN.toInt();
        }
        logger.log(null,Logger.FQCN,logbackLevel,String.format("LOGGING ERROR: %s; original (unformatted) message: %s",
                error.getMessage(), badData.getLiteralArgument()), null, null);
    }

    @Override
    public LogSite inferLogSite(Class<?> loggerClass, int stackFramesToSkip) {
        // Skip an additional stack frame because we create the Throwable inside this method, not at
        // the point that this method was invoked (which allows completely alternate implementations
        // to avoid even constructing the Throwable instance).
        StackTraceElement caller =
                CallerFinder.findCallerOf(loggerClass, new Throwable(), stackFramesToSkip + 1);
        return caller != null ? new StackBasedLogSite(caller) : LogSite.INVALID;
    }

    @Override
    public void handleFormattedLogMessage(Level level, String message, @Nullable Throwable thrown) {
        logger.log(null, Logger.FQCN, mapToLogbackLevel(level).toInt(), message, null, thrown );
    }

    private static ch.qos.logback.classic.Level mapToLogbackLevel(Level level) {
        int julLevelValue = level.intValue();

        if( julLevelValue == ALL_THRESHOLD ) {
            return ch.qos.logback.classic.Level.ALL;
        }
        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            return ch.qos.logback.classic.Level.TRACE;
        }
        if (julLevelValue <= DEBUG_LEVEL_THRESHOLD) {
            return ch.qos.logback.classic.Level.DEBUG;
        }
        if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            return ch.qos.logback.classic.Level.INFO;
        }
        if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            return ch.qos.logback.classic.Level.WARN;
        }
        if (julLevelValue <= ERROR_LEVEL_THRESHOLD) {
            return ch.qos.logback.classic.Level.ERROR;
        }
        return ch.qos.logback.classic.Level.OFF;
    }
}

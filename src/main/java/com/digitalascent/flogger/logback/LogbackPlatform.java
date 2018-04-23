package com.digitalascent.flogger.logback;


import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.system.BackendFactory;
import com.google.common.flogger.backend.system.Configuration;
import com.google.common.flogger.backend.system.DefaultPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class LogbackPlatform extends DefaultPlatform {

    @Override
    protected void configure(Configuration config) {
        super.configure(config);
        config.setBackendFactory(new LogbackBackendFactory());
    }

    private static final class LogbackBackendFactory extends BackendFactory {
        @Override
        public LoggerBackend create(String loggingClassName) {
            Logger logger = LoggerFactory.getLogger(loggingClassName.replace('$', '.'));
            if( !(logger instanceof ch.qos.logback.classic.Logger)) {
                throw new IllegalStateException("Expected instance of " + ch.qos.logback.classic.Logger.class + ", got " + logger.getClass() );
            }
            return new LogbackLoggerBackend((ch.qos.logback.classic.Logger) logger);
        }
    }
}

package com.google.common.flogger.backend;

import com.digitalascent.flogger.logback.LogbackPlatform;

public class PlatformProvider {
    public Platform getPlatform() {
        return new LogbackPlatform();
    }
}

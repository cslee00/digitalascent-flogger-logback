
Adapter to allow [Google Flogger](https://github.com/google/flogger) to write log entries via [Logback](https://logback.qos.ch/).

Useful to add Flogger support to existing projects without changing underlying logging providers.

# Deployment

1. Add digitalascent-flogger-logback as a runtime dependency to your project;
2. Add Google Flogger (flogger, flogger-system-backend) as a compile dependency to your project;
3. Start using Flogger; it will detect this adapter and direct all log activity via Logback (no configuration changes required)


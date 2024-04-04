import {LoggerConfig, LoggerModule, NgxLoggerLevel} from 'ngx-logger';

const config: LoggerConfig = {
    // serverLoggingUrl: '/api/logs',
    level: NgxLoggerLevel.DEBUG,
    serverLogLevel: NgxLoggerLevel.ERROR
};

export const LOGGING = LoggerModule.forRoot(config);
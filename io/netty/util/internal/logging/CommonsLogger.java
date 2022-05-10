package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.FormattingTuple;
import io.netty.util.internal.logging.MessageFormatter;
import org.apache.commons.logging.Log;

@Deprecated
class CommonsLogger
extends AbstractInternalLogger {
    private static final long serialVersionUID = 8647838678388394885L;
    private final transient Log logger;

    CommonsLogger(Log logger, String name) {
        super(name);
        if (logger == null) {
            throw new NullPointerException("logger");
        }
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        this.logger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg2) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, arg2);
            this.logger.trace(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, argA, argB);
            this.logger.trace(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void trace(String format, Object ... arguments) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft2 = MessageFormatter.arrayFormat(format, arguments);
            this.logger.trace(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void trace(String msg, Throwable t2) {
        this.logger.trace(msg, t2);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        this.logger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg2) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, arg2);
            this.logger.debug(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, argA, argB);
            this.logger.debug(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object ... arguments) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft2 = MessageFormatter.arrayFormat(format, arguments);
            this.logger.debug(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void debug(String msg, Throwable t2) {
        this.logger.debug(msg, t2);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        this.logger.info(msg);
    }

    @Override
    public void info(String format, Object arg2) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, arg2);
            this.logger.info(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, argA, argB);
            this.logger.info(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void info(String format, Object ... arguments) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft2 = MessageFormatter.arrayFormat(format, arguments);
            this.logger.info(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void info(String msg, Throwable t2) {
        this.logger.info(msg, t2);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        this.logger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg2) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, arg2);
            this.logger.warn(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, argA, argB);
            this.logger.warn(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object ... arguments) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft2 = MessageFormatter.arrayFormat(format, arguments);
            this.logger.warn(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void warn(String msg, Throwable t2) {
        this.logger.warn(msg, t2);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        this.logger.error(msg);
    }

    @Override
    public void error(String format, Object arg2) {
        if (this.logger.isErrorEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, arg2);
            this.logger.error(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        if (this.logger.isErrorEnabled()) {
            FormattingTuple ft2 = MessageFormatter.format(format, argA, argB);
            this.logger.error(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void error(String format, Object ... arguments) {
        if (this.logger.isErrorEnabled()) {
            FormattingTuple ft2 = MessageFormatter.arrayFormat(format, arguments);
            this.logger.error(ft2.getMessage(), ft2.getThrowable());
        }
    }

    @Override
    public void error(String msg, Throwable t2) {
        this.logger.error(msg, t2);
    }
}


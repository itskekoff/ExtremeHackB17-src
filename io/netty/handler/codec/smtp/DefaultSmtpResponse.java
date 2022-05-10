package io.netty.handler.codec.smtp;

import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.handler.codec.smtp.SmtpUtils;
import java.util.Collections;
import java.util.List;

public final class DefaultSmtpResponse
implements SmtpResponse {
    private final int code;
    private final List<CharSequence> details;

    public DefaultSmtpResponse(int code) {
        this(code, (List<CharSequence>)null);
    }

    public DefaultSmtpResponse(int code, CharSequence ... details) {
        this(code, SmtpUtils.toUnmodifiableList(details));
    }

    DefaultSmtpResponse(int code, List<CharSequence> details) {
        if (code < 100 || code > 599) {
            throw new IllegalArgumentException("code must be 100 <= code <= 599");
        }
        this.code = code;
        this.details = details == null ? Collections.emptyList() : Collections.unmodifiableList(details);
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public List<CharSequence> details() {
        return this.details;
    }

    public int hashCode() {
        return this.code * 31 + this.details.hashCode();
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof DefaultSmtpResponse)) {
            return false;
        }
        if (o2 == this) {
            return true;
        }
        DefaultSmtpResponse other = (DefaultSmtpResponse)o2;
        return this.code() == other.code() && this.details().equals(other.details());
    }

    public String toString() {
        return "DefaultSmtpResponse{code=" + this.code + ", details=" + this.details + '}';
    }
}


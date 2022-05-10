package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v5.AbstractSocks5Message;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequest;
import io.netty.util.internal.StringUtil;

public class DefaultSocks5PasswordAuthRequest
extends AbstractSocks5Message
implements Socks5PasswordAuthRequest {
    private final String username;
    private final String password;

    public DefaultSocks5PasswordAuthRequest(String username, String password) {
        if (username == null) {
            throw new NullPointerException("username");
        }
        if (password == null) {
            throw new NullPointerException("password");
        }
        if (username.length() > 255) {
            throw new IllegalArgumentException("username: **** (expected: less than 256 chars)");
        }
        if (password.length() > 255) {
            throw new IllegalArgumentException("password: **** (expected: less than 256 chars)");
        }
        this.username = username;
        this.password = password;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf2.append("(decoderResult: ");
            buf2.append(decoderResult);
            buf2.append(", username: ");
        } else {
            buf2.append("(username: ");
        }
        buf2.append(this.username());
        buf2.append(", password: ****)");
        return buf2.toString();
    }
}


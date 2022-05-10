package io.netty.handler.ssl;

import java.util.List;

public interface ApplicationProtocolNegotiator {
    public List<String> protocols();
}


package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;

public interface OpenSslApplicationProtocolNegotiator
extends ApplicationProtocolNegotiator {
    public ApplicationProtocolConfig.Protocol protocol();

    public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior();

    public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior();
}


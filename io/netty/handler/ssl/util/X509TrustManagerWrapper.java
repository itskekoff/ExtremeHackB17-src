package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

final class X509TrustManagerWrapper
extends X509ExtendedTrustManager {
    private final X509TrustManager delegate;

    X509TrustManagerWrapper(X509TrustManager delegate) {
        this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String s2) throws CertificateException {
        this.delegate.checkClientTrusted(chain, s2);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String s2, Socket socket) throws CertificateException {
        this.delegate.checkClientTrusted(chain, s2);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String s2, SSLEngine sslEngine) throws CertificateException {
        this.delegate.checkClientTrusted(chain, s2);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s2) throws CertificateException {
        this.delegate.checkServerTrusted(chain, s2);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s2, Socket socket) throws CertificateException {
        this.delegate.checkServerTrusted(chain, s2);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s2, SSLEngine sslEngine) throws CertificateException {
        this.delegate.checkServerTrusted(chain, s2);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.delegate.getAcceptedIssuers();
    }
}


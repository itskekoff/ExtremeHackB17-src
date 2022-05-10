package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslClientContext;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslClientContext
extends OpenSslContext {
    private final OpenSslSessionContext sessionContext;

    @Deprecated
    public OpenSslClientContext() throws SSLException {
        this((File)null, null, null, null, null, null, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile) throws SSLException {
        this(certChainFile, null);
    }

    @Deprecated
    public OpenSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, null, null, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, ApplicationProtocolConfig apn2, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, null, null, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn2, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn2, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, null, null, ciphers, cipherFilter, apn2, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn2, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(OpenSslClientContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, OpenSslClientContext.toX509CertificatesInternal(keyCertChainFile), OpenSslClientContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn2, null, sessionCacheSize, sessionTimeout);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    OpenSslClientContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn2, String[] protocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(ciphers, cipherFilter, apn2, sessionCacheSize, sessionTimeout, 0, (Certificate[])keyCertChain, ClientAuth.NONE, protocols, false);
        boolean success = false;
        try {
            this.sessionContext = ReferenceCountedOpenSslClientContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory);
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }

    @Override
    public OpenSslSessionContext sessionContext() {
        return this.sessionContext;
    }

    @Override
    OpenSslKeyMaterialManager keyMaterialManager() {
        return null;
    }
}


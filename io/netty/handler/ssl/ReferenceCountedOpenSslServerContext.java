package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslExtendedKeyMaterialManager;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslServerSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslServerContext
extends ReferenceCountedOpenSslContext {
    private static final byte[] ID = new byte[]{110, 101, 116, 116, 121};
    private final OpenSslServerSessionContext sessionContext;
    private final OpenSslKeyMaterialManager keyMaterialManager;

    ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn2, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls) throws SSLException {
        this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, ReferenceCountedOpenSslServerContext.toNegotiator(apn2), sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn2, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls) throws SSLException {
        super(ciphers, cipherFilter, apn2, sessionCacheSize, sessionTimeout, 1, (Certificate[])keyCertChain, clientAuth, protocols, startTls, true);
        boolean success = false;
        try {
            ServerContext context = ReferenceCountedOpenSslServerContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory);
            this.sessionContext = context.sessionContext;
            this.keyMaterialManager = context.keyMaterialManager;
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }

    @Override
    public OpenSslServerSessionContext sessionContext() {
        return this.sessionContext;
    }

    @Override
    OpenSslKeyMaterialManager keyMaterialManager() {
        return this.keyMaterialManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ServerContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory) throws SSLException {
        ServerContext result = new ServerContext();
        Class<ReferenceCountedOpenSslContext> class_ = ReferenceCountedOpenSslContext.class;
        synchronized (ReferenceCountedOpenSslContext.class) {
            block21: {
                try {
                    SSLContext.setVerify(ctx, 0, 10);
                    if (!OpenSsl.useKeyManagerFactory()) {
                        if (keyManagerFactory != null) {
                            throw new IllegalArgumentException("KeyManagerFactory not supported");
                        }
                        ObjectUtil.checkNotNull(keyCertChain, "keyCertChain");
                        ReferenceCountedOpenSslServerContext.setKeyMaterial(ctx, keyCertChain, key, keyPassword);
                    } else {
                        X509KeyManager keyManager;
                        if (keyManagerFactory == null) {
                            keyManagerFactory = ReferenceCountedOpenSslServerContext.buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory);
                        }
                        result.keyMaterialManager = ReferenceCountedOpenSslServerContext.useExtendedKeyManager(keyManager = ReferenceCountedOpenSslServerContext.chooseX509KeyManager(keyManagerFactory.getKeyManagers())) ? new OpenSslExtendedKeyMaterialManager((X509ExtendedKeyManager)keyManager, keyPassword) : new OpenSslKeyMaterialManager(keyManager, keyPassword);
                    }
                }
                catch (Exception e2) {
                    throw new SSLException("failed to set certificate and key", e2);
                }
                try {
                    if (trustCertCollection != null) {
                        trustManagerFactory = ReferenceCountedOpenSslServerContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory);
                    } else if (trustManagerFactory == null) {
                        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        trustManagerFactory.init((KeyStore)null);
                    }
                    X509TrustManager manager = ReferenceCountedOpenSslServerContext.chooseTrustManager(trustManagerFactory.getTrustManagers());
                    if (ReferenceCountedOpenSslServerContext.useExtendedTrustManager(manager)) {
                        SSLContext.setCertVerifyCallback(ctx, new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
                    } else {
                        SSLContext.setCertVerifyCallback(ctx, new TrustManagerVerifyCallback(engineMap, manager));
                    }
                    X509Certificate[] issuers = manager.getAcceptedIssuers();
                    if (issuers == null || issuers.length <= 0) break block21;
                    long bio = 0L;
                    try {
                        bio = ReferenceCountedOpenSslServerContext.toBIO(issuers);
                        if (!SSLContext.setCACertificateBio(ctx, bio)) {
                            throw new SSLException("unable to setup accepted issuers for trustmanager " + manager);
                        }
                    }
                    finally {
                        ReferenceCountedOpenSslServerContext.freeBio(bio);
                    }
                }
                catch (SSLException e3) {
                    throw e3;
                }
                catch (Exception e4) {
                    throw new SSLException("unable to setup trustmanager", e4);
                }
            }
            // ** MonitorExit[var11_10] (shouldn't be in output)
            result.sessionContext = new OpenSslServerSessionContext(thiz);
            result.sessionContext.setSessionIdContext(ID);
            return result;
        }
    }

    private static final class ExtendedTrustManagerVerifyCallback
    extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
        private final X509ExtendedTrustManager manager;

        ExtendedTrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509ExtendedTrustManager manager) {
            super(engineMap);
            this.manager = manager;
        }

        @Override
        void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
            this.manager.checkClientTrusted(peerCerts, auth, engine);
        }
    }

    private static final class TrustManagerVerifyCallback
    extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
        private final X509TrustManager manager;

        TrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509TrustManager manager) {
            super(engineMap);
            this.manager = manager;
        }

        @Override
        void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
            this.manager.checkClientTrusted(peerCerts, auth);
        }
    }

    static final class ServerContext {
        OpenSslServerSessionContext sessionContext;
        OpenSslKeyMaterialManager keyMaterialManager;

        ServerContext() {
        }
    }
}


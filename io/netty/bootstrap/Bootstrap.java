package io.netty.bootstrap;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;

public final class Bootstrap
extends AbstractBootstrap<Bootstrap, Channel> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Bootstrap.class);
    private static final AddressResolverGroup<?> DEFAULT_RESOLVER = DefaultAddressResolverGroup.INSTANCE;
    private final BootstrapConfig config = new BootstrapConfig(this);
    private volatile AddressResolverGroup<SocketAddress> resolver = DEFAULT_RESOLVER;
    private volatile SocketAddress remoteAddress;
    private static final Joiner DOT_JOINER = Joiner.on('.');
    private static final Splitter DOT_SPLITTER = Splitter.on('.');
    @VisibleForTesting
    static final Set<String> BLOCKED_SERVERS = Sets.newHashSet();

    public Bootstrap() {
    }

    private Bootstrap(Bootstrap bootstrap) {
        super(bootstrap);
        this.resolver = bootstrap.resolver;
        this.remoteAddress = bootstrap.remoteAddress;
    }

    public Bootstrap resolver(AddressResolverGroup<?> resolver) {
        this.resolver = resolver == null ? DEFAULT_RESOLVER : resolver;
        return this;
    }

    public Bootstrap remoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    public Bootstrap remoteAddress(String inetHost, int inetPort) {
        this.remoteAddress = InetSocketAddress.createUnresolved(inetHost, inetPort);
        return this;
    }

    public Bootstrap remoteAddress(InetAddress inetHost, int inetPort) {
        this.remoteAddress = new InetSocketAddress(inetHost, inetPort);
        return this;
    }

    public ChannelFuture connect() {
        this.validate();
        SocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            throw new IllegalStateException("remoteAddress not set");
        }
        return this.doResolveAndConnect(remoteAddress, this.config.localAddress());
    }

    public ChannelFuture connect(String inetHost, int inetPort) {
        return this.connect(InetSocketAddress.createUnresolved(inetHost, inetPort));
    }

    public ChannelFuture connect(InetAddress inetHost, int inetPort) {
        return this.connect(new InetSocketAddress(inetHost, inetPort));
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        if (remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        }
        this.validate();
        return this.doResolveAndConnect(remoteAddress, this.config.localAddress());
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        if (remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        }
        this.validate();
        return this.doResolveAndConnect(remoteAddress, localAddress);
    }

    private ChannelFuture doResolveAndConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
        ChannelFuture future = this.checkAddress(remoteAddress);
        if (future != null) {
            return future;
        }
        ChannelFuture regFuture = this.initAndRegister();
        final Channel channel = regFuture.channel();
        if (regFuture.isDone()) {
            if (!regFuture.isSuccess()) {
                return regFuture;
            }
            return this.doResolveAndConnect0(channel, remoteAddress, localAddress, channel.newPromise());
        }
        final AbstractBootstrap.PendingRegistrationPromise promise = new AbstractBootstrap.PendingRegistrationPromise(channel);
        regFuture.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    promise.setFailure(cause);
                } else {
                    promise.registered();
                    Bootstrap.this.doResolveAndConnect0(channel, remoteAddress, localAddress, promise);
                }
            }
        });
        return promise;
    }

    private ChannelFuture doResolveAndConnect0(final Channel channel, SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        try {
            EventLoop eventLoop = channel.eventLoop();
            AddressResolver<SocketAddress> resolver = this.resolver.getResolver(eventLoop);
            if (!resolver.isSupported(remoteAddress) || resolver.isResolved(remoteAddress)) {
                Bootstrap.doConnect(remoteAddress, localAddress, promise);
                return promise;
            }
            Future<SocketAddress> resolveFuture = resolver.resolve(remoteAddress);
            if (resolveFuture.isDone()) {
                Throwable resolveFailureCause = resolveFuture.cause();
                if (resolveFailureCause != null) {
                    channel.close();
                    promise.setFailure(resolveFailureCause);
                } else {
                    Bootstrap.doConnect(resolveFuture.getNow(), localAddress, promise);
                }
                return promise;
            }
            resolveFuture.addListener((GenericFutureListener<Future<SocketAddress>>)new FutureListener<SocketAddress>(){

                @Override
                public void operationComplete(Future<SocketAddress> future) throws Exception {
                    if (future.cause() != null) {
                        channel.close();
                        promise.setFailure(future.cause());
                    } else {
                        Bootstrap.doConnect(future.getNow(), localAddress, promise);
                    }
                }
            });
        }
        catch (Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }

    private static void doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise connectPromise) {
        final Channel channel = connectPromise.channel();
        channel.eventLoop().execute(new Runnable(){

            @Override
            public void run() {
                if (localAddress == null) {
                    channel.connect(remoteAddress, connectPromise);
                } else {
                    channel.connect(remoteAddress, localAddress, connectPromise);
                }
                connectPromise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void init(Channel channel) throws Exception {
        Map<AttributeKey<?>, Object> attrs;
        Map<ChannelOption<?>, Object> options;
        ChannelPipeline p2 = channel.pipeline();
        p2.addLast(this.config.handler());
        Map<ChannelOption<?>, Object> map = options = this.options0();
        synchronized (map) {
            Bootstrap.setChannelOptions(channel, options, logger);
        }
        Map<AttributeKey<?>, Object> map2 = attrs = this.attrs0();
        synchronized (map2) {
            for (Map.Entry<AttributeKey<?>, Object> e2 : attrs.entrySet()) {
                channel.attr(e2.getKey()).set(e2.getValue());
            }
        }
    }

    @Override
    public Bootstrap validate() {
        super.validate();
        if (this.config.handler() == null) {
            throw new IllegalStateException("handler not set");
        }
        return this;
    }

    @Override
    public Bootstrap clone() {
        return new Bootstrap(this);
    }

    public Bootstrap clone(EventLoopGroup group) {
        Bootstrap bs2 = new Bootstrap(this);
        bs2.group = group;
        return bs2;
    }

    public final BootstrapConfig config() {
        return this.config;
    }

    final SocketAddress remoteAddress() {
        return this.remoteAddress;
    }

    final AddressResolverGroup<?> resolver() {
        return this.resolver;
    }

    @Nullable
    @VisibleForTesting
    ChannelFuture checkAddress(SocketAddress remoteAddress) {
        if (remoteAddress instanceof InetSocketAddress) {
            boolean isBlocked;
            InetSocketAddress inetSocketAddress = (InetSocketAddress)remoteAddress;
            InetAddress address = inetSocketAddress.getAddress();
            if (address == null) {
                isBlocked = this.isBlockedServer(inetSocketAddress.getHostString());
            } else {
                boolean bl2 = isBlocked = this.isBlockedServer(address.getHostAddress()) || this.isBlockedServer(address.getHostName());
            }
            if (isBlocked) {
                Object channel = this.channelFactory().newChannel();
                channel.unsafe().closeForcibly();
                SocketException cause = new SocketException("Network is unreachable");
                cause.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
                return new DefaultChannelPromise((Channel)channel, GlobalEventExecutor.INSTANCE).setFailure(cause);
            }
        }
        return null;
    }

    public boolean isBlockedServer(String server) {
        boolean isIp;
        if (server == null || server.isEmpty()) {
            return false;
        }
        while (server.charAt(server.length() - 1) == '.') {
            server = server.substring(0, server.length() - 1);
        }
        if (this.isBlockedServerHostName(server)) {
            return true;
        }
        ArrayList<String> strings = Lists.newArrayList(DOT_SPLITTER.split(server));
        boolean bl2 = isIp = strings.size() == 4;
        if (isIp) {
            for (String string : strings) {
                try {
                    int part = Integer.parseInt(string);
                    if (part >= 0 && part <= 255) {
                        continue;
                    }
                }
                catch (NumberFormatException ignored) {
                    // empty catch block
                }
                isIp = false;
                break;
            }
        }
        if (!isIp && this.isBlockedServerHostName("*." + server)) {
            return true;
        }
        while (strings.size() > 1) {
            strings.remove(isIp ? strings.size() - 1 : 0);
            String starredPart = isIp ? DOT_JOINER.join(strings) + ".*" : "*." + DOT_JOINER.join(strings);
            if (!this.isBlockedServerHostName(starredPart)) continue;
            return true;
        }
        return false;
    }

    private boolean isBlockedServerHostName(String server) {
        return BLOCKED_SERVERS.contains(Hashing.sha1().hashBytes(server.toLowerCase().getBytes(Charset.forName("ISO-8859-1"))).toString());
    }

    static {
        try {
            BLOCKED_SERVERS.addAll(IOUtils.readLines(new URL("https://sessionserver.mojang.com/blockedservers").openConnection().getInputStream()));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}


package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;

public class NioSocketChannel
extends AbstractNioByteChannel
implements SocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSocketChannel.class);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private final SocketChannelConfig config;

    private static java.nio.channels.SocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openSocketChannel();
        }
        catch (IOException e2) {
            throw new ChannelException("Failed to open a socket.", e2);
        }
    }

    public NioSocketChannel() {
        this(DEFAULT_SELECTOR_PROVIDER);
    }

    public NioSocketChannel(SelectorProvider provider) {
        this(NioSocketChannel.newSocket(provider));
    }

    public NioSocketChannel(java.nio.channels.SocketChannel socket) {
        this(null, socket);
    }

    public NioSocketChannel(Channel parent, java.nio.channels.SocketChannel socket) {
        super(parent, socket);
        this.config = new NioSocketChannelConfig(this, socket.socket());
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    public SocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected java.nio.channels.SocketChannel javaChannel() {
        return (java.nio.channels.SocketChannel)super.javaChannel();
    }

    @Override
    public boolean isActive() {
        java.nio.channels.SocketChannel ch2 = this.javaChannel();
        return ch2.isOpen() && ch2.isConnected();
    }

    @Override
    public boolean isOutputShutdown() {
        return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
    }

    @Override
    public boolean isInputShutdown() {
        return this.javaChannel().socket().isInputShutdown() || !this.isActive();
    }

    @Override
    public boolean isShutdown() {
        Socket socket = this.javaChannel().socket();
        return socket.isInputShutdown() && socket.isOutputShutdown() || !this.isActive();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        Executor closeExecutor = ((NioSocketChannelUnsafe)this.unsafe()).prepareToClose();
        if (closeExecutor != null) {
            closeExecutor.execute(new Runnable(){

                @Override
                public void run() {
                    NioSocketChannel.this.shutdownOutput0(promise);
                }
            });
        } else {
            NioEventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
                this.shutdownOutput0(promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        NioSocketChannel.this.shutdownOutput0(promise);
                    }
                });
            }
        }
        return promise;
    }

    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput(this.newPromise());
    }

    @Override
    protected boolean isInputShutdown0() {
        return this.isInputShutdown();
    }

    @Override
    public ChannelFuture shutdownInput(final ChannelPromise promise) {
        Executor closeExecutor = ((NioSocketChannelUnsafe)this.unsafe()).prepareToClose();
        if (closeExecutor != null) {
            closeExecutor.execute(new Runnable(){

                @Override
                public void run() {
                    NioSocketChannel.this.shutdownInput0(promise);
                }
            });
        } else {
            NioEventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
                this.shutdownInput0(promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        NioSocketChannel.this.shutdownInput0(promise);
                    }
                });
            }
        }
        return promise;
    }

    @Override
    public ChannelFuture shutdown() {
        return this.shutdown(this.newPromise());
    }

    @Override
    public ChannelFuture shutdown(final ChannelPromise promise) {
        Executor closeExecutor = ((NioSocketChannelUnsafe)this.unsafe()).prepareToClose();
        if (closeExecutor != null) {
            closeExecutor.execute(new Runnable(){

                @Override
                public void run() {
                    NioSocketChannel.this.shutdown0(promise);
                }
            });
        } else {
            NioEventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
                this.shutdown0(promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        NioSocketChannel.this.shutdown0(promise);
                    }
                });
            }
        }
        return promise;
    }

    private void shutdownOutput0(ChannelPromise promise) {
        try {
            this.shutdownOutput0();
            promise.setSuccess();
        }
        catch (Throwable t2) {
            promise.setFailure(t2);
        }
    }

    private void shutdownOutput0() throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            this.javaChannel().shutdownOutput();
        } else {
            this.javaChannel().socket().shutdownOutput();
        }
    }

    private void shutdownInput0(ChannelPromise promise) {
        try {
            this.shutdownInput0();
            promise.setSuccess();
        }
        catch (Throwable t2) {
            promise.setFailure(t2);
        }
    }

    private void shutdownInput0() throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            this.javaChannel().shutdownInput();
        } else {
            this.javaChannel().socket().shutdownInput();
        }
    }

    private void shutdown0(ChannelPromise promise) {
        Throwable cause = null;
        try {
            this.shutdownOutput0();
        }
        catch (Throwable t2) {
            cause = t2;
        }
        try {
            this.shutdownInput0();
        }
        catch (Throwable t3) {
            if (cause == null) {
                promise.setFailure(t3);
            } else {
                logger.debug("Exception suppressed because a previous exception occurred.", t3);
                promise.setFailure(cause);
            }
            return;
        }
        if (cause == null) {
            promise.setSuccess();
        } else {
            promise.setFailure(cause);
        }
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.doBind0(localAddress);
    }

    private void doBind0(SocketAddress localAddress) throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            SocketUtils.bind(this.javaChannel(), localAddress);
        } else {
            SocketUtils.bind(this.javaChannel().socket(), localAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.doBind0(localAddress);
        }
        boolean success = false;
        try {
            boolean connected = SocketUtils.connect(this.javaChannel(), remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(8);
            }
            success = true;
            boolean bl2 = connected;
            return bl2;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error();
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.javaChannel().close();
    }

    @Override
    protected int doReadBytes(ByteBuf byteBuf) throws Exception {
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
        return byteBuf.writeBytes(this.javaChannel(), allocHandle.attemptedBytesRead());
    }

    @Override
    protected int doWriteBytes(ByteBuf buf2) throws Exception {
        int expectedWrittenBytes = buf2.readableBytes();
        return buf2.readBytes(this.javaChannel(), expectedWrittenBytes);
    }

    @Override
    protected long doWriteFileRegion(FileRegion region) throws Exception {
        long position = region.transferred();
        return region.transferTo(this.javaChannel(), position);
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in2) throws Exception {
        block10: {
            boolean setOpWrite;
            boolean done;
            do {
                int size;
                if ((size = in2.size()) == 0) {
                    this.clearOpWrite();
                    break block10;
                }
                long writtenBytes = 0L;
                done = false;
                setOpWrite = false;
                ByteBuffer[] nioBuffers = in2.nioBuffers();
                int nioBufferCnt = in2.nioBufferCount();
                long expectedWrittenBytes = in2.nioBufferSize();
                java.nio.channels.SocketChannel ch2 = this.javaChannel();
                block0 : switch (nioBufferCnt) {
                    case 0: {
                        super.doWrite(in2);
                        return;
                    }
                    case 1: {
                        int i2;
                        ByteBuffer nioBuffer = nioBuffers[0];
                        for (i2 = this.config().getWriteSpinCount() - 1; i2 >= 0; --i2) {
                            int localWrittenBytes = ch2.write(nioBuffer);
                            if (localWrittenBytes == 0) {
                                setOpWrite = true;
                                break block0;
                            }
                            writtenBytes += (long)localWrittenBytes;
                            if ((expectedWrittenBytes -= (long)localWrittenBytes) != 0L) continue;
                            done = true;
                            break block0;
                        }
                        break;
                    }
                    default: {
                        int i2;
                        for (i2 = this.config().getWriteSpinCount() - 1; i2 >= 0; --i2) {
                            long localWrittenBytes = ch2.write(nioBuffers, 0, nioBufferCnt);
                            if (localWrittenBytes == 0L) {
                                setOpWrite = true;
                                break block0;
                            }
                            writtenBytes += localWrittenBytes;
                            if ((expectedWrittenBytes -= localWrittenBytes) != 0L) continue;
                            done = true;
                            break block0;
                        }
                    }
                }
                in2.removeBytes(writtenBytes);
            } while (done);
            this.incompleteWrite(setOpWrite);
        }
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioSocketChannelUnsafe();
    }

    private final class NioSocketChannelConfig
    extends DefaultSocketChannelConfig {
        private NioSocketChannelConfig(NioSocketChannel channel, Socket javaSocket) {
            super(channel, javaSocket);
        }

        @Override
        protected void autoReadCleared() {
            NioSocketChannel.this.clearReadPending();
        }
    }

    private final class NioSocketChannelUnsafe
    extends AbstractNioByteChannel.NioByteUnsafe {
        private NioSocketChannelUnsafe() {
            super(NioSocketChannel.this);
        }

        @Override
        protected Executor prepareToClose() {
            try {
                if (NioSocketChannel.this.javaChannel().isOpen() && NioSocketChannel.this.config().getSoLinger() > 0) {
                    NioSocketChannel.this.doDeregister();
                    return GlobalEventExecutor.INSTANCE;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return null;
        }
    }
}


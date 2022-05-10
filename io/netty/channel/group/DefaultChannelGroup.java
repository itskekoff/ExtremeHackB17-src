package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.group.CombinedIterator;
import io.netty.channel.group.DefaultChannelGroupFuture;
import io.netty.channel.group.VoidChannelGroupFuture;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup
extends AbstractSet<Channel>
implements ChannelGroup {
    private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentMap<ChannelId, Channel> serverChannels = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<ChannelId, Channel> nonServerChannels = PlatformDependent.newConcurrentHashMap();
    private final ChannelFutureListener remover = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            DefaultChannelGroup.this.remove(future.channel());
        }
    };
    private final VoidChannelGroupFuture voidFuture = new VoidChannelGroupFuture(this);
    private final boolean stayClosed;
    private volatile boolean closed;

    public DefaultChannelGroup(EventExecutor executor) {
        this(executor, false);
    }

    public DefaultChannelGroup(String name, EventExecutor executor) {
        this(name, executor, false);
    }

    public DefaultChannelGroup(EventExecutor executor, boolean stayClosed) {
        this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor, stayClosed);
    }

    public DefaultChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.executor = executor;
        this.stayClosed = stayClosed;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Channel find(ChannelId id2) {
        Channel c2 = (Channel)this.nonServerChannels.get(id2);
        if (c2 != null) {
            return c2;
        }
        return (Channel)this.serverChannels.get(id2);
    }

    @Override
    public boolean isEmpty() {
        return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
    }

    @Override
    public int size() {
        return this.nonServerChannels.size() + this.serverChannels.size();
    }

    @Override
    public boolean contains(Object o2) {
        if (o2 instanceof Channel) {
            Channel c2 = (Channel)o2;
            if (o2 instanceof ServerChannel) {
                return this.serverChannels.containsValue(c2);
            }
            return this.nonServerChannels.containsValue(c2);
        }
        return false;
    }

    @Override
    public boolean add(Channel channel) {
        boolean added;
        ConcurrentMap<ChannelId, Channel> map = channel instanceof ServerChannel ? this.serverChannels : this.nonServerChannels;
        boolean bl2 = added = map.putIfAbsent(channel.id(), channel) == null;
        if (added) {
            channel.closeFuture().addListener(this.remover);
        }
        if (this.stayClosed && this.closed) {
            channel.close();
        }
        return added;
    }

    @Override
    public boolean remove(Object o2) {
        Channel c2 = null;
        if (o2 instanceof ChannelId) {
            c2 = (Channel)this.nonServerChannels.remove(o2);
            if (c2 == null) {
                c2 = (Channel)this.serverChannels.remove(o2);
            }
        } else if (o2 instanceof Channel) {
            c2 = (Channel)o2;
            c2 = c2 instanceof ServerChannel ? (Channel)this.serverChannels.remove(c2.id()) : (Channel)this.nonServerChannels.remove(c2.id());
        }
        if (c2 == null) {
            return false;
        }
        c2.closeFuture().removeListener(this.remover);
        return true;
    }

    @Override
    public void clear() {
        this.nonServerChannels.clear();
        this.serverChannels.clear();
    }

    @Override
    public Iterator<Channel> iterator() {
        return new CombinedIterator<Channel>(this.serverChannels.values().iterator(), this.nonServerChannels.values().iterator());
    }

    @Override
    public Object[] toArray() {
        ArrayList channels = new ArrayList(this.size());
        channels.addAll(this.serverChannels.values());
        channels.addAll(this.nonServerChannels.values());
        return channels.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a2) {
        ArrayList channels = new ArrayList(this.size());
        channels.addAll(this.serverChannels.values());
        channels.addAll(this.nonServerChannels.values());
        return channels.toArray(a2);
    }

    @Override
    public ChannelGroupFuture close() {
        return this.close(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect() {
        return this.disconnect(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture deregister() {
        return this.deregister(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture write(Object message) {
        return this.write(message, ChannelMatchers.all());
    }

    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf)message).retainedDuplicate();
        }
        if (message instanceof ByteBufHolder) {
            return ((ByteBufHolder)message).retainedDuplicate();
        }
        return ReferenceCountUtil.retain(message);
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
        return this.write(message, matcher, false);
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher, boolean voidPromise) {
        ChannelGroupFuture future;
        if (message == null) {
            throw new NullPointerException("message");
        }
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        if (voidPromise) {
            for (Channel c2 : this.nonServerChannels.values()) {
                if (!matcher.matches(c2)) continue;
                c2.write(DefaultChannelGroup.safeDuplicate(message), c2.voidPromise());
            }
            future = this.voidFuture;
        } else {
            LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
            for (Channel c3 : this.nonServerChannels.values()) {
                if (!matcher.matches(c3)) continue;
                futures.put(c3, c3.write(DefaultChannelGroup.safeDuplicate(message)));
            }
            future = new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
        }
        ReferenceCountUtil.release(message);
        return future;
    }

    @Override
    public ChannelGroup flush() {
        return this.flush(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message) {
        return this.writeAndFlush(message);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message) {
        return this.writeAndFlush(message, ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.serverChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.disconnect());
        }
        for (Channel c2 : this.nonServerChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.disconnect());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroupFuture close(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        if (this.stayClosed) {
            this.closed = true;
        }
        for (Channel c2 : this.serverChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.close());
        }
        for (Channel c2 : this.nonServerChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.close());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroupFuture deregister(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException("matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.serverChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.deregister());
        }
        for (Channel c2 : this.nonServerChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.deregister());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public ChannelGroup flush(ChannelMatcher matcher) {
        for (Channel c2 : this.nonServerChannels.values()) {
            if (!matcher.matches(c2)) continue;
            c2.flush();
        }
        return this;
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
        return this.writeAndFlush(message, matcher);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
        return this.writeAndFlush(message, matcher, false);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher, boolean voidPromise) {
        ChannelGroupFuture future;
        if (message == null) {
            throw new NullPointerException("message");
        }
        if (voidPromise) {
            for (Channel c2 : this.nonServerChannels.values()) {
                if (!matcher.matches(c2)) continue;
                c2.writeAndFlush(DefaultChannelGroup.safeDuplicate(message), c2.voidPromise());
            }
            future = this.voidFuture;
        } else {
            LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
            for (Channel c3 : this.nonServerChannels.values()) {
                if (!matcher.matches(c3)) continue;
                futures.put(c3, c3.writeAndFlush(DefaultChannelGroup.safeDuplicate(message)));
            }
            future = new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
        }
        ReferenceCountUtil.release(message);
        return future;
    }

    @Override
    public ChannelGroupFuture newCloseFuture() {
        return this.newCloseFuture(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture newCloseFuture(ChannelMatcher matcher) {
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(this.size());
        for (Channel c2 : this.serverChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.closeFuture());
        }
        for (Channel c2 : this.nonServerChannels.values()) {
            if (!matcher.matches(c2)) continue;
            futures.put(c2, c2.closeFuture());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, this.executor);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object o2) {
        return this == o2;
    }

    @Override
    public int compareTo(ChannelGroup o2) {
        int v2 = this.name().compareTo(o2.name());
        if (v2 != 0) {
            return v2;
        }
        return System.identityHashCode(this) - System.identityHashCode(o2);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(name: " + this.name() + ", size: " + this.size() + ')';
    }
}


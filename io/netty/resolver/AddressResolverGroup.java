package io.netty.resolver;

import io.netty.resolver.AddressResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AddressResolverGroup<T extends SocketAddress>
implements Closeable {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
    private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap<EventExecutor, AddressResolver<T>>();

    protected AddressResolverGroup() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AddressResolver<T> getResolver(final EventExecutor executor) {
        AddressResolver<T> r2;
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        if (executor.isShuttingDown()) {
            throw new IllegalStateException("executor not accepting a task");
        }
        Map<EventExecutor, AddressResolver<T>> map = this.resolvers;
        synchronized (map) {
            r2 = this.resolvers.get(executor);
            if (r2 == null) {
                AddressResolver<T> newResolver;
                try {
                    newResolver = this.newResolver(executor);
                }
                catch (Exception e2) {
                    throw new IllegalStateException("failed to create a new resolver", e2);
                }
                this.resolvers.put(executor, newResolver);
                executor.terminationFuture().addListener(new FutureListener<Object>(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void operationComplete(Future<Object> future) throws Exception {
                        Map map = AddressResolverGroup.this.resolvers;
                        synchronized (map) {
                            AddressResolverGroup.this.resolvers.remove(executor);
                        }
                        newResolver.close();
                    }
                });
                r2 = newResolver;
            }
        }
        return r2;
    }

    protected abstract AddressResolver<T> newResolver(EventExecutor var1) throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        AddressResolver[] arraddressResolver = this.resolvers;
        synchronized (this.resolvers) {
            AddressResolver[] rArray = this.resolvers.values().toArray(new AddressResolver[this.resolvers.size()]);
            this.resolvers.clear();
            // ** MonitorExit[var2_1] (shouldn't be in output)
            for (AddressResolver r2 : rArray) {
                try {
                    r2.close();
                }
                catch (Throwable t2) {
                    logger.warn("Failed to close a resolver:", t2);
                }
            }
            return;
        }
    }
}


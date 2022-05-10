package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

class Subscriber {
    @Weak
    private EventBus bus;
    @VisibleForTesting
    final Object target;
    private final Method method;
    private final Executor executor;

    static Subscriber create(EventBus bus2, Object listener, Method method) {
        return Subscriber.isDeclaredThreadSafe(method) ? new Subscriber(bus2, listener, method) : new SynchronizedSubscriber(bus2, listener, method);
    }

    private Subscriber(EventBus bus2, Object target, Method method) {
        this.bus = bus2;
        this.target = Preconditions.checkNotNull(target);
        this.method = method;
        method.setAccessible(true);
        this.executor = bus2.executor();
    }

    final void dispatchEvent(final Object event) {
        this.executor.execute(new Runnable(){

            @Override
            public void run() {
                try {
                    Subscriber.this.invokeSubscriberMethod(event);
                }
                catch (InvocationTargetException e2) {
                    Subscriber.this.bus.handleSubscriberException(e2.getCause(), Subscriber.this.context(event));
                }
            }
        });
    }

    @VisibleForTesting
    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        try {
            this.method.invoke(this.target, Preconditions.checkNotNull(event));
        }
        catch (IllegalArgumentException e2) {
            throw new Error("Method rejected target/argument: " + event, e2);
        }
        catch (IllegalAccessException e3) {
            throw new Error("Method became inaccessible: " + event, e3);
        }
        catch (InvocationTargetException e4) {
            if (e4.getCause() instanceof Error) {
                throw (Error)e4.getCause();
            }
            throw e4;
        }
    }

    private SubscriberExceptionContext context(Object event) {
        return new SubscriberExceptionContext(this.bus, event, this.target, this.method);
    }

    public final int hashCode() {
        return (31 + this.method.hashCode()) * 31 + System.identityHashCode(this.target);
    }

    public final boolean equals(@Nullable Object obj) {
        if (obj instanceof Subscriber) {
            Subscriber that = (Subscriber)obj;
            return this.target == that.target && this.method.equals(that.method);
        }
        return false;
    }

    private static boolean isDeclaredThreadSafe(Method method) {
        return method.getAnnotation(AllowConcurrentEvents.class) != null;
    }

    @VisibleForTesting
    static final class SynchronizedSubscriber
    extends Subscriber {
        private SynchronizedSubscriber(EventBus bus2, Object target, Method method) {
            super(bus2, target, method);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void invokeSubscriberMethod(Object event) throws InvocationTargetException {
            SynchronizedSubscriber synchronizedSubscriber = this;
            synchronized (synchronizedSubscriber) {
                super.invokeSubscriberMethod(event);
            }
        }
    }
}


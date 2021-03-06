package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public final class SimpleTimeLimiter
implements TimeLimiter {
    private final ExecutorService executor;

    public SimpleTimeLimiter(ExecutorService executor) {
        this.executor = Preconditions.checkNotNull(executor);
    }

    public SimpleTimeLimiter() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    public <T> T newProxy(final T target, Class<T> interfaceType, final long timeoutDuration, final TimeUnit timeoutUnit) {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(interfaceType);
        Preconditions.checkNotNull(timeoutUnit);
        Preconditions.checkArgument(timeoutDuration > 0L, "bad timeout: %s", timeoutDuration);
        Preconditions.checkArgument(interfaceType.isInterface(), "interfaceType must be an interface type");
        final Set<Method> interruptibleMethods = SimpleTimeLimiter.findInterruptibleMethods(interfaceType);
        InvocationHandler handler = new InvocationHandler(){

            @Override
            public Object invoke(Object obj, final Method method, final Object[] args) throws Throwable {
                Callable<Object> callable = new Callable<Object>(){

                    @Override
                    public Object call() throws Exception {
                        try {
                            return method.invoke(target, args);
                        }
                        catch (InvocationTargetException e2) {
                            throw SimpleTimeLimiter.throwCause(e2, false);
                        }
                    }
                };
                return SimpleTimeLimiter.this.callWithTimeout(callable, timeoutDuration, timeoutUnit, interruptibleMethods.contains(method));
            }
        };
        return SimpleTimeLimiter.newProxy(interfaceType, handler);
    }

    @Override
    @CanIgnoreReturnValue
    public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible) throws Exception {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(timeoutUnit);
        Preconditions.checkArgument(timeoutDuration > 0L, "timeout must be positive: %s", timeoutDuration);
        Future<T> future = this.executor.submit(callable);
        try {
            if (amInterruptible) {
                try {
                    return future.get(timeoutDuration, timeoutUnit);
                }
                catch (InterruptedException e2) {
                    future.cancel(true);
                    throw e2;
                }
            }
            return Uninterruptibles.getUninterruptibly(future, timeoutDuration, timeoutUnit);
        }
        catch (ExecutionException e3) {
            throw SimpleTimeLimiter.throwCause(e3, true);
        }
        catch (TimeoutException e4) {
            future.cancel(true);
            throw new UncheckedTimeoutException(e4);
        }
    }

    private static Exception throwCause(Exception e2, boolean combineStackTraces) throws Exception {
        Throwable cause = e2.getCause();
        if (cause == null) {
            throw e2;
        }
        if (combineStackTraces) {
            StackTraceElement[] combined = ObjectArrays.concat(cause.getStackTrace(), e2.getStackTrace(), StackTraceElement.class);
            cause.setStackTrace(combined);
        }
        if (cause instanceof Exception) {
            throw (Exception)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw e2;
    }

    private static Set<Method> findInterruptibleMethods(Class<?> interfaceType) {
        HashSet<Method> set = Sets.newHashSet();
        for (Method m2 : interfaceType.getMethods()) {
            if (!SimpleTimeLimiter.declaresInterruptedEx(m2)) continue;
            set.add(m2);
        }
        return set;
    }

    private static boolean declaresInterruptedEx(Method method) {
        for (Class<?> exType : method.getExceptionTypes()) {
            if (exType != InterruptedException.class) continue;
            return true;
        }
        return false;
    }

    private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
        return interfaceType.cast(object);
    }
}


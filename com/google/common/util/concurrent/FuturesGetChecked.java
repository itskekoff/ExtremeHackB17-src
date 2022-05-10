package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@GwtIncompatible
final class FuturesGetChecked {
    private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function<Constructor<?>, Boolean>(){

        @Override
        public Boolean apply(Constructor<?> input) {
            return Arrays.asList(input.getParameterTypes()).contains(String.class);
        }
    }).reverse();

    @CanIgnoreReturnValue
    static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws X {
        return FuturesGetChecked.getChecked(FuturesGetChecked.bestGetCheckedTypeValidator(), future, exceptionClass);
    }

    @CanIgnoreReturnValue
    @VisibleForTesting
    static <V, X extends Exception> V getChecked(GetCheckedTypeValidator validator, Future<V> future, Class<X> exceptionClass) throws X {
        validator.validateClass(exceptionClass);
        try {
            return future.get();
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw FuturesGetChecked.newWithCause(exceptionClass, e2);
        }
        catch (ExecutionException e3) {
            FuturesGetChecked.wrapAndThrowExceptionOrError(e3.getCause(), exceptionClass);
            throw new AssertionError();
        }
    }

    @CanIgnoreReturnValue
    static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws X {
        FuturesGetChecked.bestGetCheckedTypeValidator().validateClass(exceptionClass);
        try {
            return future.get(timeout, unit);
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw FuturesGetChecked.newWithCause(exceptionClass, e2);
        }
        catch (TimeoutException e3) {
            throw FuturesGetChecked.newWithCause(exceptionClass, e3);
        }
        catch (ExecutionException e4) {
            FuturesGetChecked.wrapAndThrowExceptionOrError(e4.getCause(), exceptionClass);
            throw new AssertionError();
        }
    }

    private static GetCheckedTypeValidator bestGetCheckedTypeValidator() {
        return GetCheckedTypeValidatorHolder.BEST_VALIDATOR;
    }

    @VisibleForTesting
    static GetCheckedTypeValidator weakSetValidator() {
        return GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
    }

    @VisibleForTesting
    static GetCheckedTypeValidator classValueValidator() {
        return GetCheckedTypeValidatorHolder.ClassValueValidator.INSTANCE;
    }

    private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass) throws X {
        if (cause instanceof Error) {
            throw new ExecutionError((Error)cause);
        }
        if (cause instanceof RuntimeException) {
            throw new UncheckedExecutionException(cause);
        }
        throw FuturesGetChecked.newWithCause(exceptionClass, cause);
    }

    private static boolean hasConstructorUsableByGetChecked(Class<? extends Exception> exceptionClass) {
        try {
            Exception unused = FuturesGetChecked.newWithCause(exceptionClass, new Exception());
            return true;
        }
        catch (Exception e2) {
            return false;
        }
    }

    private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause) {
        List<Constructor<X>> constructors = Arrays.asList(exceptionClass.getConstructors());
        for (Constructor<X> constructor : FuturesGetChecked.preferringStrings(constructors)) {
            Exception instance = (Exception)FuturesGetChecked.newFromConstructor(constructor, cause);
            if (instance == null) continue;
            if (instance.getCause() == null) {
                instance.initCause(cause);
            }
            return (X)instance;
        }
        throw new IllegalArgumentException("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception", cause);
    }

    private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors) {
        return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
    }

    @Nullable
    private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        for (int i2 = 0; i2 < paramTypes.length; ++i2) {
            Class<?> paramType = paramTypes[i2];
            if (paramType.equals(String.class)) {
                params[i2] = cause.toString();
                continue;
            }
            if (paramType.equals(Throwable.class)) {
                params[i2] = cause;
                continue;
            }
            return null;
        }
        try {
            return constructor.newInstance(params);
        }
        catch (IllegalArgumentException e2) {
            return null;
        }
        catch (InstantiationException e3) {
            return null;
        }
        catch (IllegalAccessException e4) {
            return null;
        }
        catch (InvocationTargetException e5) {
            return null;
        }
    }

    @VisibleForTesting
    static boolean isCheckedException(Class<? extends Exception> type) {
        return !RuntimeException.class.isAssignableFrom(type);
    }

    @VisibleForTesting
    static void checkExceptionClassValidity(Class<? extends Exception> exceptionClass) {
        Preconditions.checkArgument(FuturesGetChecked.isCheckedException(exceptionClass), "Futures.getChecked exception type (%s) must not be a RuntimeException", exceptionClass);
        Preconditions.checkArgument(FuturesGetChecked.hasConstructorUsableByGetChecked(exceptionClass), "Futures.getChecked exception type (%s) must be an accessible class with an accessible constructor whose parameters (if any) must be of type String and/or Throwable", exceptionClass);
    }

    private FuturesGetChecked() {
    }

    @VisibleForTesting
    static class GetCheckedTypeValidatorHolder {
        static final String CLASS_VALUE_VALIDATOR_NAME = GetCheckedTypeValidatorHolder.class.getName() + "$ClassValueValidator";
        static final GetCheckedTypeValidator BEST_VALIDATOR = GetCheckedTypeValidatorHolder.getBestValidator();

        GetCheckedTypeValidatorHolder() {
        }

        static GetCheckedTypeValidator getBestValidator() {
            try {
                Class<?> theClass = Class.forName(CLASS_VALUE_VALIDATOR_NAME);
                return (GetCheckedTypeValidator)theClass.getEnumConstants()[0];
            }
            catch (Throwable t2) {
                return FuturesGetChecked.weakSetValidator();
            }
        }

        static enum WeakSetValidator implements GetCheckedTypeValidator
        {
            INSTANCE;

            private static final Set<WeakReference<Class<? extends Exception>>> validClasses;

            @Override
            public void validateClass(Class<? extends Exception> exceptionClass) {
                for (WeakReference<Class<? extends Exception>> knownGood : validClasses) {
                    if (!exceptionClass.equals(knownGood.get())) continue;
                    return;
                }
                FuturesGetChecked.checkExceptionClassValidity(exceptionClass);
                if (validClasses.size() > 1000) {
                    validClasses.clear();
                }
                validClasses.add(new WeakReference<Class<? extends Exception>>(exceptionClass));
            }

            static {
                validClasses = new CopyOnWriteArraySet<WeakReference<Class<? extends Exception>>>();
            }
        }

        @IgnoreJRERequirement
        static enum ClassValueValidator implements GetCheckedTypeValidator
        {
            INSTANCE;

            private static final ClassValue<Boolean> isValidClass;

            @Override
            public void validateClass(Class<? extends Exception> exceptionClass) {
                isValidClass.get(exceptionClass);
            }

            static {
                isValidClass = new ClassValue<Boolean>(){

                    @Override
                    protected Boolean computeValue(Class<?> type) {
                        FuturesGetChecked.checkExceptionClassValidity(type.asSubclass(Exception.class));
                        return true;
                    }
                };
            }
        }
    }

    @VisibleForTesting
    static interface GetCheckedTypeValidator {
        public void validateClass(Class<? extends Exception> var1);
    }
}


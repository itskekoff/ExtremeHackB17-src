package io.netty.util.internal;

import io.netty.util.internal.ConstantTimeUtils;
import io.netty.util.internal.LongAdderCounter;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.OutOfDirectMemoryError;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlatformDependent {
    private static final InternalLogger logger;
    private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN;
    private static final boolean IS_EXPLICIT_NO_UNSAFE;
    private static final boolean IS_ANDROID;
    private static final boolean IS_WINDOWS;
    private static final boolean MAYBE_SUPER_USER;
    private static final int JAVA_VERSION;
    private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    private static final boolean HAS_UNSAFE;
    private static final boolean DIRECT_BUFFER_PREFERRED;
    private static final long MAX_DIRECT_MEMORY;
    private static final int MPSC_CHUNK_SIZE = 1024;
    private static final int MIN_MAX_MPSC_CAPACITY = 2048;
    private static final int DEFAULT_MAX_MPSC_CAPACITY = 0x100000;
    private static final int MAX_ALLOWED_MPSC_CAPACITY = 0x40000000;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final File TMPDIR;
    private static final int BIT_MODE;
    private static final int ADDRESS_SIZE;
    private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
    private static final AtomicLong DIRECT_MEMORY_COUNTER;
    private static final long DIRECT_MEMORY_LIMIT;
    private static final ThreadLocalRandomProvider RANDOM_PROVIDER;
    public static final boolean BIG_ENDIAN_NATIVE_ORDER;

    public static boolean isAndroid() {
        return IS_ANDROID;
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean maybeSuperUser() {
        return MAYBE_SUPER_USER;
    }

    public static int javaVersion() {
        return JAVA_VERSION;
    }

    public static boolean canEnableTcpNoDelayByDefault() {
        return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    }

    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }

    public static boolean isUnaligned() {
        return PlatformDependent0.isUnaligned();
    }

    public static boolean directBufferPreferred() {
        return DIRECT_BUFFER_PREFERRED;
    }

    public static long maxDirectMemory() {
        return MAX_DIRECT_MEMORY;
    }

    public static File tmpdir() {
        return TMPDIR;
    }

    public static int bitMode() {
        return BIT_MODE;
    }

    public static int addressSize() {
        return ADDRESS_SIZE;
    }

    public static long allocateMemory(long size) {
        return PlatformDependent0.allocateMemory(size);
    }

    public static void freeMemory(long address) {
        PlatformDependent0.freeMemory(address);
    }

    public static void throwException(Throwable t2) {
        if (PlatformDependent.hasUnsafe()) {
            PlatformDependent0.throwException(t2);
        } else {
            PlatformDependent.throwException0(t2);
        }
    }

    private static <E extends Throwable> void throwException0(Throwable t2) throws E {
        throw t2;
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap();
    }

    public static LongCounter newLongCounter() {
        if (PlatformDependent.javaVersion() >= 8) {
            return new LongAdderCounter();
        }
        return new AtomicLongCounter();
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity) {
        return new ConcurrentHashMap(initialCapacity);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor) {
        return new ConcurrentHashMap(initialCapacity, loadFactor);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        return new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> map) {
        return new ConcurrentHashMap<K, V>(map);
    }

    public static void freeDirectBuffer(ByteBuffer buffer) {
        if (PlatformDependent.hasUnsafe() && !PlatformDependent.isAndroid()) {
            PlatformDependent0.freeDirectBuffer(buffer);
        }
    }

    public static long directBufferAddress(ByteBuffer buffer) {
        return PlatformDependent0.directBufferAddress(buffer);
    }

    public static ByteBuffer directBuffer(long memoryAddress, int size) {
        if (PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            return PlatformDependent0.newDirectBuffer(memoryAddress, size);
        }
        throw new UnsupportedOperationException("sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available");
    }

    public static int getInt(Object object, long fieldOffset) {
        return PlatformDependent0.getInt(object, fieldOffset);
    }

    public static byte getByte(long address) {
        return PlatformDependent0.getByte(address);
    }

    public static short getShort(long address) {
        return PlatformDependent0.getShort(address);
    }

    public static int getInt(long address) {
        return PlatformDependent0.getInt(address);
    }

    public static long getLong(long address) {
        return PlatformDependent0.getLong(address);
    }

    public static byte getByte(byte[] data, int index) {
        return PlatformDependent0.getByte(data, index);
    }

    public static short getShort(byte[] data, int index) {
        return PlatformDependent0.getShort(data, index);
    }

    public static int getInt(byte[] data, int index) {
        return PlatformDependent0.getInt(data, index);
    }

    public static long getLong(byte[] data, int index) {
        return PlatformDependent0.getLong(data, index);
    }

    private static long getLongSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return (long)bytes[offset] << 56 | ((long)bytes[offset + 1] & 0xFFL) << 48 | ((long)bytes[offset + 2] & 0xFFL) << 40 | ((long)bytes[offset + 3] & 0xFFL) << 32 | ((long)bytes[offset + 4] & 0xFFL) << 24 | ((long)bytes[offset + 5] & 0xFFL) << 16 | ((long)bytes[offset + 6] & 0xFFL) << 8 | (long)bytes[offset + 7] & 0xFFL;
        }
        return (long)bytes[offset] & 0xFFL | ((long)bytes[offset + 1] & 0xFFL) << 8 | ((long)bytes[offset + 2] & 0xFFL) << 16 | ((long)bytes[offset + 3] & 0xFFL) << 24 | ((long)bytes[offset + 4] & 0xFFL) << 32 | ((long)bytes[offset + 5] & 0xFFL) << 40 | ((long)bytes[offset + 6] & 0xFFL) << 48 | (long)bytes[offset + 7] << 56;
    }

    private static int getIntSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return bytes[offset] << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | bytes[offset + 3] & 0xFF;
        }
        return bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | bytes[offset + 3] << 24;
    }

    private static short getShortSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return (short)(bytes[offset] << 8 | bytes[offset + 1] & 0xFF);
        }
        return (short)(bytes[offset] & 0xFF | bytes[offset + 1] << 8);
    }

    private static int hashCodeAsciiCompute(CharSequence value, int offset, int hash) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset + 4) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset);
        }
        return hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset + 4);
    }

    private static int hashCodeAsciiSanitizeInt(CharSequence value, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return value.charAt(offset + 3) & 0x1F | (value.charAt(offset + 2) & 0x1F) << 8 | (value.charAt(offset + 1) & 0x1F) << 16 | (value.charAt(offset) & 0x1F) << 24;
        }
        return (value.charAt(offset + 3) & 0x1F) << 24 | (value.charAt(offset + 2) & 0x1F) << 16 | (value.charAt(offset + 1) & 0x1F) << 8 | value.charAt(offset) & 0x1F;
    }

    private static int hashCodeAsciiSanitizeShort(CharSequence value, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return value.charAt(offset + 1) & 0x1F | (value.charAt(offset) & 0x1F) << 8;
        }
        return (value.charAt(offset + 1) & 0x1F) << 8 | value.charAt(offset) & 0x1F;
    }

    private static int hashCodeAsciiSanitizsByte(char value) {
        return value & 0x1F;
    }

    public static void putByte(long address, byte value) {
        PlatformDependent0.putByte(address, value);
    }

    public static void putShort(long address, short value) {
        PlatformDependent0.putShort(address, value);
    }

    public static void putInt(long address, int value) {
        PlatformDependent0.putInt(address, value);
    }

    public static void putLong(long address, long value) {
        PlatformDependent0.putLong(address, value);
    }

    public static void putByte(byte[] data, int index, byte value) {
        PlatformDependent0.putByte(data, index, value);
    }

    public static void putShort(byte[] data, int index, short value) {
        PlatformDependent0.putShort(data, index, value);
    }

    public static void putInt(byte[] data, int index, int value) {
        PlatformDependent0.putInt(data, index, value);
    }

    public static void putLong(byte[] data, int index, long value) {
        PlatformDependent0.putLong(data, index, value);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long length) {
        PlatformDependent0.copyMemory(srcAddr, dstAddr, length);
    }

    public static void copyMemory(byte[] src, int srcIndex, long dstAddr, long length) {
        PlatformDependent0.copyMemory(src, BYTE_ARRAY_BASE_OFFSET + (long)srcIndex, null, dstAddr, length);
    }

    public static void copyMemory(long srcAddr, byte[] dst, int dstIndex, long length) {
        PlatformDependent0.copyMemory(null, srcAddr, dst, BYTE_ARRAY_BASE_OFFSET + (long)dstIndex, length);
    }

    public static void setMemory(byte[] dst, int dstIndex, long bytes, byte value) {
        PlatformDependent0.setMemory(dst, BYTE_ARRAY_BASE_OFFSET + (long)dstIndex, bytes, value);
    }

    public static void setMemory(long address, long bytes, byte value) {
        PlatformDependent0.setMemory(address, bytes, value);
    }

    public static ByteBuffer allocateDirectNoCleaner(int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        PlatformDependent.incrementMemoryCounter(capacity);
        try {
            return PlatformDependent0.allocateDirectNoCleaner(capacity);
        }
        catch (Throwable e2) {
            PlatformDependent.decrementMemoryCounter(capacity);
            PlatformDependent.throwException(e2);
            return null;
        }
    }

    public static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        int len = capacity - buffer.capacity();
        PlatformDependent.incrementMemoryCounter(len);
        try {
            return PlatformDependent0.reallocateDirectNoCleaner(buffer, capacity);
        }
        catch (Throwable e2) {
            PlatformDependent.decrementMemoryCounter(len);
            PlatformDependent.throwException(e2);
            return null;
        }
    }

    public static void freeDirectNoCleaner(ByteBuffer buffer) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        int capacity = buffer.capacity();
        PlatformDependent0.freeMemory(PlatformDependent0.directBufferAddress(buffer));
        PlatformDependent.decrementMemoryCounter(capacity);
    }

    private static void incrementMemoryCounter(int capacity) {
        block1: {
            long newUsedMemory;
            long usedMemory;
            if (DIRECT_MEMORY_COUNTER == null) break block1;
            do {
                if ((newUsedMemory = (usedMemory = DIRECT_MEMORY_COUNTER.get()) + (long)capacity) <= DIRECT_MEMORY_LIMIT) continue;
                throw new OutOfDirectMemoryError("failed to allocate " + capacity + " byte(s) of direct memory (used: " + usedMemory + ", max: " + DIRECT_MEMORY_LIMIT + ')');
            } while (!DIRECT_MEMORY_COUNTER.compareAndSet(usedMemory, newUsedMemory));
        }
    }

    private static void decrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER != null) {
            long usedMemory = DIRECT_MEMORY_COUNTER.addAndGet(-capacity);
            assert (usedMemory >= 0L);
        }
    }

    public static boolean useDirectBufferNoCleaner() {
        return USE_DIRECT_BUFFER_NO_CLEANER;
    }

    public static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() ? PlatformDependent.equalsSafe(bytes1, startPos1, bytes2, startPos2, length) : PlatformDependent0.equals(bytes1, startPos1, bytes2, startPos2, length);
    }

    public static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() ? ConstantTimeUtils.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length) : PlatformDependent0.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length);
    }

    public static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() ? PlatformDependent.hashCodeAsciiSafe(bytes, startPos, length) : PlatformDependent0.hashCodeAscii(bytes, startPos, length);
    }

    public static int hashCodeAscii(CharSequence bytes) {
        int hash = -1028477387;
        int remainingBytes = bytes.length() & 7;
        switch (bytes.length()) {
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: {
                hash = PlatformDependent.hashCodeAsciiCompute(bytes, bytes.length() - 24, PlatformDependent.hashCodeAsciiCompute(bytes, bytes.length() - 16, PlatformDependent.hashCodeAsciiCompute(bytes, bytes.length() - 8, hash)));
                break;
            }
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: {
                hash = PlatformDependent.hashCodeAsciiCompute(bytes, bytes.length() - 16, PlatformDependent.hashCodeAsciiCompute(bytes, bytes.length() - 8, hash));
                break;
            }
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                hash = PlatformDependent.hashCodeAsciiCompute(bytes, bytes.length() - 8, hash);
                break;
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                break;
            }
            default: {
                for (int i2 = bytes.length() - 8; i2 >= remainingBytes; i2 -= 8) {
                    hash = PlatformDependent.hashCodeAsciiCompute(bytes, i2, hash);
                }
            }
        }
        switch (remainingBytes) {
            case 7: {
                return ((hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizsByte(bytes.charAt(0))) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeShort(bytes, 1)) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(bytes, 3);
            }
            case 6: {
                return (hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizeShort(bytes, 0)) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(bytes, 2);
            }
            case 5: {
                return (hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizsByte(bytes.charAt(0))) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(bytes, 1);
            }
            case 4: {
                return hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(bytes, 0);
            }
            case 3: {
                return (hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizsByte(bytes.charAt(0))) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeShort(bytes, 1);
            }
            case 2: {
                return hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizeShort(bytes, 0);
            }
            case 1: {
                return hash * 461845907 + PlatformDependent.hashCodeAsciiSanitizsByte(bytes.charAt(0));
            }
        }
        return hash;
    }

    public static <T> Queue<T> newMpscQueue() {
        return PlatformDependent.newMpscQueue(0x100000);
    }

    public static <T> Queue<T> newMpscQueue(int maxCapacity) {
        return Mpsc.newMpscQueue(maxCapacity);
    }

    public static <T> Queue<T> newSpscQueue() {
        return PlatformDependent.hasUnsafe() ? new SpscLinkedQueue() : new SpscLinkedAtomicQueue();
    }

    public static <T> Queue<T> newFixedMpscQueue(int capacity) {
        return PlatformDependent.hasUnsafe() ? new MpscArrayQueue(capacity) : new MpscAtomicArrayQueue(capacity);
    }

    public static ClassLoader getClassLoader(Class<?> clazz) {
        return PlatformDependent0.getClassLoader(clazz);
    }

    public static ClassLoader getContextClassLoader() {
        return PlatformDependent0.getContextClassLoader();
    }

    public static ClassLoader getSystemClassLoader() {
        return PlatformDependent0.getSystemClassLoader();
    }

    public static <C> Deque<C> newConcurrentDeque() {
        if (PlatformDependent.javaVersion() < 7) {
            return new LinkedBlockingDeque();
        }
        return new ConcurrentLinkedDeque();
    }

    public static Random threadLocalRandom() {
        return RANDOM_PROVIDER.current();
    }

    private static boolean isAndroid0() {
        boolean android;
        try {
            Class.forName("android.app.Application", false, PlatformDependent.getSystemClassLoader());
            android = true;
        }
        catch (Throwable ignored) {
            android = false;
        }
        if (android) {
            logger.debug("Platform: Android");
        }
        return android;
    }

    private static boolean isWindows0() {
        boolean windows = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).contains("win");
        if (windows) {
            logger.debug("Platform: Windows");
        }
        return windows;
    }

    private static boolean maybeSuperUser0() {
        String username = SystemPropertyUtil.get("user.name");
        if (PlatformDependent.isWindows()) {
            return "Administrator".equals(username);
        }
        return "root".equals(username) || "toor".equals(username);
    }

    private static int javaVersion0() {
        int majorVersion = PlatformDependent.isAndroid() ? 6 : PlatformDependent.majorVersionFromJavaSpecificationVersion();
        logger.debug("Java version: {}", (Object)majorVersion);
        return majorVersion;
    }

    static int majorVersionFromJavaSpecificationVersion() {
        try {
            String javaSpecVersion = AccessController.doPrivileged(new PrivilegedAction<String>(){

                @Override
                public String run() {
                    return System.getProperty("java.specification.version");
                }
            });
            return PlatformDependent.majorVersion(javaSpecVersion);
        }
        catch (SecurityException e2) {
            logger.debug("security exception while reading java.specification.version", e2);
            return 6;
        }
    }

    static int majorVersion(String javaSpecVersion) {
        String[] components = javaSpecVersion.split("\\.");
        int[] version = new int[components.length];
        for (int i2 = 0; i2 < components.length; ++i2) {
            version[i2] = Integer.parseInt(components[i2]);
        }
        if (version[0] == 1) {
            assert (version[1] >= 6);
            return version[1];
        }
        return version[0];
    }

    static boolean isExplicitNoUnsafe() {
        return IS_EXPLICIT_NO_UNSAFE;
    }

    private static boolean explicitNoUnsafe0() {
        boolean noUnsafe = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
        logger.debug("-Dio.netty.noUnsafe: {}", (Object)noUnsafe);
        if (noUnsafe) {
            logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
            return true;
        }
        boolean tryUnsafe = SystemPropertyUtil.contains("io.netty.tryUnsafe") ? SystemPropertyUtil.getBoolean("io.netty.tryUnsafe", true) : SystemPropertyUtil.getBoolean("org.jboss.netty.tryUnsafe", true);
        if (!tryUnsafe) {
            logger.debug("sun.misc.Unsafe: unavailable (io.netty.tryUnsafe/org.jboss.netty.tryUnsafe)");
            return true;
        }
        return false;
    }

    private static boolean hasUnsafe0() {
        if (PlatformDependent.isAndroid()) {
            logger.debug("sun.misc.Unsafe: unavailable (Android)");
            return false;
        }
        if (IS_EXPLICIT_NO_UNSAFE) {
            return false;
        }
        try {
            boolean hasUnsafe = PlatformDependent0.hasUnsafe();
            logger.debug("sun.misc.Unsafe: {}", (Object)(hasUnsafe ? "available" : "unavailable"));
            return hasUnsafe;
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static long maxDirectMemory0() {
        long maxDirectMemory = 0L;
        ClassLoader systemClassLoader = null;
        try {
            systemClassLoader = PlatformDependent.getSystemClassLoader();
            Class<?> vmClass = Class.forName("sun.misc.VM", true, systemClassLoader);
            Method m2 = vmClass.getDeclaredMethod("maxDirectMemory", new Class[0]);
            maxDirectMemory = ((Number)m2.invoke(null, new Object[0])).longValue();
        }
        catch (Throwable vmClass) {
            // empty catch block
        }
        if (maxDirectMemory > 0L) {
            return maxDirectMemory;
        }
        try {
            Class<?> mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory", true, systemClassLoader);
            Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, systemClassLoader);
            Object runtime = mgmtFactoryClass.getDeclaredMethod("getRuntimeMXBean", new Class[0]).invoke(null, new Object[0]);
            List vmArgs = (List)runtimeClass.getDeclaredMethod("getInputArguments", new Class[0]).invoke(runtime, new Object[0]);
            for (int i2 = vmArgs.size() - 1; i2 >= 0; --i2) {
                Matcher m3 = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher((CharSequence)vmArgs.get(i2));
                if (!m3.matches()) continue;
                maxDirectMemory = Long.parseLong(m3.group(1));
                switch (m3.group(2).charAt(0)) {
                    case 'K': 
                    case 'k': {
                        maxDirectMemory *= 1024L;
                        break;
                    }
                    case 'M': 
                    case 'm': {
                        maxDirectMemory *= 0x100000L;
                        break;
                    }
                    case 'G': 
                    case 'g': {
                        maxDirectMemory *= 0x40000000L;
                    }
                }
                break;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (maxDirectMemory <= 0L) {
            maxDirectMemory = Runtime.getRuntime().maxMemory();
            logger.debug("maxDirectMemory: {} bytes (maybe)", (Object)maxDirectMemory);
        } else {
            logger.debug("maxDirectMemory: {} bytes", (Object)maxDirectMemory);
        }
        return maxDirectMemory;
    }

    private static File tmpdir0() {
        File f2;
        try {
            f2 = PlatformDependent.toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
            if (f2 != null) {
                logger.debug("-Dio.netty.tmpdir: {}", (Object)f2);
                return f2;
            }
            f2 = PlatformDependent.toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
            if (f2 != null) {
                logger.debug("-Dio.netty.tmpdir: {} (java.io.tmpdir)", (Object)f2);
                return f2;
            }
            if (PlatformDependent.isWindows()) {
                f2 = PlatformDependent.toDirectory(System.getenv("TEMP"));
                if (f2 != null) {
                    logger.debug("-Dio.netty.tmpdir: {} (%TEMP%)", (Object)f2);
                    return f2;
                }
                String userprofile = System.getenv("USERPROFILE");
                if (userprofile != null) {
                    f2 = PlatformDependent.toDirectory(userprofile + "\\AppData\\Local\\Temp");
                    if (f2 != null) {
                        logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", (Object)f2);
                        return f2;
                    }
                    f2 = PlatformDependent.toDirectory(userprofile + "\\Local Settings\\Temp");
                    if (f2 != null) {
                        logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", (Object)f2);
                        return f2;
                    }
                }
            } else {
                f2 = PlatformDependent.toDirectory(System.getenv("TMPDIR"));
                if (f2 != null) {
                    logger.debug("-Dio.netty.tmpdir: {} ($TMPDIR)", (Object)f2);
                    return f2;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        f2 = PlatformDependent.isWindows() ? new File("C:\\Windows\\Temp") : new File("/tmp");
        logger.warn("Failed to get the temporary directory; falling back to: {}", (Object)f2);
        return f2;
    }

    private static File toDirectory(String path) {
        if (path == null) {
            return null;
        }
        File f2 = new File(path);
        f2.mkdirs();
        if (!f2.isDirectory()) {
            return null;
        }
        try {
            return f2.getAbsoluteFile();
        }
        catch (Exception ignored) {
            return f2;
        }
    }

    private static int bitMode0() {
        int bitMode = SystemPropertyUtil.getInt("io.netty.bitMode", 0);
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {}", (Object)bitMode);
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt("sun.arch.data.model", 0);
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (sun.arch.data.model)", (Object)bitMode);
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt("com.ibm.vm.bitmode", 0);
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", (Object)bitMode);
            return bitMode;
        }
        String arch = SystemPropertyUtil.get("os.arch", "").toLowerCase(Locale.US).trim();
        if ("amd64".equals(arch) || "x86_64".equals(arch)) {
            bitMode = 64;
        } else if ("i386".equals(arch) || "i486".equals(arch) || "i586".equals(arch) || "i686".equals(arch)) {
            bitMode = 32;
        }
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (os.arch: {})", (Object)bitMode, (Object)arch);
        }
        String vm2 = SystemPropertyUtil.get("java.vm.name", "").toLowerCase(Locale.US);
        Pattern BIT_PATTERN = Pattern.compile("([1-9][0-9]+)-?bit");
        Matcher m2 = BIT_PATTERN.matcher(vm2);
        if (m2.find()) {
            return Integer.parseInt(m2.group(1));
        }
        return 64;
    }

    private static int addressSize0() {
        if (!PlatformDependent.hasUnsafe()) {
            return -1;
        }
        return PlatformDependent0.addressSize();
    }

    private static long byteArrayBaseOffset0() {
        if (!PlatformDependent.hasUnsafe()) {
            return -1L;
        }
        return PlatformDependent0.byteArrayBaseOffset();
    }

    private static boolean equalsSafe(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int end = startPos1 + length;
        int i2 = startPos1;
        int j2 = startPos2;
        while (i2 < end) {
            if (bytes1[i2] != bytes2[j2]) {
                return false;
            }
            ++i2;
            ++j2;
        }
        return true;
    }

    static int hashCodeAsciiSafe(byte[] bytes, int startPos, int length) {
        int hash = -1028477387;
        int remainingBytes = length & 7;
        int end = startPos + remainingBytes;
        for (int i2 = startPos - 8 + length; i2 >= end; i2 -= 8) {
            hash = PlatformDependent0.hashCodeAsciiCompute(PlatformDependent.getLongSafe(bytes, i2), hash);
        }
        switch (remainingBytes) {
            case 7: {
                return ((hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos + 1))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos + 3));
            }
            case 6: {
                return (hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos + 2));
            }
            case 5: {
                return (hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos + 1));
            }
            case 4: {
                return hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos));
            }
            case 3: {
                return (hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos + 1));
            }
            case 2: {
                return hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos));
            }
            case 1: {
                return hash * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos]);
            }
        }
        return hash;
    }

    private PlatformDependent() {
    }

    static {
        long maxDirectMemory;
        logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
        MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
        IS_EXPLICIT_NO_UNSAFE = PlatformDependent.explicitNoUnsafe0();
        IS_ANDROID = PlatformDependent.isAndroid0();
        IS_WINDOWS = PlatformDependent.isWindows0();
        JAVA_VERSION = PlatformDependent.javaVersion0();
        CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !PlatformDependent.isAndroid();
        HAS_UNSAFE = PlatformDependent.hasUnsafe0();
        DIRECT_BUFFER_PREFERRED = HAS_UNSAFE && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
        MAX_DIRECT_MEMORY = PlatformDependent.maxDirectMemory0();
        BYTE_ARRAY_BASE_OFFSET = PlatformDependent.byteArrayBaseOffset0();
        TMPDIR = PlatformDependent.tmpdir0();
        BIT_MODE = PlatformDependent.bitMode0();
        ADDRESS_SIZE = PlatformDependent.addressSize0();
        BIG_ENDIAN_NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        RANDOM_PROVIDER = PlatformDependent.javaVersion() >= 7 ? new ThreadLocalRandomProvider(){

            @Override
            public Random current() {
                return java.util.concurrent.ThreadLocalRandom.current();
            }
        } : new ThreadLocalRandomProvider(){

            @Override
            public Random current() {
                return ThreadLocalRandom.current();
            }
        };
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noPreferDirect: {}", (Object)(!DIRECT_BUFFER_PREFERRED ? 1 : 0));
        }
        if (!(PlatformDependent.hasUnsafe() || PlatformDependent.isAndroid() || IS_EXPLICIT_NO_UNSAFE)) {
            logger.info("Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system unstability.");
        }
        if ((maxDirectMemory = SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1L)) == 0L || !PlatformDependent.hasUnsafe() || !PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            USE_DIRECT_BUFFER_NO_CLEANER = false;
            DIRECT_MEMORY_COUNTER = null;
        } else {
            USE_DIRECT_BUFFER_NO_CLEANER = true;
            DIRECT_MEMORY_COUNTER = maxDirectMemory < 0L ? ((maxDirectMemory = PlatformDependent.maxDirectMemory0()) <= 0L ? null : new AtomicLong()) : new AtomicLong();
        }
        DIRECT_MEMORY_LIMIT = maxDirectMemory;
        logger.debug("io.netty.maxDirectMemory: {} bytes", (Object)maxDirectMemory);
        MAYBE_SUPER_USER = PlatformDependent.maybeSuperUser0();
    }

    private static interface ThreadLocalRandomProvider {
        public Random current();
    }

    private static final class AtomicLongCounter
    extends AtomicLong
    implements LongCounter {
        private static final long serialVersionUID = 4074772784610639305L;

        private AtomicLongCounter() {
        }

        @Override
        public void add(long delta) {
            this.addAndGet(delta);
        }

        @Override
        public void increment() {
            this.incrementAndGet();
        }

        @Override
        public void decrement() {
            this.decrementAndGet();
        }

        @Override
        public long value() {
            return this.get();
        }
    }

    private static final class Mpsc {
        private static final boolean USE_MPSC_CHUNKED_ARRAY_QUEUE;

        private Mpsc() {
        }

        static <T> Queue<T> newMpscQueue(int maxCapacity) {
            if (USE_MPSC_CHUNKED_ARRAY_QUEUE) {
                int capacity = Math.max(Math.min(maxCapacity, 0x40000000), 2048);
                return new MpscChunkedArrayQueue(1024, capacity);
            }
            return new MpscLinkedAtomicQueue();
        }

        static {
            Object unsafe = null;
            if (PlatformDependent.hasUnsafe()) {
                unsafe = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        return UnsafeAccess.UNSAFE;
                    }
                });
            }
            if (unsafe == null) {
                logger.debug("org.jctools-core.MpscChunkedArrayQueue: unavailable");
                USE_MPSC_CHUNKED_ARRAY_QUEUE = false;
            } else {
                logger.debug("org.jctools-core.MpscChunkedArrayQueue: available");
                USE_MPSC_CHUNKED_ARRAY_QUEUE = true;
            }
        }
    }
}


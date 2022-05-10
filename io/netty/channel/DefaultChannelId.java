package io.netty.channel;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelId;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MacAddressUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultChannelId
implements ChannelId {
    private static final long serialVersionUID = 3884076183504074063L;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
    private static final byte[] MACHINE_ID;
    private static final int PROCESS_ID_LEN = 4;
    private static final int PROCESS_ID;
    private static final int SEQUENCE_LEN = 4;
    private static final int TIMESTAMP_LEN = 8;
    private static final int RANDOM_LEN = 4;
    private static final AtomicInteger nextSequence;
    private final byte[] data = new byte[MACHINE_ID.length + 4 + 4 + 8 + 4];
    private final int hashCode;
    private transient String shortValue;
    private transient String longValue;

    public static DefaultChannelId newInstance() {
        return new DefaultChannelId();
    }

    private static int defaultProcessId() {
        int pid;
        String value;
        ClassLoader loader = null;
        try {
            loader = PlatformDependent.getClassLoader(DefaultChannelId.class);
            Class<?> mgmtFactoryType = Class.forName("java.lang.management.ManagementFactory", true, loader);
            Class<?> runtimeMxBeanType = Class.forName("java.lang.management.RuntimeMXBean", true, loader);
            Method getRuntimeMXBean = mgmtFactoryType.getMethod("getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
            Object bean = getRuntimeMXBean.invoke(null, EmptyArrays.EMPTY_OBJECTS);
            Method getName = runtimeMxBeanType.getMethod("getName", EmptyArrays.EMPTY_CLASSES);
            value = (String)getName.invoke(bean, EmptyArrays.EMPTY_OBJECTS);
        }
        catch (Throwable t2) {
            logger.debug("Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", t2);
            try {
                Class<?> processType = Class.forName("android.os.Process", true, loader);
                Method myPid = processType.getMethod("myPid", EmptyArrays.EMPTY_CLASSES);
                value = myPid.invoke(null, EmptyArrays.EMPTY_OBJECTS).toString();
            }
            catch (Throwable t22) {
                logger.debug("Could not invoke Process.myPid(); not Android?", t22);
                value = "";
            }
        }
        int atIndex = value.indexOf(64);
        if (atIndex >= 0) {
            value = value.substring(0, atIndex);
        }
        try {
            pid = Integer.parseInt(value);
        }
        catch (NumberFormatException e2) {
            pid = -1;
        }
        if (pid < 0) {
            pid = PlatformDependent.threadLocalRandom().nextInt();
            logger.warn("Failed to find the current process ID from '{}'; using a random value: {}", (Object)value, (Object)pid);
        }
        return pid;
    }

    private DefaultChannelId() {
        int i2 = 0;
        System.arraycopy(MACHINE_ID, 0, this.data, i2, MACHINE_ID.length);
        i2 += MACHINE_ID.length;
        i2 = this.writeInt(i2, PROCESS_ID);
        i2 = this.writeInt(i2, nextSequence.getAndIncrement());
        i2 = this.writeLong(i2, Long.reverse(System.nanoTime()) ^ System.currentTimeMillis());
        int random = PlatformDependent.threadLocalRandom().nextInt();
        i2 = this.writeInt(i2, random);
        assert (i2 == this.data.length);
        this.hashCode = Arrays.hashCode(this.data);
    }

    private int writeInt(int i2, int value) {
        this.data[i2++] = (byte)(value >>> 24);
        this.data[i2++] = (byte)(value >>> 16);
        this.data[i2++] = (byte)(value >>> 8);
        this.data[i2++] = (byte)value;
        return i2;
    }

    private int writeLong(int i2, long value) {
        this.data[i2++] = (byte)(value >>> 56);
        this.data[i2++] = (byte)(value >>> 48);
        this.data[i2++] = (byte)(value >>> 40);
        this.data[i2++] = (byte)(value >>> 32);
        this.data[i2++] = (byte)(value >>> 24);
        this.data[i2++] = (byte)(value >>> 16);
        this.data[i2++] = (byte)(value >>> 8);
        this.data[i2++] = (byte)value;
        return i2;
    }

    @Override
    public String asShortText() {
        String shortValue = this.shortValue;
        if (shortValue == null) {
            this.shortValue = shortValue = ByteBufUtil.hexDump(this.data, this.data.length - 4, 4);
        }
        return shortValue;
    }

    @Override
    public String asLongText() {
        String longValue = this.longValue;
        if (longValue == null) {
            this.longValue = longValue = this.newLongValue();
        }
        return longValue;
    }

    private String newLongValue() {
        StringBuilder buf2 = new StringBuilder(2 * this.data.length + 5);
        int i2 = 0;
        i2 = this.appendHexDumpField(buf2, i2, MACHINE_ID.length);
        i2 = this.appendHexDumpField(buf2, i2, 4);
        i2 = this.appendHexDumpField(buf2, i2, 4);
        i2 = this.appendHexDumpField(buf2, i2, 8);
        i2 = this.appendHexDumpField(buf2, i2, 4);
        assert (i2 == this.data.length);
        return buf2.substring(0, buf2.length() - 1);
    }

    private int appendHexDumpField(StringBuilder buf2, int i2, int length) {
        buf2.append(ByteBufUtil.hexDump(this.data, i2, length));
        buf2.append('-');
        return i2 += length;
    }

    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public int compareTo(ChannelId o2) {
        if (this == o2) {
            return 0;
        }
        if (o2 instanceof DefaultChannelId) {
            byte[] otherData = ((DefaultChannelId)o2).data;
            int len1 = this.data.length;
            int len2 = otherData.length;
            int len = Math.min(len1, len2);
            for (int k2 = 0; k2 < len; ++k2) {
                byte x2 = this.data[k2];
                byte y2 = otherData[k2];
                if (x2 == y2) continue;
                return (x2 & 0xFF) - (y2 & 0xFF);
            }
            return len1 - len2;
        }
        return this.asLongText().compareTo(o2.asLongText());
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof DefaultChannelId && Arrays.equals(this.data, ((DefaultChannelId)obj).data);
    }

    public String toString() {
        return this.asShortText();
    }

    static {
        nextSequence = new AtomicInteger();
        int processId = -1;
        String customProcessId = SystemPropertyUtil.get("io.netty.processId");
        if (customProcessId != null) {
            try {
                processId = Integer.parseInt(customProcessId);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (processId < 0) {
                processId = -1;
                logger.warn("-Dio.netty.processId: {} (malformed)", (Object)customProcessId);
            } else if (logger.isDebugEnabled()) {
                logger.debug("-Dio.netty.processId: {} (user-set)", (Object)processId);
            }
        }
        if (processId < 0) {
            processId = DefaultChannelId.defaultProcessId();
            if (logger.isDebugEnabled()) {
                logger.debug("-Dio.netty.processId: {} (auto-detected)", (Object)processId);
            }
        }
        PROCESS_ID = processId;
        byte[] machineId = null;
        String customMachineId = SystemPropertyUtil.get("io.netty.machineId");
        if (customMachineId != null) {
            try {
                machineId = MacAddressUtil.parseMAC(customMachineId);
            }
            catch (Exception e2) {
                logger.warn("-Dio.netty.machineId: {} (malformed)", (Object)customMachineId, (Object)e2);
            }
            if (machineId != null) {
                logger.debug("-Dio.netty.machineId: {} (user-set)", (Object)customMachineId);
            }
        }
        if (machineId == null) {
            machineId = MacAddressUtil.defaultMachineId();
            if (logger.isDebugEnabled()) {
                logger.debug("-Dio.netty.machineId: {} (auto-detected)", (Object)MacAddressUtil.formatAddress(machineId));
            }
        }
        MACHINE_ID = machineId;
    }
}


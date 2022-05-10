package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.WString;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Pointer {
    public static final int SIZE = Native.POINTER_SIZE;
    public static final Pointer NULL;
    protected long peer;

    public static final Pointer createConstant(long peer) {
        return new Opaque(peer);
    }

    public static final Pointer createConstant(int peer) {
        return new Opaque((long)peer & 0xFFFFFFFFFFFFFFFFL);
    }

    Pointer() {
    }

    public Pointer(long peer) {
        this.peer = peer;
    }

    public Pointer share(long offset) {
        return this.share(offset, 0L);
    }

    public Pointer share(long offset, long sz2) {
        if (offset == 0L) {
            return this;
        }
        return new Pointer(this.peer + offset);
    }

    public void clear(long size) {
        this.setMemory(0L, size, (byte)0);
    }

    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (o2 == null) {
            return false;
        }
        return o2 instanceof Pointer && ((Pointer)o2).peer == this.peer;
    }

    public int hashCode() {
        return (int)((this.peer >>> 32) + (this.peer & 0xFFFFFFFFFFFFFFFFL));
    }

    public long indexOf(long offset, byte value) {
        return Native.indexOf(this, this.peer, offset, value);
    }

    public void read(long offset, byte[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, short[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, char[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, int[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, long[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, float[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, double[] buf2, int index, int length) {
        Native.read(this, this.peer, offset, buf2, index, length);
    }

    public void read(long offset, Pointer[] buf2, int index, int length) {
        for (int i2 = 0; i2 < length; ++i2) {
            Pointer p2 = this.getPointer(offset + (long)(i2 * SIZE));
            Pointer oldp = buf2[i2 + index];
            if (oldp != null && p2 != null && p2.peer == oldp.peer) continue;
            buf2[i2 + index] = p2;
        }
    }

    public void write(long offset, byte[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long offset, short[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long offset, char[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long offset, int[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long offset, long[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long offset, float[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long offset, double[] buf2, int index, int length) {
        Native.write(this, this.peer, offset, buf2, index, length);
    }

    public void write(long bOff, Pointer[] buf2, int index, int length) {
        for (int i2 = 0; i2 < length; ++i2) {
            this.setPointer(bOff + (long)(i2 * SIZE), buf2[index + i2]);
        }
    }

    Object getValue(long offset, Class<?> type, Object currentValue) {
        Object result = null;
        if (Structure.class.isAssignableFrom(type)) {
            Structure s2 = (Structure)currentValue;
            if (Structure.ByReference.class.isAssignableFrom(type)) {
                s2 = Structure.updateStructureByReference(type, s2, this.getPointer(offset));
            } else {
                s2.useMemory(this, (int)offset, true);
                s2.read();
            }
            result = s2;
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            result = Function.valueOf(this.getInt(offset) != 0);
        } else if (type == Byte.TYPE || type == Byte.class) {
            result = this.getByte(offset);
        } else if (type == Short.TYPE || type == Short.class) {
            result = this.getShort(offset);
        } else if (type == Character.TYPE || type == Character.class) {
            result = Character.valueOf(this.getChar(offset));
        } else if (type == Integer.TYPE || type == Integer.class) {
            result = this.getInt(offset);
        } else if (type == Long.TYPE || type == Long.class) {
            result = this.getLong(offset);
        } else if (type == Float.TYPE || type == Float.class) {
            result = Float.valueOf(this.getFloat(offset));
        } else if (type == Double.TYPE || type == Double.class) {
            result = this.getDouble(offset);
        } else if (Pointer.class.isAssignableFrom(type)) {
            Pointer p2 = this.getPointer(offset);
            if (p2 != null) {
                Pointer oldp;
                Pointer pointer = oldp = currentValue instanceof Pointer ? (Pointer)currentValue : null;
                result = oldp == null || p2.peer != oldp.peer ? p2 : oldp;
            }
        } else if (type == String.class) {
            Pointer p3 = this.getPointer(offset);
            result = p3 != null ? p3.getString(0L) : null;
        } else if (type == WString.class) {
            Pointer p4 = this.getPointer(offset);
            result = p4 != null ? new WString(p4.getWideString(0L)) : null;
        } else if (Callback.class.isAssignableFrom(type)) {
            Pointer fp = this.getPointer(offset);
            if (fp == null) {
                result = null;
            } else {
                Callback cb2 = (Callback)currentValue;
                Pointer oldfp = CallbackReference.getFunctionPointer(cb2);
                if (!fp.equals(oldfp)) {
                    cb2 = CallbackReference.getCallback(type, fp);
                }
                result = cb2;
            }
        } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
            Pointer bp2 = this.getPointer(offset);
            if (bp2 == null) {
                result = null;
            } else {
                Pointer oldbp;
                Pointer pointer = oldbp = currentValue == null ? null : Native.getDirectBufferPointer((Buffer)currentValue);
                if (oldbp == null || !oldbp.equals(bp2)) {
                    throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
                }
                result = currentValue;
            }
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMapped nm2 = (NativeMapped)currentValue;
            if (nm2 != null) {
                Object value = this.getValue(offset, nm2.nativeType(), null);
                result = nm2.fromNative(value, new FromNativeContext(type));
                if (nm2.equals(result)) {
                    result = nm2;
                }
            } else {
                NativeMappedConverter tc2 = NativeMappedConverter.getInstance(type);
                Object value = this.getValue(offset, tc2.nativeType(), null);
                result = tc2.fromNative(value, new FromNativeContext(type));
            }
        } else if (type.isArray()) {
            result = currentValue;
            if (result == null) {
                throw new IllegalStateException("Need an initialized array");
            }
            this.readArray(offset, result, type.getComponentType());
        } else {
            throw new IllegalArgumentException("Reading \"" + type + "\" from memory is not supported");
        }
        return result;
    }

    private void readArray(long offset, Object o2, Class<?> cls) {
        int length = 0;
        length = Array.getLength(o2);
        Object result = o2;
        if (cls == Byte.TYPE) {
            this.read(offset, (byte[])result, 0, length);
        } else if (cls == Short.TYPE) {
            this.read(offset, (short[])result, 0, length);
        } else if (cls == Character.TYPE) {
            this.read(offset, (char[])result, 0, length);
        } else if (cls == Integer.TYPE) {
            this.read(offset, (int[])result, 0, length);
        } else if (cls == Long.TYPE) {
            this.read(offset, (long[])result, 0, length);
        } else if (cls == Float.TYPE) {
            this.read(offset, (float[])result, 0, length);
        } else if (cls == Double.TYPE) {
            this.read(offset, (double[])result, 0, length);
        } else if (Pointer.class.isAssignableFrom(cls)) {
            this.read(offset, (Pointer[])result, 0, length);
        } else if (Structure.class.isAssignableFrom(cls)) {
            Structure[] sarray = (Structure[])result;
            if (Structure.ByReference.class.isAssignableFrom(cls)) {
                Pointer[] parray = this.getPointerArray(offset, sarray.length);
                for (int i2 = 0; i2 < sarray.length; ++i2) {
                    sarray[i2] = Structure.updateStructureByReference(cls, sarray[i2], parray[i2]);
                }
            } else {
                Structure first = sarray[0];
                if (first == null) {
                    first = Structure.newInstance(cls, this.share(offset));
                    first.conditionalAutoRead();
                    sarray[0] = first;
                } else {
                    first.useMemory(this, (int)offset, true);
                    first.read();
                }
                Structure[] tmp = first.toArray(sarray.length);
                for (int i3 = 1; i3 < sarray.length; ++i3) {
                    if (sarray[i3] == null) {
                        sarray[i3] = tmp[i3];
                        continue;
                    }
                    sarray[i3].useMemory(this, (int)(offset + (long)(i3 * sarray[i3].size())), true);
                    sarray[i3].read();
                }
            }
        } else if (NativeMapped.class.isAssignableFrom(cls)) {
            NativeMapped[] array = (NativeMapped[])result;
            NativeMappedConverter tc2 = NativeMappedConverter.getInstance(cls);
            int size = Native.getNativeSize(result.getClass(), result) / array.length;
            for (int i4 = 0; i4 < array.length; ++i4) {
                Object value = this.getValue(offset + (long)(size * i4), tc2.nativeType(), array[i4]);
                array[i4] = (NativeMapped)tc2.fromNative(value, new FromNativeContext(cls));
            }
        } else {
            throw new IllegalArgumentException("Reading array of " + cls + " from memory not supported");
        }
    }

    public byte getByte(long offset) {
        return Native.getByte(this, this.peer, offset);
    }

    public char getChar(long offset) {
        return Native.getChar(this, this.peer, offset);
    }

    public short getShort(long offset) {
        return Native.getShort(this, this.peer, offset);
    }

    public int getInt(long offset) {
        return Native.getInt(this, this.peer, offset);
    }

    public long getLong(long offset) {
        return Native.getLong(this, this.peer, offset);
    }

    public NativeLong getNativeLong(long offset) {
        return new NativeLong(NativeLong.SIZE == 8 ? this.getLong(offset) : (long)this.getInt(offset));
    }

    public float getFloat(long offset) {
        return Native.getFloat(this, this.peer, offset);
    }

    public double getDouble(long offset) {
        return Native.getDouble(this, this.peer, offset);
    }

    public Pointer getPointer(long offset) {
        return Native.getPointer(this.peer + offset);
    }

    public ByteBuffer getByteBuffer(long offset, long length) {
        return Native.getDirectByteBuffer(this, this.peer, offset, length).order(ByteOrder.nativeOrder());
    }

    @Deprecated
    public String getString(long offset, boolean wide) {
        return wide ? this.getWideString(offset) : this.getString(offset);
    }

    public String getWideString(long offset) {
        return Native.getWideString(this, this.peer, offset);
    }

    public String getString(long offset) {
        return this.getString(offset, Native.getDefaultStringEncoding());
    }

    public String getString(long offset, String encoding) {
        return Native.getString(this, offset, encoding);
    }

    public byte[] getByteArray(long offset, int arraySize) {
        byte[] buf2 = new byte[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public char[] getCharArray(long offset, int arraySize) {
        char[] buf2 = new char[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public short[] getShortArray(long offset, int arraySize) {
        short[] buf2 = new short[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public int[] getIntArray(long offset, int arraySize) {
        int[] buf2 = new int[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public long[] getLongArray(long offset, int arraySize) {
        long[] buf2 = new long[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public float[] getFloatArray(long offset, int arraySize) {
        float[] buf2 = new float[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public double[] getDoubleArray(long offset, int arraySize) {
        double[] buf2 = new double[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public Pointer[] getPointerArray(long offset) {
        ArrayList<Pointer> array = new ArrayList<Pointer>();
        int addOffset = 0;
        Pointer p2 = this.getPointer(offset);
        while (p2 != null) {
            array.add(p2);
            p2 = this.getPointer(offset + (long)(addOffset += SIZE));
        }
        return array.toArray(new Pointer[array.size()]);
    }

    public Pointer[] getPointerArray(long offset, int arraySize) {
        Pointer[] buf2 = new Pointer[arraySize];
        this.read(offset, buf2, 0, arraySize);
        return buf2;
    }

    public String[] getStringArray(long offset) {
        return this.getStringArray(offset, -1, Native.getDefaultStringEncoding());
    }

    public String[] getStringArray(long offset, String encoding) {
        return this.getStringArray(offset, -1, encoding);
    }

    public String[] getStringArray(long offset, int length) {
        return this.getStringArray(offset, length, Native.getDefaultStringEncoding());
    }

    @Deprecated
    public String[] getStringArray(long offset, boolean wide) {
        return this.getStringArray(offset, -1, wide);
    }

    public String[] getWideStringArray(long offset) {
        return this.getWideStringArray(offset, -1);
    }

    public String[] getWideStringArray(long offset, int length) {
        return this.getStringArray(offset, length, "--WIDE-STRING--");
    }

    @Deprecated
    public String[] getStringArray(long offset, int length, boolean wide) {
        return this.getStringArray(offset, length, wide ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
    }

    public String[] getStringArray(long offset, int length, String encoding) {
        ArrayList<String> strings = new ArrayList<String>();
        int addOffset = 0;
        if (length != -1) {
            Pointer p2 = this.getPointer(offset + (long)addOffset);
            int count = 0;
            while (count++ < length) {
                String s2 = p2 == null ? null : ("--WIDE-STRING--".equals(encoding) ? p2.getWideString(0L) : p2.getString(0L, encoding));
                strings.add(s2);
                if (count >= length) continue;
                p2 = this.getPointer(offset + (long)(addOffset += SIZE));
            }
        } else {
            Pointer p3;
            while ((p3 = this.getPointer(offset + (long)addOffset)) != null) {
                String s3 = p3 == null ? null : ("--WIDE-STRING--".equals(encoding) ? p3.getWideString(0L) : p3.getString(0L, encoding));
                strings.add(s3);
                addOffset += SIZE;
            }
        }
        return strings.toArray(new String[strings.size()]);
    }

    void setValue(long offset, Object value, Class<?> type) {
        if (type == Boolean.TYPE || type == Boolean.class) {
            this.setInt(offset, Boolean.TRUE.equals(value) ? -1 : 0);
        } else if (type == Byte.TYPE || type == Byte.class) {
            this.setByte(offset, value == null ? (byte)0 : (Byte)value);
        } else if (type == Short.TYPE || type == Short.class) {
            this.setShort(offset, value == null ? (short)0 : (Short)value);
        } else if (type == Character.TYPE || type == Character.class) {
            this.setChar(offset, value == null ? (char)'\u0000' : ((Character)value).charValue());
        } else if (type == Integer.TYPE || type == Integer.class) {
            this.setInt(offset, value == null ? 0 : (Integer)value);
        } else if (type == Long.TYPE || type == Long.class) {
            this.setLong(offset, value == null ? 0L : (Long)value);
        } else if (type == Float.TYPE || type == Float.class) {
            this.setFloat(offset, value == null ? 0.0f : ((Float)value).floatValue());
        } else if (type == Double.TYPE || type == Double.class) {
            this.setDouble(offset, value == null ? 0.0 : (Double)value);
        } else if (type == Pointer.class) {
            this.setPointer(offset, (Pointer)value);
        } else if (type == String.class) {
            this.setPointer(offset, (Pointer)value);
        } else if (type == WString.class) {
            this.setPointer(offset, (Pointer)value);
        } else if (Structure.class.isAssignableFrom(type)) {
            Structure s2 = (Structure)value;
            if (Structure.ByReference.class.isAssignableFrom(type)) {
                this.setPointer(offset, s2 == null ? null : s2.getPointer());
                if (s2 != null) {
                    s2.autoWrite();
                }
            } else {
                s2.useMemory(this, (int)offset, true);
                s2.write();
            }
        } else if (Callback.class.isAssignableFrom(type)) {
            this.setPointer(offset, CallbackReference.getFunctionPointer((Callback)value));
        } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
            Pointer p2 = value == null ? null : Native.getDirectBufferPointer((Buffer)value);
            this.setPointer(offset, p2);
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc2 = NativeMappedConverter.getInstance(type);
            Class<?> nativeType = tc2.nativeType();
            this.setValue(offset, tc2.toNative(value, new ToNativeContext()), nativeType);
        } else if (type.isArray()) {
            this.writeArray(offset, value, type.getComponentType());
        } else {
            throw new IllegalArgumentException("Writing " + type + " to memory is not supported");
        }
    }

    private void writeArray(long offset, Object value, Class<?> cls) {
        if (cls == Byte.TYPE) {
            byte[] buf2 = (byte[])value;
            this.write(offset, buf2, 0, buf2.length);
        } else if (cls == Short.TYPE) {
            short[] buf3 = (short[])value;
            this.write(offset, buf3, 0, buf3.length);
        } else if (cls == Character.TYPE) {
            char[] buf4 = (char[])value;
            this.write(offset, buf4, 0, buf4.length);
        } else if (cls == Integer.TYPE) {
            int[] buf5 = (int[])value;
            this.write(offset, buf5, 0, buf5.length);
        } else if (cls == Long.TYPE) {
            long[] buf6 = (long[])value;
            this.write(offset, buf6, 0, buf6.length);
        } else if (cls == Float.TYPE) {
            float[] buf7 = (float[])value;
            this.write(offset, buf7, 0, buf7.length);
        } else if (cls == Double.TYPE) {
            double[] buf8 = (double[])value;
            this.write(offset, buf8, 0, buf8.length);
        } else if (Pointer.class.isAssignableFrom(cls)) {
            Pointer[] buf9 = (Pointer[])value;
            this.write(offset, buf9, 0, buf9.length);
        } else if (Structure.class.isAssignableFrom(cls)) {
            Structure[] sbuf = (Structure[])value;
            if (Structure.ByReference.class.isAssignableFrom(cls)) {
                Pointer[] buf10 = new Pointer[sbuf.length];
                for (int i2 = 0; i2 < sbuf.length; ++i2) {
                    if (sbuf[i2] == null) {
                        buf10[i2] = null;
                        continue;
                    }
                    buf10[i2] = sbuf[i2].getPointer();
                    sbuf[i2].write();
                }
                this.write(offset, buf10, 0, buf10.length);
            } else {
                Structure first = sbuf[0];
                if (first == null) {
                    sbuf[0] = first = Structure.newInstance(cls, this.share(offset));
                } else {
                    first.useMemory(this, (int)offset, true);
                }
                first.write();
                Structure[] tmp = first.toArray(sbuf.length);
                for (int i3 = 1; i3 < sbuf.length; ++i3) {
                    if (sbuf[i3] == null) {
                        sbuf[i3] = tmp[i3];
                    } else {
                        sbuf[i3].useMemory(this, (int)(offset + (long)(i3 * sbuf[i3].size())), true);
                    }
                    sbuf[i3].write();
                }
            }
        } else if (NativeMapped.class.isAssignableFrom(cls)) {
            NativeMapped[] buf11 = (NativeMapped[])value;
            NativeMappedConverter tc2 = NativeMappedConverter.getInstance(cls);
            Class<?> nativeType = tc2.nativeType();
            int size = Native.getNativeSize(value.getClass(), value) / buf11.length;
            for (int i4 = 0; i4 < buf11.length; ++i4) {
                Object element = tc2.toNative(buf11[i4], new ToNativeContext());
                this.setValue(offset + (long)(i4 * size), element, nativeType);
            }
        } else {
            throw new IllegalArgumentException("Writing array of " + cls + " to memory not supported");
        }
    }

    public void setMemory(long offset, long length, byte value) {
        Native.setMemory(this, this.peer, offset, length, value);
    }

    public void setByte(long offset, byte value) {
        Native.setByte(this, this.peer, offset, value);
    }

    public void setShort(long offset, short value) {
        Native.setShort(this, this.peer, offset, value);
    }

    public void setChar(long offset, char value) {
        Native.setChar(this, this.peer, offset, value);
    }

    public void setInt(long offset, int value) {
        Native.setInt(this, this.peer, offset, value);
    }

    public void setLong(long offset, long value) {
        Native.setLong(this, this.peer, offset, value);
    }

    public void setNativeLong(long offset, NativeLong value) {
        if (NativeLong.SIZE == 8) {
            this.setLong(offset, value.longValue());
        } else {
            this.setInt(offset, value.intValue());
        }
    }

    public void setFloat(long offset, float value) {
        Native.setFloat(this, this.peer, offset, value);
    }

    public void setDouble(long offset, double value) {
        Native.setDouble(this, this.peer, offset, value);
    }

    public void setPointer(long offset, Pointer value) {
        Native.setPointer(this, this.peer, offset, value != null ? value.peer : 0L);
    }

    @Deprecated
    public void setString(long offset, String value, boolean wide) {
        if (wide) {
            this.setWideString(offset, value);
        } else {
            this.setString(offset, value);
        }
    }

    public void setWideString(long offset, String value) {
        Native.setWideString(this, this.peer, offset, value);
    }

    public void setString(long offset, WString value) {
        this.setWideString(offset, value == null ? null : value.toString());
    }

    public void setString(long offset, String value) {
        this.setString(offset, value, Native.getDefaultStringEncoding());
    }

    public void setString(long offset, String value, String encoding) {
        byte[] data = Native.getBytes(value, encoding);
        this.write(offset, data, 0, data.length);
        this.setByte(offset + (long)data.length, (byte)0);
    }

    public String dump(long offset, int size) {
        int BYTES_PER_ROW = 4;
        String TITLE = "memory dump";
        StringWriter sw2 = new StringWriter("memory dump".length() + 2 + size * 2 + size / 4 * 4);
        PrintWriter out = new PrintWriter(sw2);
        out.println("memory dump");
        for (int i2 = 0; i2 < size; ++i2) {
            byte b2 = this.getByte(offset + (long)i2);
            if (i2 % 4 == 0) {
                out.print("[");
            }
            if (b2 >= 0 && b2 < 16) {
                out.print("0");
            }
            out.print(Integer.toHexString(b2 & 0xFF));
            if (i2 % 4 != 3 || i2 >= size - 1) continue;
            out.println("]");
        }
        if (sw2.getBuffer().charAt(sw2.getBuffer().length() - 2) != ']') {
            out.println("]");
        }
        return sw2.toString();
    }

    public String toString() {
        return "native@0x" + Long.toHexString(this.peer);
    }

    public static long nativeValue(Pointer p2) {
        return p2 == null ? 0L : p2.peer;
    }

    public static void nativeValue(Pointer p2, long value) {
        p2.peer = value;
    }

    static {
        if (SIZE == 0) {
            throw new Error("Native library not initialized");
        }
        NULL = null;
    }

    private static class Opaque
    extends Pointer {
        private final String MSG = "This pointer is opaque: " + this;

        private Opaque(long peer) {
            super(peer);
        }

        @Override
        public Pointer share(long offset, long size) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void clear(long size) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public long indexOf(long offset, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, byte[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, char[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, short[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, int[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, long[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, float[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, double[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void read(long bOff, Pointer[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, byte[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, char[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, short[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, int[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, long[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, float[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, double[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void write(long bOff, Pointer[] buf2, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public ByteBuffer getByteBuffer(long offset, long length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public byte getByte(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public char getChar(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public short getShort(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public int getInt(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public long getLong(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public float getFloat(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public double getDouble(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public Pointer getPointer(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public String getString(long bOff, String encoding) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public String getWideString(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setByte(long bOff, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setChar(long bOff, char value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setShort(long bOff, short value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setInt(long bOff, int value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setLong(long bOff, long value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setFloat(long bOff, float value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setDouble(long bOff, double value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setPointer(long offset, Pointer value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setString(long offset, String value, String encoding) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setWideString(long offset, String value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public void setMemory(long offset, long size, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public String dump(long offset, int size) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override
        public String toString() {
            return "const@0x" + Long.toHexString(this.peer);
        }
    }
}


package org.apache.commons.compress.archivers.zip;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.AsiExtraField;
import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField;
import org.apache.commons.compress.archivers.zip.UnicodePathExtraField;
import org.apache.commons.compress.archivers.zip.UnparseableExtraFieldData;
import org.apache.commons.compress.archivers.zip.UnrecognizedExtraField;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.X7875_NewUnix;
import org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.archivers.zip.ZipShort;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExtraFieldUtils {
    private static final int WORD = 4;
    private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap();

    public static void register(Class<?> c2) {
        try {
            ZipExtraField ze2 = (ZipExtraField)c2.newInstance();
            implementations.put(ze2.getHeaderId(), c2);
        }
        catch (ClassCastException cc2) {
            throw new RuntimeException(c2 + " doesn't implement ZipExtraField");
        }
        catch (InstantiationException ie2) {
            throw new RuntimeException(c2 + " is not a concrete class");
        }
        catch (IllegalAccessException ie3) {
            throw new RuntimeException(c2 + "'s no-arg constructor is not public");
        }
    }

    public static ZipExtraField createExtraField(ZipShort headerId) throws InstantiationException, IllegalAccessException {
        Class<?> c2 = implementations.get(headerId);
        if (c2 != null) {
            return (ZipExtraField)c2.newInstance();
        }
        UnrecognizedExtraField u2 = new UnrecognizedExtraField();
        u2.setHeaderId(headerId);
        return u2;
    }

    public static ZipExtraField[] parse(byte[] data) throws ZipException {
        return ExtraFieldUtils.parse(data, true, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local) throws ZipException {
        return ExtraFieldUtils.parse(data, local, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local, UnparseableExtraField onUnparseableData) throws ZipException {
        int length;
        ArrayList<ZipExtraField> v2 = new ArrayList<ZipExtraField>();
        block8: for (int start = 0; start <= data.length - 4; start += length + 4) {
            ZipShort headerId = new ZipShort(data, start);
            length = new ZipShort(data, start + 2).getValue();
            if (start + 4 + length > data.length) {
                switch (onUnparseableData.getKey()) {
                    case 0: {
                        throw new ZipException("bad extra field starting at " + start + ".  Block length of " + length + " bytes exceeds remaining" + " data of " + (data.length - start - 4) + " bytes.");
                    }
                    case 2: {
                        UnparseableExtraFieldData field = new UnparseableExtraFieldData();
                        if (local) {
                            field.parseFromLocalFileData(data, start, data.length - start);
                        } else {
                            field.parseFromCentralDirectoryData(data, start, data.length - start);
                        }
                        v2.add(field);
                    }
                    case 1: {
                        break block8;
                    }
                    default: {
                        throw new ZipException("unknown UnparseableExtraField key: " + onUnparseableData.getKey());
                    }
                }
            }
            try {
                ZipExtraField ze2 = ExtraFieldUtils.createExtraField(headerId);
                if (local) {
                    ze2.parseFromLocalFileData(data, start + 4, length);
                } else {
                    ze2.parseFromCentralDirectoryData(data, start + 4, length);
                }
                v2.add(ze2);
                continue;
            }
            catch (InstantiationException ie2) {
                throw (ZipException)new ZipException(ie2.getMessage()).initCause(ie2);
            }
            catch (IllegalAccessException iae) {
                throw (ZipException)new ZipException(iae.getMessage()).initCause(iae);
            }
        }
        ZipExtraField[] result = new ZipExtraField[v2.size()];
        return v2.toArray(result);
    }

    public static byte[] mergeLocalFileDataData(ZipExtraField[] data) {
        byte[] local;
        boolean lastIsUnparseableHolder = data.length > 0 && data[data.length - 1] instanceof UnparseableExtraFieldData;
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = 4 * regularExtraFieldCount;
        for (ZipExtraField element : data) {
            sum += element.getLocalFileDataLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i2 = 0; i2 < regularExtraFieldCount; ++i2) {
            System.arraycopy(data[i2].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i2].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            byte[] local2 = data[i2].getLocalFileDataData();
            if (local2 == null) continue;
            System.arraycopy(local2, 0, result, start, local2.length);
            start += local2.length;
        }
        if (lastIsUnparseableHolder && (local = data[data.length - 1].getLocalFileDataData()) != null) {
            System.arraycopy(local, 0, result, start, local.length);
        }
        return result;
    }

    public static byte[] mergeCentralDirectoryData(ZipExtraField[] data) {
        byte[] local;
        boolean lastIsUnparseableHolder = data.length > 0 && data[data.length - 1] instanceof UnparseableExtraFieldData;
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = 4 * regularExtraFieldCount;
        for (ZipExtraField element : data) {
            sum += element.getCentralDirectoryLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i2 = 0; i2 < regularExtraFieldCount; ++i2) {
            System.arraycopy(data[i2].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i2].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            byte[] local2 = data[i2].getCentralDirectoryData();
            if (local2 == null) continue;
            System.arraycopy(local2, 0, result, start, local2.length);
            start += local2.length;
        }
        if (lastIsUnparseableHolder && (local = data[data.length - 1].getCentralDirectoryData()) != null) {
            System.arraycopy(local, 0, result, start, local.length);
        }
        return result;
    }

    static {
        ExtraFieldUtils.register(AsiExtraField.class);
        ExtraFieldUtils.register(X5455_ExtendedTimestamp.class);
        ExtraFieldUtils.register(X7875_NewUnix.class);
        ExtraFieldUtils.register(JarMarker.class);
        ExtraFieldUtils.register(UnicodePathExtraField.class);
        ExtraFieldUtils.register(UnicodeCommentExtraField.class);
        ExtraFieldUtils.register(Zip64ExtendedInformationExtraField.class);
    }

    public static final class UnparseableExtraField {
        public static final int THROW_KEY = 0;
        public static final int SKIP_KEY = 1;
        public static final int READ_KEY = 2;
        public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
        public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
        public static final UnparseableExtraField READ = new UnparseableExtraField(2);
        private final int key;

        private UnparseableExtraField(int k2) {
            this.key = k2;
        }

        public int getKey() {
            return this.key;
        }
    }
}


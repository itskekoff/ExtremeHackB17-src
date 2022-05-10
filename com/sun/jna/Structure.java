package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.StructureReadContext;
import com.sun.jna.StructureWriteContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.Union;
import com.sun.jna.WString;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class Structure {
    public static final int ALIGN_DEFAULT = 0;
    public static final int ALIGN_NONE = 1;
    public static final int ALIGN_GNUC = 2;
    public static final int ALIGN_MSVC = 3;
    protected static final int CALCULATE_SIZE = -1;
    static final Map<Class<?>, LayoutInfo> layoutInfo = new WeakHashMap();
    static final Map<Class<?>, List<String>> fieldOrder = new WeakHashMap();
    private Pointer memory;
    private int size = -1;
    private int alignType;
    private String encoding;
    private int actualAlignType;
    private int structAlignment;
    private Map<String, StructField> structFields;
    private final Map<String, Object> nativeStrings = new HashMap<String, Object>();
    private TypeMapper typeMapper;
    private long typeInfo;
    private boolean autoRead = true;
    private boolean autoWrite = true;
    private Structure[] array;
    private boolean readCalled;
    private static final ThreadLocal<Map<Pointer, Structure>> reads = new ThreadLocal<Map<Pointer, Structure>>(){

        @Override
        protected synchronized Map<Pointer, Structure> initialValue() {
            return new HashMap<Pointer, Structure>();
        }
    };
    private static final ThreadLocal<Set<Structure>> busy = new ThreadLocal<Set<Structure>>(){

        @Override
        protected synchronized Set<Structure> initialValue() {
            return new StructureSet();
        }
    };
    private static final Pointer PLACEHOLDER_MEMORY = new Pointer(0L){

        @Override
        public Pointer share(long offset, long sz2) {
            return this;
        }
    };

    protected Structure() {
        this(0);
    }

    protected Structure(TypeMapper mapper) {
        this(null, 0, mapper);
    }

    protected Structure(int alignType) {
        this(null, alignType);
    }

    protected Structure(int alignType, TypeMapper mapper) {
        this(null, alignType, mapper);
    }

    protected Structure(Pointer p2) {
        this(p2, 0);
    }

    protected Structure(Pointer p2, int alignType) {
        this(p2, alignType, null);
    }

    protected Structure(Pointer p2, int alignType, TypeMapper mapper) {
        this.setAlignType(alignType);
        this.setStringEncoding(Native.getStringEncoding(this.getClass()));
        this.initializeTypeMapper(mapper);
        this.validateFields();
        if (p2 != null) {
            this.useMemory(p2, 0, true);
        } else {
            this.allocateMemory(-1);
        }
        this.initializeFields();
    }

    Map<String, StructField> fields() {
        return this.structFields;
    }

    TypeMapper getTypeMapper() {
        return this.typeMapper;
    }

    private void initializeTypeMapper(TypeMapper mapper) {
        if (mapper == null) {
            mapper = Native.getTypeMapper(this.getClass());
        }
        this.typeMapper = mapper;
        this.layoutChanged();
    }

    private void layoutChanged() {
        if (this.size != -1) {
            this.size = -1;
            if (this.memory instanceof AutoAllocated) {
                this.memory = null;
            }
            this.ensureAllocated();
        }
    }

    protected void setStringEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected String getStringEncoding() {
        return this.encoding;
    }

    protected void setAlignType(int alignType) {
        this.alignType = alignType;
        if (alignType == 0 && (alignType = Native.getStructureAlignment(this.getClass())) == 0) {
            alignType = Platform.isWindows() ? 3 : 2;
        }
        this.actualAlignType = alignType;
        this.layoutChanged();
    }

    protected Memory autoAllocate(int size) {
        return new AutoAllocated(size);
    }

    protected void useMemory(Pointer m2) {
        this.useMemory(m2, 0);
    }

    protected void useMemory(Pointer m2, int offset) {
        this.useMemory(m2, offset, false);
    }

    void useMemory(Pointer m2, int offset, boolean force) {
        try {
            this.nativeStrings.clear();
            if (this instanceof ByValue && !force) {
                byte[] buf2 = new byte[this.size()];
                m2.read(0L, buf2, 0, buf2.length);
                this.memory.write(0L, buf2, 0, buf2.length);
            } else {
                this.memory = m2.share(offset);
                if (this.size == -1) {
                    this.size = this.calculateSize(false);
                }
                if (this.size != -1) {
                    this.memory = m2.share(offset, this.size);
                }
            }
            this.array = null;
            this.readCalled = false;
        }
        catch (IndexOutOfBoundsException e2) {
            throw new IllegalArgumentException("Structure exceeds provided memory bounds", e2);
        }
    }

    protected void ensureAllocated() {
        this.ensureAllocated(false);
    }

    private void ensureAllocated(boolean avoidFFIType) {
        if (this.memory == null) {
            this.allocateMemory(avoidFFIType);
        } else if (this.size == -1) {
            this.size = this.calculateSize(true, avoidFFIType);
            if (!(this.memory instanceof AutoAllocated)) {
                try {
                    this.memory = this.memory.share(0L, this.size);
                }
                catch (IndexOutOfBoundsException e2) {
                    throw new IllegalArgumentException("Structure exceeds provided memory bounds", e2);
                }
            }
        }
    }

    protected void allocateMemory() {
        this.allocateMemory(false);
    }

    private void allocateMemory(boolean avoidFFIType) {
        this.allocateMemory(this.calculateSize(true, avoidFFIType));
    }

    protected void allocateMemory(int size) {
        if (size == -1) {
            size = this.calculateSize(false);
        } else if (size <= 0) {
            throw new IllegalArgumentException("Structure size must be greater than zero: " + size);
        }
        if (size != -1) {
            if (this.memory == null || this.memory instanceof AutoAllocated) {
                this.memory = this.autoAllocate(size);
            }
            this.size = size;
        }
    }

    public int size() {
        this.ensureAllocated();
        return this.size;
    }

    public void clear() {
        this.ensureAllocated();
        this.memory.clear(this.size());
    }

    public Pointer getPointer() {
        this.ensureAllocated();
        return this.memory;
    }

    static Set<Structure> busy() {
        return busy.get();
    }

    static Map<Pointer, Structure> reading() {
        return reads.get();
    }

    void conditionalAutoRead() {
        if (!this.readCalled) {
            this.autoRead();
        }
    }

    public void read() {
        if (this.memory == PLACEHOLDER_MEMORY) {
            return;
        }
        this.readCalled = true;
        this.ensureAllocated();
        if (Structure.busy().contains(this)) {
            return;
        }
        Structure.busy().add(this);
        if (this instanceof ByReference) {
            Structure.reading().put(this.getPointer(), this);
        }
        try {
            for (StructField structField : this.fields().values()) {
                this.readField(structField);
            }
        }
        finally {
            Structure.busy().remove(this);
            if (Structure.reading().get(this.getPointer()) == this) {
                Structure.reading().remove(this.getPointer());
            }
        }
    }

    protected int fieldOffset(String name) {
        this.ensureAllocated();
        StructField f2 = this.fields().get(name);
        if (f2 == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        return f2.offset;
    }

    public Object readField(String name) {
        this.ensureAllocated();
        StructField f2 = this.fields().get(name);
        if (f2 == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        return this.readField(f2);
    }

    Object getFieldValue(Field field) {
        try {
            return field.get(this);
        }
        catch (Exception e2) {
            throw new Error("Exception reading field '" + field.getName() + "' in " + this.getClass(), e2);
        }
    }

    void setFieldValue(Field field, Object value) {
        this.setFieldValue(field, value, false);
    }

    private void setFieldValue(Field field, Object value, boolean overrideFinal) {
        try {
            field.set(this, value);
        }
        catch (IllegalAccessException e2) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                if (overrideFinal) {
                    throw new UnsupportedOperationException("This VM does not support Structures with final fields (field '" + field.getName() + "' within " + this.getClass() + ")", e2);
                }
                throw new UnsupportedOperationException("Attempt to write to read-only field '" + field.getName() + "' within " + this.getClass(), e2);
            }
            throw new Error("Unexpectedly unable to write to field '" + field.getName() + "' within " + this.getClass(), e2);
        }
    }

    static Structure updateStructureByReference(Class<?> type, Structure s2, Pointer address) {
        if (address == null) {
            s2 = null;
        } else if (s2 == null || !address.equals(s2.getPointer())) {
            Structure s1 = Structure.reading().get(address);
            if (s1 != null && type.equals(s1.getClass())) {
                s2 = s1;
                s2.autoRead();
            } else {
                s2 = Structure.newInstance(type, address);
                s2.conditionalAutoRead();
            }
        } else {
            s2.autoRead();
        }
        return s2;
    }

    protected Object readField(StructField structField) {
        Pointer p2;
        Object currentValue;
        int offset = structField.offset;
        Class<?> fieldType = structField.type;
        FromNativeConverter readConverter = structField.readConverter;
        if (readConverter != null) {
            fieldType = readConverter.nativeType();
        }
        Object object = currentValue = Structure.class.isAssignableFrom(fieldType) || Callback.class.isAssignableFrom(fieldType) || Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(fieldType) || Pointer.class.isAssignableFrom(fieldType) || NativeMapped.class.isAssignableFrom(fieldType) || fieldType.isArray() ? this.getFieldValue(structField.field) : null;
        Object result = fieldType == String.class ? ((p2 = this.memory.getPointer(offset)) == null ? null : p2.getString(0L, this.encoding)) : this.memory.getValue(offset, fieldType, currentValue);
        if (readConverter != null) {
            result = readConverter.fromNative(result, structField.context);
            if (currentValue != null && currentValue.equals(result)) {
                result = currentValue;
            }
        }
        if (fieldType.equals(String.class) || fieldType.equals(WString.class)) {
            this.nativeStrings.put(structField.name + ".ptr", this.memory.getPointer(offset));
            this.nativeStrings.put(structField.name + ".val", result);
        }
        this.setFieldValue(structField.field, result, true);
        return result;
    }

    public void write() {
        if (this.memory == PLACEHOLDER_MEMORY) {
            return;
        }
        this.ensureAllocated();
        if (this instanceof ByValue) {
            this.getTypeInfo();
        }
        if (Structure.busy().contains(this)) {
            return;
        }
        Structure.busy().add(this);
        try {
            for (StructField sf2 : this.fields().values()) {
                if (sf2.isVolatile) continue;
                this.writeField(sf2);
            }
        }
        finally {
            Structure.busy().remove(this);
        }
    }

    public void writeField(String name) {
        this.ensureAllocated();
        StructField f2 = this.fields().get(name);
        if (f2 == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        this.writeField(f2);
    }

    public void writeField(String name, Object value) {
        this.ensureAllocated();
        StructField structField = this.fields().get(name);
        if (structField == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        this.setFieldValue(structField.field, value);
        this.writeField(structField);
    }

    protected void writeField(StructField structField) {
        if (structField.isReadOnly) {
            return;
        }
        int offset = structField.offset;
        Object value = this.getFieldValue(structField.field);
        Class<?> fieldType = structField.type;
        ToNativeConverter converter = structField.writeConverter;
        if (converter != null) {
            value = converter.toNative(value, new StructureWriteContext(this, structField.field));
            fieldType = converter.nativeType();
        }
        if (String.class == fieldType || WString.class == fieldType) {
            boolean wide;
            boolean bl2 = wide = fieldType == WString.class;
            if (value != null) {
                if (this.nativeStrings.containsKey(structField.name + ".ptr") && value.equals(this.nativeStrings.get(structField.name + ".val"))) {
                    return;
                }
                NativeString nativeString = wide ? new NativeString(value.toString(), true) : new NativeString(value.toString(), this.encoding);
                this.nativeStrings.put(structField.name, nativeString);
                value = nativeString.getPointer();
            } else {
                this.nativeStrings.remove(structField.name);
            }
            this.nativeStrings.remove(structField.name + ".ptr");
            this.nativeStrings.remove(structField.name + ".val");
        }
        try {
            this.memory.setValue(offset, value, fieldType);
        }
        catch (IllegalArgumentException e2) {
            String msg = "Structure field \"" + structField.name + "\" was declared as " + structField.type + (structField.type == fieldType ? "" : " (native type " + fieldType + ")") + ", which is not supported within a Structure";
            throw new IllegalArgumentException(msg, e2);
        }
    }

    protected abstract List<String> getFieldOrder();

    @Deprecated
    protected final void setFieldOrder(String[] fields) {
        throw new Error("This method is obsolete, use getFieldOrder() instead");
    }

    protected void sortFields(List<Field> fields, List<String> names) {
        block0: for (int i2 = 0; i2 < names.size(); ++i2) {
            String name = names.get(i2);
            for (int f2 = 0; f2 < fields.size(); ++f2) {
                Field field = fields.get(f2);
                if (!name.equals(field.getName())) continue;
                Collections.swap(fields, i2, f2);
                continue block0;
            }
        }
    }

    protected List<Field> getFieldList() {
        ArrayList<Field> flist = new ArrayList<Field>();
        Class<?> cls = this.getClass();
        while (!cls.equals(Structure.class)) {
            ArrayList<Field> classFields = new ArrayList<Field>();
            Field[] fields = cls.getDeclaredFields();
            for (int i2 = 0; i2 < fields.length; ++i2) {
                int modifiers = fields[i2].getModifiers();
                if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) continue;
                classFields.add(fields[i2]);
            }
            flist.addAll(0, classFields);
            cls = cls.getSuperclass();
        }
        return flist;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<String> fieldOrder() {
        Class<?> clazz = this.getClass();
        Map<Class<?>, List<String>> map = fieldOrder;
        synchronized (map) {
            List<String> list = fieldOrder.get(clazz);
            if (list == null) {
                list = this.getFieldOrder();
                fieldOrder.put(clazz, list);
            }
            return list;
        }
    }

    public static List<String> createFieldsOrder(List<String> baseFields, String ... extraFields) {
        return Structure.createFieldsOrder(baseFields, Arrays.asList(extraFields));
    }

    public static List<String> createFieldsOrder(List<String> baseFields, List<String> extraFields) {
        ArrayList<String> fields = new ArrayList<String>(baseFields.size() + extraFields.size());
        fields.addAll(baseFields);
        fields.addAll(extraFields);
        return Collections.unmodifiableList(fields);
    }

    public static List<String> createFieldsOrder(String field) {
        return Collections.unmodifiableList(Collections.singletonList(field));
    }

    public static List<String> createFieldsOrder(String ... fields) {
        return Collections.unmodifiableList(Arrays.asList(fields));
    }

    private static <T extends Comparable<T>> List<T> sort(Collection<? extends T> c2) {
        ArrayList<? extends T> list = new ArrayList<T>(c2);
        Collections.sort(list);
        return list;
    }

    protected List<Field> getFields(boolean force) {
        List<Field> flist = this.getFieldList();
        HashSet<String> names = new HashSet<String>();
        for (Field f2 : flist) {
            names.add(f2.getName());
        }
        List<String> fieldOrder = this.fieldOrder();
        if (fieldOrder.size() != flist.size() && flist.size() > 1) {
            if (force) {
                throw new Error("Structure.getFieldOrder() on " + this.getClass() + " does not provide enough names [" + fieldOrder.size() + "] (" + Structure.sort(fieldOrder) + ") to match declared fields [" + flist.size() + "] (" + Structure.sort(names) + ")");
            }
            return null;
        }
        HashSet<String> orderedNames = new HashSet<String>(fieldOrder);
        if (!orderedNames.equals(names)) {
            throw new Error("Structure.getFieldOrder() on " + this.getClass() + " returns names (" + Structure.sort(fieldOrder) + ") which do not match declared field names (" + Structure.sort(names) + ")");
        }
        this.sortFields(flist, fieldOrder);
        return flist;
    }

    protected int calculateSize(boolean force) {
        return this.calculateSize(force, false);
    }

    static int size(Class<?> type) {
        return Structure.size(type, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int size(Class<?> type, Structure value) {
        int sz2;
        LayoutInfo info;
        Map<Class<?>, LayoutInfo> map = layoutInfo;
        synchronized (map) {
            info = layoutInfo.get(type);
        }
        int n2 = sz2 = info != null && !info.variable ? info.size : -1;
        if (sz2 == -1) {
            if (value == null) {
                value = Structure.newInstance(type, PLACEHOLDER_MEMORY);
            }
            sz2 = value.size();
        }
        return sz2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int calculateSize(boolean force, boolean avoidFFIType) {
        LayoutInfo info;
        int size = -1;
        Class<?> clazz = this.getClass();
        Map<Class<?>, LayoutInfo> map = layoutInfo;
        synchronized (map) {
            info = layoutInfo.get(clazz);
        }
        if (info == null || this.alignType != info.alignType || this.typeMapper != info.typeMapper) {
            info = this.deriveLayout(force, avoidFFIType);
        }
        if (info != null) {
            this.structAlignment = info.alignment;
            this.structFields = info.fields;
            if (!info.variable) {
                map = layoutInfo;
                synchronized (map) {
                    if (!layoutInfo.containsKey(clazz) || this.alignType != 0 || this.typeMapper != null) {
                        layoutInfo.put(clazz, info);
                    }
                }
            }
            size = info.size;
        }
        return size;
    }

    private void validateField(String name, Class<?> type) {
        ToNativeConverter toNative;
        if (this.typeMapper != null && (toNative = this.typeMapper.getToNativeConverter(type)) != null) {
            this.validateField(name, toNative.nativeType());
            return;
        }
        if (type.isArray()) {
            this.validateField(name, type.getComponentType());
        } else {
            try {
                this.getNativeSize(type);
            }
            catch (IllegalArgumentException e2) {
                String msg = "Invalid Structure field in " + this.getClass() + ", field name '" + name + "' (" + type + "): " + e2.getMessage();
                throw new IllegalArgumentException(msg, e2);
            }
        }
    }

    private void validateFields() {
        List<Field> fields = this.getFieldList();
        for (Field f2 : fields) {
            this.validateField(f2.getName(), f2.getType());
        }
    }

    private LayoutInfo deriveLayout(boolean force, boolean avoidFFIType) {
        int calculatedSize = 0;
        List<Field> fields = this.getFields(force);
        if (fields == null) {
            return null;
        }
        LayoutInfo info = new LayoutInfo();
        info.alignType = this.alignType;
        info.typeMapper = this.typeMapper;
        boolean firstField = true;
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            Class<?> type = field.getType();
            if (type.isArray()) {
                info.variable = true;
            }
            StructField structField = new StructField();
            structField.isVolatile = Modifier.isVolatile(modifiers);
            structField.isReadOnly = Modifier.isFinal(modifiers);
            if (structField.isReadOnly) {
                if (!Platform.RO_FIELDS) {
                    throw new IllegalArgumentException("This VM does not support read-only fields (field '" + field.getName() + "' within " + this.getClass() + ")");
                }
                field.setAccessible(true);
            }
            structField.field = field;
            structField.name = field.getName();
            structField.type = type;
            if (Callback.class.isAssignableFrom(type) && !type.isInterface()) {
                throw new IllegalArgumentException("Structure Callback field '" + field.getName() + "' must be an interface");
            }
            if (type.isArray() && Structure.class.equals(type.getComponentType())) {
                String msg = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
                throw new IllegalArgumentException(msg);
            }
            int fieldAlignment = 1;
            if (Modifier.isPublic(field.getModifiers())) {
                Object value = this.getFieldValue(structField.field);
                if (value == null && type.isArray()) {
                    if (force) {
                        throw new IllegalStateException("Array fields must be initialized");
                    }
                    return null;
                }
                Class<Object> nativeType = type;
                if (NativeMapped.class.isAssignableFrom(type)) {
                    NativeMappedConverter tc2 = NativeMappedConverter.getInstance(type);
                    nativeType = tc2.nativeType();
                    structField.writeConverter = tc2;
                    structField.readConverter = tc2;
                    structField.context = new StructureReadContext(this, field);
                } else if (this.typeMapper != null) {
                    ToNativeConverter writeConverter = this.typeMapper.getToNativeConverter(type);
                    FromNativeConverter readConverter = this.typeMapper.getFromNativeConverter(type);
                    if (writeConverter != null && readConverter != null) {
                        nativeType = (value = writeConverter.toNative(value, new StructureWriteContext(this, structField.field))) != null ? value.getClass() : Pointer.class;
                        structField.writeConverter = writeConverter;
                        structField.readConverter = readConverter;
                        structField.context = new StructureReadContext(this, field);
                    } else if (writeConverter != null || readConverter != null) {
                        String msg = "Structures require bidirectional type conversion for " + type;
                        throw new IllegalArgumentException(msg);
                    }
                }
                if (value == null) {
                    value = this.initializeField(structField.field, type);
                }
                try {
                    structField.size = this.getNativeSize(nativeType, value);
                    fieldAlignment = this.getNativeAlignment(nativeType, value, firstField);
                }
                catch (IllegalArgumentException e2) {
                    if (!force && this.typeMapper == null) {
                        return null;
                    }
                    String msg = "Invalid Structure field in " + this.getClass() + ", field name '" + structField.name + "' (" + structField.type + "): " + e2.getMessage();
                    throw new IllegalArgumentException(msg, e2);
                }
                if (fieldAlignment == 0) {
                    throw new Error("Field alignment is zero for field '" + structField.name + "' within " + this.getClass());
                }
                info.alignment = Math.max(info.alignment, fieldAlignment);
                if (calculatedSize % fieldAlignment != 0) {
                    calculatedSize += fieldAlignment - calculatedSize % fieldAlignment;
                }
                if (this instanceof Union) {
                    structField.offset = 0;
                    calculatedSize = Math.max(calculatedSize, structField.size);
                } else {
                    structField.offset = calculatedSize;
                    calculatedSize += structField.size;
                }
                info.fields.put(structField.name, structField);
                if (info.typeInfoField == null || ((LayoutInfo)info).typeInfoField.size < structField.size || ((LayoutInfo)info).typeInfoField.size == structField.size && Structure.class.isAssignableFrom(structField.type)) {
                    info.typeInfoField = structField;
                }
            }
            firstField = false;
        }
        if (calculatedSize > 0) {
            int size = this.addPadding(calculatedSize, info.alignment);
            if (this instanceof ByValue && !avoidFFIType) {
                this.getTypeInfo();
            }
            info.size = size;
            return info;
        }
        throw new IllegalArgumentException("Structure " + this.getClass() + " has unknown or zero size (ensure all fields are public)");
    }

    private void initializeFields() {
        List<Field> flist = this.getFieldList();
        for (Field f2 : flist) {
            try {
                Object o2 = f2.get(this);
                if (o2 != null) continue;
                this.initializeField(f2, f2.getType());
            }
            catch (Exception e2) {
                throw new Error("Exception reading field '" + f2.getName() + "' in " + this.getClass(), e2);
            }
        }
    }

    private Object initializeField(Field field, Class<?> type) {
        Object value = null;
        if (Structure.class.isAssignableFrom(type) && !ByReference.class.isAssignableFrom(type)) {
            try {
                value = Structure.newInstance(type, PLACEHOLDER_MEMORY);
                this.setFieldValue(field, value);
            }
            catch (IllegalArgumentException e2) {
                String msg = "Can't determine size of nested structure";
                throw new IllegalArgumentException(msg, e2);
            }
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc2 = NativeMappedConverter.getInstance(type);
            value = tc2.defaultValue();
            this.setFieldValue(field, value);
        }
        return value;
    }

    private int addPadding(int calculatedSize) {
        return this.addPadding(calculatedSize, this.structAlignment);
    }

    private int addPadding(int calculatedSize, int alignment) {
        if (this.actualAlignType != 1 && calculatedSize % alignment != 0) {
            calculatedSize += alignment - calculatedSize % alignment;
        }
        return calculatedSize;
    }

    protected int getStructAlignment() {
        if (this.size == -1) {
            this.calculateSize(true);
        }
        return this.structAlignment;
    }

    protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
        int alignment = 1;
        if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc2 = NativeMappedConverter.getInstance(type);
            type = tc2.nativeType();
            value = tc2.toNative(value, new ToNativeContext());
        }
        int size = Native.getNativeSize(type, value);
        if (type.isPrimitive() || Long.class == type || Integer.class == type || Short.class == type || Character.class == type || Byte.class == type || Boolean.class == type || Float.class == type || Double.class == type) {
            alignment = size;
        } else if (Pointer.class.isAssignableFrom(type) && !Function.class.isAssignableFrom(type) || Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type) || Callback.class.isAssignableFrom(type) || WString.class == type || String.class == type) {
            alignment = Pointer.SIZE;
        } else if (Structure.class.isAssignableFrom(type)) {
            if (ByReference.class.isAssignableFrom(type)) {
                alignment = Pointer.SIZE;
            } else {
                if (value == null) {
                    value = Structure.newInstance(type, PLACEHOLDER_MEMORY);
                }
                alignment = ((Structure)value).getStructAlignment();
            }
        } else if (type.isArray()) {
            alignment = this.getNativeAlignment(type.getComponentType(), null, isFirstElement);
        } else {
            throw new IllegalArgumentException("Type " + type + " has unknown native alignment");
        }
        if (this.actualAlignType == 1) {
            alignment = 1;
        } else if (this.actualAlignType == 3) {
            alignment = Math.min(8, alignment);
        } else if (this.actualAlignType == 2) {
            if (!(isFirstElement && Platform.isMac() && Platform.isPPC())) {
                alignment = Math.min(Native.MAX_ALIGNMENT, alignment);
            }
            if (!isFirstElement && Platform.isAIX() && (type == Double.TYPE || type == Double.class)) {
                alignment = 4;
            }
        }
        return alignment;
    }

    public String toString() {
        return this.toString(Boolean.getBoolean("jna.dump_memory"));
    }

    public String toString(boolean debug) {
        return this.toString(0, true, debug);
    }

    private String format(Class<?> type) {
        String s2 = type.getName();
        int dot = s2.lastIndexOf(".");
        return s2.substring(dot + 1);
    }

    private String toString(int indent, boolean showContents, boolean dumpMemory) {
        this.ensureAllocated();
        String LS = System.getProperty("line.separator");
        String name = this.format(this.getClass()) + "(" + this.getPointer() + ")";
        if (!(this.getPointer() instanceof Memory)) {
            name = name + " (" + this.size() + " bytes)";
        }
        String prefix = "";
        for (int idx = 0; idx < indent; ++idx) {
            prefix = prefix + "  ";
        }
        String contents = LS;
        if (!showContents) {
            contents = "...}";
        } else {
            Iterator<StructField> i2 = this.fields().values().iterator();
            while (i2.hasNext()) {
                StructField sf2 = i2.next();
                Object value = this.getFieldValue(sf2.field);
                String type = this.format(sf2.type);
                String index = "";
                contents = contents + prefix;
                if (sf2.type.isArray() && value != null) {
                    type = this.format(sf2.type.getComponentType());
                    index = "[" + Array.getLength(value) + "]";
                }
                contents = contents + "  " + type + " " + sf2.name + index + "@" + Integer.toHexString(sf2.offset);
                if (value instanceof Structure) {
                    value = ((Structure)value).toString(indent + 1, !(value instanceof ByReference), dumpMemory);
                }
                contents = contents + "=";
                contents = value instanceof Long ? contents + Long.toHexString((Long)value) : (value instanceof Integer ? contents + Integer.toHexString((Integer)value) : (value instanceof Short ? contents + Integer.toHexString(((Short)value).shortValue()) : (value instanceof Byte ? contents + Integer.toHexString(((Byte)value).byteValue()) : contents + String.valueOf(value).trim())));
                contents = contents + LS;
                if (i2.hasNext()) continue;
                contents = contents + prefix + "}";
            }
        }
        if (indent == 0 && dumpMemory) {
            int BYTES_PER_ROW = 4;
            contents = contents + LS + "memory dump" + LS;
            byte[] buf2 = this.getPointer().getByteArray(0L, this.size());
            for (int i3 = 0; i3 < buf2.length; ++i3) {
                if (i3 % 4 == 0) {
                    contents = contents + "[";
                }
                if (buf2[i3] >= 0 && buf2[i3] < 16) {
                    contents = contents + "0";
                }
                contents = contents + Integer.toHexString(buf2[i3] & 0xFF);
                if (i3 % 4 != 3 || i3 >= buf2.length - 1) continue;
                contents = contents + "]" + LS;
            }
            contents = contents + "]";
        }
        return name + " {" + contents;
    }

    public Structure[] toArray(Structure[] array) {
        this.ensureAllocated();
        if (this.memory instanceof AutoAllocated) {
            Memory m2 = (Memory)this.memory;
            int requiredSize = array.length * this.size();
            if (m2.size() < (long)requiredSize) {
                this.useMemory(this.autoAllocate(requiredSize));
            }
        }
        array[0] = this;
        int size = this.size();
        for (int i2 = 1; i2 < array.length; ++i2) {
            array[i2] = Structure.newInstance(this.getClass(), this.memory.share(i2 * size, size));
            array[i2].conditionalAutoRead();
        }
        if (!(this instanceof ByValue)) {
            this.array = array;
        }
        return array;
    }

    public Structure[] toArray(int size) {
        return this.toArray((Structure[])Array.newInstance(this.getClass(), size));
    }

    private Class<?> baseClass() {
        if ((this instanceof ByReference || this instanceof ByValue) && Structure.class.isAssignableFrom(this.getClass().getSuperclass())) {
            return this.getClass().getSuperclass();
        }
        return this.getClass();
    }

    public boolean dataEquals(Structure s2) {
        return this.dataEquals(s2, false);
    }

    public boolean dataEquals(Structure s2, boolean clear) {
        byte[] ref;
        byte[] data;
        if (clear) {
            s2.getPointer().clear(s2.size());
            s2.write();
            this.getPointer().clear(this.size());
            this.write();
        }
        if ((data = s2.getPointer().getByteArray(0L, s2.size())).length == (ref = this.getPointer().getByteArray(0L, this.size())).length) {
            for (int i2 = 0; i2 < data.length; ++i2) {
                if (data[i2] == ref[i2]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean equals(Object o2) {
        return o2 instanceof Structure && o2.getClass() == this.getClass() && ((Structure)o2).getPointer().equals(this.getPointer());
    }

    public int hashCode() {
        Pointer p2 = this.getPointer();
        if (p2 != null) {
            return this.getPointer().hashCode();
        }
        return this.getClass().hashCode();
    }

    protected void cacheTypeInfo(Pointer p2) {
        this.typeInfo = p2.peer;
    }

    Pointer getFieldTypeInfo(StructField f2) {
        ToNativeConverter nc;
        Class<?> type = f2.type;
        Object value = this.getFieldValue(f2.field);
        if (this.typeMapper != null && (nc = this.typeMapper.getToNativeConverter(type)) != null) {
            type = nc.nativeType();
            value = nc.toNative(value, new ToNativeContext());
        }
        return FFIType.get(value, type);
    }

    Pointer getTypeInfo() {
        Pointer p2 = Structure.getTypeInfo(this);
        this.cacheTypeInfo(p2);
        return p2;
    }

    public void setAutoSynch(boolean auto) {
        this.setAutoRead(auto);
        this.setAutoWrite(auto);
    }

    public void setAutoRead(boolean auto) {
        this.autoRead = auto;
    }

    public boolean getAutoRead() {
        return this.autoRead;
    }

    public void setAutoWrite(boolean auto) {
        this.autoWrite = auto;
    }

    public boolean getAutoWrite() {
        return this.autoWrite;
    }

    static Pointer getTypeInfo(Object obj) {
        return FFIType.get(obj);
    }

    private static Structure newInstance(Class<?> type, long init) {
        try {
            Structure s2 = Structure.newInstance(type, init == 0L ? PLACEHOLDER_MEMORY : new Pointer(init));
            if (init != 0L) {
                s2.conditionalAutoRead();
            }
            return s2;
        }
        catch (Throwable e2) {
            System.err.println("JNA: Error creating structure: " + e2);
            return null;
        }
    }

    public static Structure newInstance(Class<?> type, Pointer init) throws IllegalArgumentException {
        try {
            Constructor<?> ctor = type.getConstructor(Pointer.class);
            return (Structure)ctor.newInstance(init);
        }
        catch (NoSuchMethodException ctor) {
        }
        catch (SecurityException ctor) {
        }
        catch (InstantiationException e2) {
            String msg = "Can't instantiate " + type;
            throw new IllegalArgumentException(msg, e2);
        }
        catch (IllegalAccessException e3) {
            String msg = "Instantiation of " + type + " (Pointer) not allowed, is it public?";
            throw new IllegalArgumentException(msg, e3);
        }
        catch (InvocationTargetException e4) {
            String msg = "Exception thrown while instantiating an instance of " + type;
            e4.printStackTrace();
            throw new IllegalArgumentException(msg, e4);
        }
        Structure s2 = Structure.newInstance(type);
        if (init != PLACEHOLDER_MEMORY) {
            s2.useMemory(init);
        }
        return s2;
    }

    public static Structure newInstance(Class<?> type) throws IllegalArgumentException {
        try {
            Structure s2 = (Structure)type.newInstance();
            if (s2 instanceof ByValue) {
                s2.allocateMemory();
            }
            return s2;
        }
        catch (InstantiationException e2) {
            String msg = "Can't instantiate " + type;
            throw new IllegalArgumentException(msg, e2);
        }
        catch (IllegalAccessException e3) {
            String msg = "Instantiation of " + type + " not allowed, is it public?";
            throw new IllegalArgumentException(msg, e3);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    StructField typeInfoField() {
        LayoutInfo info;
        Map<Class<?>, LayoutInfo> map = layoutInfo;
        synchronized (map) {
            info = layoutInfo.get(this.getClass());
        }
        if (info != null) {
            return info.typeInfoField;
        }
        return null;
    }

    private static void structureArrayCheck(Structure[] ss2) {
        if (ByReference[].class.isAssignableFrom(ss2.getClass())) {
            return;
        }
        Pointer base = ss2[0].getPointer();
        int size = ss2[0].size();
        for (int si2 = 1; si2 < ss2.length; ++si2) {
            if (ss2[si2].getPointer().peer == base.peer + (long)(size * si2)) continue;
            String msg = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + si2 + ")";
            throw new IllegalArgumentException(msg);
        }
    }

    public static void autoRead(Structure[] ss2) {
        Structure.structureArrayCheck(ss2);
        if (ss2[0].array == ss2) {
            ss2[0].autoRead();
        } else {
            for (int si2 = 0; si2 < ss2.length; ++si2) {
                if (ss2[si2] == null) continue;
                ss2[si2].autoRead();
            }
        }
    }

    public void autoRead() {
        if (this.getAutoRead()) {
            this.read();
            if (this.array != null) {
                for (int i2 = 1; i2 < this.array.length; ++i2) {
                    this.array[i2].autoRead();
                }
            }
        }
    }

    public static void autoWrite(Structure[] ss2) {
        Structure.structureArrayCheck(ss2);
        if (ss2[0].array == ss2) {
            ss2[0].autoWrite();
        } else {
            for (int si2 = 0; si2 < ss2.length; ++si2) {
                if (ss2[si2] == null) continue;
                ss2[si2].autoWrite();
            }
        }
    }

    public void autoWrite() {
        if (this.getAutoWrite()) {
            this.write();
            if (this.array != null) {
                for (int i2 = 1; i2 < this.array.length; ++i2) {
                    this.array[i2].autoWrite();
                }
            }
        }
    }

    protected int getNativeSize(Class<?> nativeType) {
        return this.getNativeSize(nativeType, null);
    }

    protected int getNativeSize(Class<?> nativeType, Object value) {
        return Native.getNativeSize(nativeType, value);
    }

    static void validate(Class<?> cls) {
        Structure.newInstance(cls, PLACEHOLDER_MEMORY);
    }

    private static class AutoAllocated
    extends Memory {
        public AutoAllocated(int size) {
            super(size);
            super.clear();
        }

        @Override
        public String toString() {
            return "auto-" + super.toString();
        }
    }

    static class FFIType
    extends Structure {
        private static final Map<Object, Object> typeInfoMap = new WeakHashMap<Object, Object>();
        private static final int FFI_TYPE_STRUCT = 13;
        public size_t size;
        public short alignment;
        public short type = (short)13;
        public Pointer elements;

        private FFIType(Structure ref) {
            Pointer[] els;
            ref.ensureAllocated(true);
            if (ref instanceof Union) {
                StructField sf2 = ((Union)ref).typeInfoField();
                els = new Pointer[]{FFIType.get(ref.getFieldValue(sf2.field), sf2.type), null};
            } else {
                els = new Pointer[ref.fields().size() + 1];
                int idx = 0;
                for (StructField sf3 : ref.fields().values()) {
                    els[idx++] = ref.getFieldTypeInfo(sf3);
                }
            }
            this.init(els);
        }

        private FFIType(Object array, Class<?> type) {
            int length = Array.getLength(array);
            Pointer[] els = new Pointer[length + 1];
            Pointer p2 = FFIType.get(null, type.getComponentType());
            for (int i2 = 0; i2 < length; ++i2) {
                els[i2] = p2;
            }
            this.init(els);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("size", "alignment", "type", "elements");
        }

        private void init(Pointer[] els) {
            this.elements = new Memory(Pointer.SIZE * els.length);
            this.elements.write(0L, els, 0, els.length);
            this.write();
        }

        static Pointer get(Object obj) {
            if (obj == null) {
                return FFITypes.ffi_type_pointer;
            }
            if (obj instanceof Class) {
                return FFIType.get(null, (Class)obj);
            }
            return FFIType.get(obj, obj.getClass());
        }

        private static Pointer get(Object obj, Class<?> cls) {
            ToNativeConverter nc;
            TypeMapper mapper = Native.getTypeMapper(cls);
            if (mapper != null && (nc = mapper.getToNativeConverter(cls)) != null) {
                cls = nc.nativeType();
            }
            Map<Object, Object> map = typeInfoMap;
            synchronized (map) {
                Object o2 = typeInfoMap.get(cls);
                if (o2 instanceof Pointer) {
                    return (Pointer)o2;
                }
                if (o2 instanceof FFIType) {
                    return ((FFIType)o2).getPointer();
                }
                if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(cls) || Callback.class.isAssignableFrom(cls)) {
                    typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                    return FFITypes.ffi_type_pointer;
                }
                if (Structure.class.isAssignableFrom(cls)) {
                    if (obj == null) {
                        obj = FFIType.newInstance(cls, PLACEHOLDER_MEMORY);
                    }
                    if (ByReference.class.isAssignableFrom(cls)) {
                        typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                        return FFITypes.ffi_type_pointer;
                    }
                    FFIType type = new FFIType((Structure)obj);
                    typeInfoMap.put(cls, type);
                    return type.getPointer();
                }
                if (NativeMapped.class.isAssignableFrom(cls)) {
                    NativeMappedConverter c2 = NativeMappedConverter.getInstance(cls);
                    return FFIType.get(c2.toNative(obj, new ToNativeContext()), c2.nativeType());
                }
                if (cls.isArray()) {
                    FFIType type = new FFIType(obj, cls);
                    typeInfoMap.put(obj, type);
                    return type.getPointer();
                }
                throw new IllegalArgumentException("Unsupported type " + cls);
            }
        }

        static {
            if (Native.POINTER_SIZE == 0) {
                throw new Error("Native library not initialized");
            }
            if (FFITypes.ffi_type_void == null) {
                throw new Error("FFI types not initialized");
            }
            typeInfoMap.put(Void.TYPE, FFITypes.ffi_type_void);
            typeInfoMap.put(Void.class, FFITypes.ffi_type_void);
            typeInfoMap.put(Float.TYPE, FFITypes.ffi_type_float);
            typeInfoMap.put(Float.class, FFITypes.ffi_type_float);
            typeInfoMap.put(Double.TYPE, FFITypes.ffi_type_double);
            typeInfoMap.put(Double.class, FFITypes.ffi_type_double);
            typeInfoMap.put(Long.TYPE, FFITypes.ffi_type_sint64);
            typeInfoMap.put(Long.class, FFITypes.ffi_type_sint64);
            typeInfoMap.put(Integer.TYPE, FFITypes.ffi_type_sint32);
            typeInfoMap.put(Integer.class, FFITypes.ffi_type_sint32);
            typeInfoMap.put(Short.TYPE, FFITypes.ffi_type_sint16);
            typeInfoMap.put(Short.class, FFITypes.ffi_type_sint16);
            Pointer ctype = Native.WCHAR_SIZE == 2 ? FFITypes.ffi_type_uint16 : FFITypes.ffi_type_uint32;
            typeInfoMap.put(Character.TYPE, ctype);
            typeInfoMap.put(Character.class, ctype);
            typeInfoMap.put(Byte.TYPE, FFITypes.ffi_type_sint8);
            typeInfoMap.put(Byte.class, FFITypes.ffi_type_sint8);
            typeInfoMap.put(Pointer.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(String.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(WString.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(Boolean.TYPE, FFITypes.ffi_type_uint32);
            typeInfoMap.put(Boolean.class, FFITypes.ffi_type_uint32);
        }

        private static class FFITypes {
            private static Pointer ffi_type_void;
            private static Pointer ffi_type_float;
            private static Pointer ffi_type_double;
            private static Pointer ffi_type_longdouble;
            private static Pointer ffi_type_uint8;
            private static Pointer ffi_type_sint8;
            private static Pointer ffi_type_uint16;
            private static Pointer ffi_type_sint16;
            private static Pointer ffi_type_uint32;
            private static Pointer ffi_type_sint32;
            private static Pointer ffi_type_uint64;
            private static Pointer ffi_type_sint64;
            private static Pointer ffi_type_pointer;

            private FFITypes() {
            }
        }

        public static class size_t
        extends IntegerType {
            private static final long serialVersionUID = 1L;

            public size_t() {
                this(0L);
            }

            public size_t(long value) {
                super(Native.SIZE_T_SIZE, value);
            }
        }
    }

    protected static class StructField {
        public String name;
        public Class<?> type;
        public Field field;
        public int size = -1;
        public int offset = -1;
        public boolean isVolatile;
        public boolean isReadOnly;
        public FromNativeConverter readConverter;
        public ToNativeConverter writeConverter;
        public FromNativeContext context;

        protected StructField() {
        }

        public String toString() {
            return this.name + "@" + this.offset + "[" + this.size + "] (" + this.type + ")";
        }
    }

    private static class LayoutInfo {
        private int size = -1;
        private int alignment = 1;
        private final Map<String, StructField> fields = Collections.synchronizedMap(new LinkedHashMap());
        private int alignType = 0;
        private TypeMapper typeMapper;
        private boolean variable;
        private StructField typeInfoField;

        private LayoutInfo() {
        }
    }

    static class StructureSet
    extends AbstractCollection<Structure>
    implements Set<Structure> {
        Structure[] elements;
        private int count;

        StructureSet() {
        }

        private void ensureCapacity(int size) {
            if (this.elements == null) {
                this.elements = new Structure[size * 3 / 2];
            } else if (this.elements.length < size) {
                Structure[] e2 = new Structure[size * 3 / 2];
                System.arraycopy(this.elements, 0, e2, 0, this.elements.length);
                this.elements = e2;
            }
        }

        public Structure[] getElements() {
            return this.elements;
        }

        @Override
        public int size() {
            return this.count;
        }

        @Override
        public boolean contains(Object o2) {
            return this.indexOf((Structure)o2) != -1;
        }

        @Override
        public boolean add(Structure o2) {
            if (!this.contains(o2)) {
                this.ensureCapacity(this.count + 1);
                this.elements[this.count++] = o2;
            }
            return true;
        }

        private int indexOf(Structure s1) {
            for (int i2 = 0; i2 < this.count; ++i2) {
                Structure s2 = this.elements[i2];
                if (s1 != s2 && (s1.getClass() != s2.getClass() || s1.size() != s2.size() || !s1.getPointer().equals(s2.getPointer()))) continue;
                return i2;
            }
            return -1;
        }

        @Override
        public boolean remove(Object o2) {
            int idx = this.indexOf((Structure)o2);
            if (idx != -1) {
                if (--this.count >= 0) {
                    this.elements[idx] = this.elements[this.count];
                    this.elements[this.count] = null;
                }
                return true;
            }
            return false;
        }

        @Override
        public Iterator<Structure> iterator() {
            Structure[] e2 = new Structure[this.count];
            if (this.count > 0) {
                System.arraycopy(this.elements, 0, e2, 0, this.count);
            }
            return Arrays.asList(e2).iterator();
        }
    }

    public static interface ByReference {
    }

    public static interface ByValue {
    }
}


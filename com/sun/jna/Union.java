package com.sun.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Union
extends Structure {
    private Structure.StructField activeField;

    protected Union() {
    }

    protected Union(Pointer p2) {
        super(p2);
    }

    protected Union(Pointer p2, int alignType) {
        super(p2, alignType);
    }

    protected Union(TypeMapper mapper) {
        super(mapper);
    }

    protected Union(Pointer p2, int alignType, TypeMapper mapper) {
        super(p2, alignType, mapper);
    }

    @Override
    protected List<String> getFieldOrder() {
        List<Field> flist = this.getFieldList();
        ArrayList<String> list = new ArrayList<String>(flist.size());
        for (Field f2 : flist) {
            list.add(f2.getName());
        }
        return list;
    }

    public void setType(Class<?> type) {
        this.ensureAllocated();
        for (Structure.StructField f2 : this.fields().values()) {
            if (f2.type != type) continue;
            this.activeField = f2;
            return;
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    public void setType(String fieldName) {
        this.ensureAllocated();
        Structure.StructField f2 = this.fields().get(fieldName);
        if (f2 == null) {
            throw new IllegalArgumentException("No field named " + fieldName + " in " + this);
        }
        this.activeField = f2;
    }

    @Override
    public Object readField(String fieldName) {
        this.ensureAllocated();
        this.setType(fieldName);
        return super.readField(fieldName);
    }

    @Override
    public void writeField(String fieldName) {
        this.ensureAllocated();
        this.setType(fieldName);
        super.writeField(fieldName);
    }

    @Override
    public void writeField(String fieldName, Object value) {
        this.ensureAllocated();
        this.setType(fieldName);
        super.writeField(fieldName, value);
    }

    public Object getTypedValue(Class<?> type) {
        this.ensureAllocated();
        for (Structure.StructField f2 : this.fields().values()) {
            if (f2.type != type) continue;
            this.activeField = f2;
            this.read();
            return this.getFieldValue(this.activeField.field);
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    public Object setTypedValue(Object object) {
        Structure.StructField f2 = this.findField(object.getClass());
        if (f2 != null) {
            this.activeField = f2;
            this.setFieldValue(f2.field, object);
            return this;
        }
        throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
    }

    private Structure.StructField findField(Class<?> type) {
        this.ensureAllocated();
        for (Structure.StructField f2 : this.fields().values()) {
            if (!f2.type.isAssignableFrom(type)) continue;
            return f2;
        }
        return null;
    }

    @Override
    protected void writeField(Structure.StructField field) {
        if (field == this.activeField) {
            super.writeField(field);
        }
    }

    @Override
    protected Object readField(Structure.StructField field) {
        if (field == this.activeField || !Structure.class.isAssignableFrom(field.type) && !String.class.isAssignableFrom(field.type) && !WString.class.isAssignableFrom(field.type)) {
            return super.readField(field);
        }
        return null;
    }

    @Override
    protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
        return super.getNativeAlignment(type, value, true);
    }
}


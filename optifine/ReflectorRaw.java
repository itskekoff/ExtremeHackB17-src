package optifine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import optifine.Reflector;
import optifine.ReflectorClass;
import optifine.ReflectorField;

public class ReflectorRaw {
    public static Field getField(Class p_getField_0_, Class p_getField_1_) {
        try {
            Field[] afield = p_getField_0_.getDeclaredFields();
            for (int i2 = 0; i2 < afield.length; ++i2) {
                Field field = afield[i2];
                if (field.getType() != p_getField_1_) continue;
                field.setAccessible(true);
                return field;
            }
            return null;
        }
        catch (Exception var5) {
            return null;
        }
    }

    public static Field[] getFields(Class p_getFields_0_, Class p_getFields_1_) {
        try {
            Field[] afield = p_getFields_0_.getDeclaredFields();
            return ReflectorRaw.getFields(afield, p_getFields_1_);
        }
        catch (Exception var3) {
            return null;
        }
    }

    public static Field[] getFields(Field[] p_getFields_0_, Class p_getFields_1_) {
        try {
            ArrayList<Field> list = new ArrayList<Field>();
            for (int i2 = 0; i2 < p_getFields_0_.length; ++i2) {
                Field field = p_getFields_0_[i2];
                if (field.getType() != p_getFields_1_) continue;
                field.setAccessible(true);
                list.add(field);
            }
            Field[] afield = list.toArray(new Field[list.size()]);
            return afield;
        }
        catch (Exception var5) {
            return null;
        }
    }

    public static Field[] getFieldsAfter(Class p_getFieldsAfter_0_, Field p_getFieldsAfter_1_, Class p_getFieldsAfter_2_) {
        try {
            Field[] afield = p_getFieldsAfter_0_.getDeclaredFields();
            List<Field> list = Arrays.asList(afield);
            int i2 = list.indexOf(p_getFieldsAfter_1_);
            if (i2 < 0) {
                return new Field[0];
            }
            List<Field> list1 = list.subList(i2 + 1, list.size());
            Field[] afield1 = list1.toArray(new Field[list1.size()]);
            return ReflectorRaw.getFields(afield1, p_getFieldsAfter_2_);
        }
        catch (Exception var8) {
            return null;
        }
    }

    public static Field[] getFields(Object p_getFields_0_, Field[] p_getFields_1_, Class p_getFields_2_, Object p_getFields_3_) {
        try {
            ArrayList<Field> list = new ArrayList<Field>();
            for (int i2 = 0; i2 < p_getFields_1_.length; ++i2) {
                Field field = p_getFields_1_[i2];
                if (field.getType() != p_getFields_2_) continue;
                boolean flag = Modifier.isStatic(field.getModifiers());
                if (p_getFields_0_ == null && !flag || p_getFields_0_ != null && flag) continue;
                field.setAccessible(true);
                Object object = field.get(p_getFields_0_);
                if (object == p_getFields_3_) {
                    list.add(field);
                    continue;
                }
                if (object == null || p_getFields_3_ == null || !object.equals(p_getFields_3_)) continue;
                list.add(field);
            }
            Field[] afield = list.toArray(new Field[list.size()]);
            return afield;
        }
        catch (Exception var9) {
            return null;
        }
    }

    public static Field getField(Class p_getField_0_, Class p_getField_1_, int p_getField_2_) {
        Field[] afield = ReflectorRaw.getFields(p_getField_0_, p_getField_1_);
        return p_getField_2_ >= 0 && p_getField_2_ < afield.length ? afield[p_getField_2_] : null;
    }

    public static Field getFieldAfter(Class p_getFieldAfter_0_, Field p_getFieldAfter_1_, Class p_getFieldAfter_2_, int p_getFieldAfter_3_) {
        Field[] afield = ReflectorRaw.getFieldsAfter(p_getFieldAfter_0_, p_getFieldAfter_1_, p_getFieldAfter_2_);
        return p_getFieldAfter_3_ >= 0 && p_getFieldAfter_3_ < afield.length ? afield[p_getFieldAfter_3_] : null;
    }

    public static Object getFieldValue(Object p_getFieldValue_0_, Class p_getFieldValue_1_, Class p_getFieldValue_2_) {
        ReflectorField reflectorfield = ReflectorRaw.getReflectorField(p_getFieldValue_1_, p_getFieldValue_2_);
        if (reflectorfield == null) {
            return null;
        }
        return !reflectorfield.exists() ? null : Reflector.getFieldValue(p_getFieldValue_0_, reflectorfield);
    }

    public static Object getFieldValue(Object p_getFieldValue_0_, Class p_getFieldValue_1_, Class p_getFieldValue_2_, int p_getFieldValue_3_) {
        ReflectorField reflectorfield = ReflectorRaw.getReflectorField(p_getFieldValue_1_, p_getFieldValue_2_, p_getFieldValue_3_);
        if (reflectorfield == null) {
            return null;
        }
        return !reflectorfield.exists() ? null : Reflector.getFieldValue(p_getFieldValue_0_, reflectorfield);
    }

    public static boolean setFieldValue(Object p_setFieldValue_0_, Class p_setFieldValue_1_, Class p_setFieldValue_2_, Object p_setFieldValue_3_) {
        ReflectorField reflectorfield = ReflectorRaw.getReflectorField(p_setFieldValue_1_, p_setFieldValue_2_);
        if (reflectorfield == null) {
            return false;
        }
        return !reflectorfield.exists() ? false : Reflector.setFieldValue(p_setFieldValue_0_, reflectorfield, p_setFieldValue_3_);
    }

    public static boolean setFieldValue(Object p_setFieldValue_0_, Class p_setFieldValue_1_, Class p_setFieldValue_2_, int p_setFieldValue_3_, Object p_setFieldValue_4_) {
        ReflectorField reflectorfield = ReflectorRaw.getReflectorField(p_setFieldValue_1_, p_setFieldValue_2_, p_setFieldValue_3_);
        if (reflectorfield == null) {
            return false;
        }
        return !reflectorfield.exists() ? false : Reflector.setFieldValue(p_setFieldValue_0_, reflectorfield, p_setFieldValue_4_);
    }

    public static ReflectorField getReflectorField(Class p_getReflectorField_0_, Class p_getReflectorField_1_) {
        Field field = ReflectorRaw.getField(p_getReflectorField_0_, p_getReflectorField_1_);
        if (field == null) {
            return null;
        }
        ReflectorClass reflectorclass = new ReflectorClass(p_getReflectorField_0_);
        return new ReflectorField(reflectorclass, field.getName());
    }

    public static ReflectorField getReflectorField(Class p_getReflectorField_0_, Class p_getReflectorField_1_, int p_getReflectorField_2_) {
        Field field = ReflectorRaw.getField(p_getReflectorField_0_, p_getReflectorField_1_, p_getReflectorField_2_);
        if (field == null) {
            return null;
        }
        ReflectorClass reflectorclass = new ReflectorClass(p_getReflectorField_0_);
        return new ReflectorField(reflectorclass, field.getName());
    }
}


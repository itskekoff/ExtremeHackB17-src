package optifine;

import optifine.ReflectorClass;
import optifine.ReflectorField;

public class ReflectorFields {
    private ReflectorClass reflectorClass;
    private Class fieldType;
    private int fieldCount;
    private ReflectorField[] reflectorFields;

    public ReflectorFields(ReflectorClass p_i92_1_, Class p_i92_2_, int p_i92_3_) {
        this.reflectorClass = p_i92_1_;
        this.fieldType = p_i92_2_;
        if (p_i92_1_.exists() && p_i92_2_ != null) {
            this.reflectorFields = new ReflectorField[p_i92_3_];
            for (int i2 = 0; i2 < this.reflectorFields.length; ++i2) {
                this.reflectorFields[i2] = new ReflectorField(p_i92_1_, p_i92_2_, i2);
            }
        }
    }

    public ReflectorClass getReflectorClass() {
        return this.reflectorClass;
    }

    public Class getFieldType() {
        return this.fieldType;
    }

    public int getFieldCount() {
        return this.fieldCount;
    }

    public ReflectorField getReflectorField(int p_getReflectorField_1_) {
        return p_getReflectorField_1_ >= 0 && p_getReflectorField_1_ < this.reflectorFields.length ? this.reflectorFields[p_getReflectorField_1_] : null;
    }
}


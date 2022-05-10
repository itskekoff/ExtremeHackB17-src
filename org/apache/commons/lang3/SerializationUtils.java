package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.SerializationException;

public class SerializationUtils {
    public static <T extends Serializable> T clone(T object) {
        if (object == null) {
            return null;
        }
        byte[] objectData = SerializationUtils.serialize(object);
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        ObjectInputStream in2 = null;
        try {
            Serializable readObject;
            in2 = new ClassLoaderAwareObjectInputStream(bais, object.getClass().getClassLoader());
            Serializable serializable = readObject = (Serializable)in2.readObject();
            return (T)serializable;
        }
        catch (ClassNotFoundException ex2) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", ex2);
        }
        catch (IOException ex3) {
            throw new SerializationException("IOException while reading cloned object data", ex3);
        }
        finally {
            try {
                if (in2 != null) {
                    in2.close();
                }
            }
            catch (IOException ex4) {
                throw new SerializationException("IOException on closing cloned object data InputStream.", ex4);
            }
        }
    }

    public static <T extends Serializable> T roundtrip(T msg) {
        return (T)((Serializable)SerializationUtils.deserialize(SerializationUtils.serialize(msg)));
    }

    public static void serialize(Serializable obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
        }
        catch (IOException ex2) {
            throw new SerializationException(ex2);
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        SerializationUtils.serialize(obj, baos);
        return baos.toByteArray();
    }

    public static <T> T deserialize(InputStream inputStream) {
        Object object;
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in2 = null;
        try {
            Object obj;
            in2 = new ObjectInputStream(inputStream);
            object = obj = in2.readObject();
        }
        catch (ClassNotFoundException ex2) {
            throw new SerializationException(ex2);
        }
        catch (IOException ex3) {
            throw new SerializationException(ex3);
        }
        finally {
            try {
                if (in2 != null) {
                    in2.close();
                }
            }
            catch (IOException iOException) {}
        }
        return (T)object;
    }

    public static <T> T deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        return SerializationUtils.deserialize(new ByteArrayInputStream(objectData));
    }

    static class ClassLoaderAwareObjectInputStream
    extends ObjectInputStream {
        private static final Map<String, Class<?>> primitiveTypes = new HashMap();
        private final ClassLoader classLoader;

        public ClassLoaderAwareObjectInputStream(InputStream in2, ClassLoader classLoader) throws IOException {
            super(in2);
            this.classLoader = classLoader;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            try {
                return Class.forName(name, false, this.classLoader);
            }
            catch (ClassNotFoundException ex2) {
                try {
                    return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
                }
                catch (ClassNotFoundException cnfe) {
                    Class<?> cls = primitiveTypes.get(name);
                    if (cls != null) {
                        return cls;
                    }
                    throw cnfe;
                }
            }
        }

        static {
            primitiveTypes.put("byte", Byte.TYPE);
            primitiveTypes.put("short", Short.TYPE);
            primitiveTypes.put("int", Integer.TYPE);
            primitiveTypes.put("long", Long.TYPE);
            primitiveTypes.put("float", Float.TYPE);
            primitiveTypes.put("double", Double.TYPE);
            primitiveTypes.put("boolean", Boolean.TYPE);
            primitiveTypes.put("char", Character.TYPE);
            primitiveTypes.put("void", Void.TYPE);
        }
    }
}


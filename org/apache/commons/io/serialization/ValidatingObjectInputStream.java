package org.apache.commons.io.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.serialization.ClassNameMatcher;
import org.apache.commons.io.serialization.FullClassNameMatcher;
import org.apache.commons.io.serialization.RegexpClassNameMatcher;
import org.apache.commons.io.serialization.WildcardClassNameMatcher;

public class ValidatingObjectInputStream
extends ObjectInputStream {
    private final List<ClassNameMatcher> acceptMatchers = new ArrayList<ClassNameMatcher>();
    private final List<ClassNameMatcher> rejectMatchers = new ArrayList<ClassNameMatcher>();

    public ValidatingObjectInputStream(InputStream input) throws IOException {
        super(input);
    }

    private void validateClassName(String name) throws InvalidClassException {
        for (ClassNameMatcher m2 : this.rejectMatchers) {
            if (!m2.matches(name)) continue;
            this.invalidClassNameFound(name);
        }
        boolean ok2 = false;
        for (ClassNameMatcher m3 : this.acceptMatchers) {
            if (!m3.matches(name)) continue;
            ok2 = true;
            break;
        }
        if (!ok2) {
            this.invalidClassNameFound(name);
        }
    }

    protected void invalidClassNameFound(String className) throws InvalidClassException {
        throw new InvalidClassException("Class name not accepted: " + className);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
        this.validateClassName(osc.getName());
        return super.resolveClass(osc);
    }

    public ValidatingObjectInputStream accept(Class<?> ... classes) {
        for (Class<?> c2 : classes) {
            this.acceptMatchers.add(new FullClassNameMatcher(c2.getName()));
        }
        return this;
    }

    public ValidatingObjectInputStream reject(Class<?> ... classes) {
        for (Class<?> c2 : classes) {
            this.rejectMatchers.add(new FullClassNameMatcher(c2.getName()));
        }
        return this;
    }

    public ValidatingObjectInputStream accept(String ... patterns) {
        for (String pattern : patterns) {
            this.acceptMatchers.add(new WildcardClassNameMatcher(pattern));
        }
        return this;
    }

    public ValidatingObjectInputStream reject(String ... patterns) {
        for (String pattern : patterns) {
            this.rejectMatchers.add(new WildcardClassNameMatcher(pattern));
        }
        return this;
    }

    public ValidatingObjectInputStream accept(Pattern pattern) {
        this.acceptMatchers.add(new RegexpClassNameMatcher(pattern));
        return this;
    }

    public ValidatingObjectInputStream reject(Pattern pattern) {
        this.rejectMatchers.add(new RegexpClassNameMatcher(pattern));
        return this;
    }

    public ValidatingObjectInputStream accept(ClassNameMatcher m2) {
        this.acceptMatchers.add(m2);
        return this;
    }

    public ValidatingObjectInputStream reject(ClassNameMatcher m2) {
        this.rejectMatchers.add(m2);
        return this;
    }
}


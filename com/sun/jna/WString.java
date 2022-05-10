package com.sun.jna;

public final class WString
implements CharSequence,
Comparable {
    private String string;

    public WString(String s2) {
        if (s2 == null) {
            throw new NullPointerException("String initializer must be non-null");
        }
        this.string = s2;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public boolean equals(Object o2) {
        return o2 instanceof WString && this.toString().equals(o2.toString());
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public int compareTo(Object o2) {
        return this.toString().compareTo(o2.toString());
    }

    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public char charAt(int index) {
        return this.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.toString().subSequence(start, end);
    }
}


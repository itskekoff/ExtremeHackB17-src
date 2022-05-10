package io.netty.handler.codec.xml;

public class XmlEntityReference {
    private final String name;
    private final String text;

    public XmlEntityReference(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String name() {
        return this.name;
    }

    public String text() {
        return this.text;
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        XmlEntityReference that = (XmlEntityReference)o2;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(this.text != null ? !this.text.equals(that.text) : that.text != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.text != null ? this.text.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "XmlEntityReference{name='" + this.name + '\'' + ", text='" + this.text + '\'' + '}';
    }
}


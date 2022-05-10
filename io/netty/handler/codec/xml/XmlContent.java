package io.netty.handler.codec.xml;

public abstract class XmlContent {
    private final String data;

    protected XmlContent(String data) {
        this.data = data;
    }

    public String data() {
        return this.data;
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        XmlContent that = (XmlContent)o2;
        return !(this.data != null ? !this.data.equals(that.data) : that.data != null);
    }

    public int hashCode() {
        return this.data != null ? this.data.hashCode() : 0;
    }

    public String toString() {
        return "XmlContent{data='" + this.data + '\'' + '}';
    }
}


package io.netty.handler.codec.xml;

public class XmlDTD {
    private final String text;

    public XmlDTD(String text) {
        this.text = text;
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
        XmlDTD xmlDTD = (XmlDTD)o2;
        return !(this.text != null ? !this.text.equals(xmlDTD.text) : xmlDTD.text != null);
    }

    public int hashCode() {
        return this.text != null ? this.text.hashCode() : 0;
    }

    public String toString() {
        return "XmlDTD{text='" + this.text + '\'' + '}';
    }
}


package com.mojang.patchy;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.core.util.Transform;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.util.Strings;

@Plugin(name="LegacyXMLLayout", category="Core", elementType="layout", printObject=true)
public class LegacyXMLLayout
extends AbstractStringLayout {
    private static final String XML_NAMESPACE = "http://logging.apache.org/log4j/2.0/events";
    private static final String ROOT_TAG = "Events";
    private static final int DEFAULT_SIZE = 256;
    private static final String DEFAULT_EOL = "\r\n";
    private static final String COMPACT_EOL = "";
    private static final String DEFAULT_INDENT = "  ";
    private static final String COMPACT_INDENT = "";
    private static final String DEFAULT_NS_PREFIX = "log4j";
    private static final String[] FORMATS = new String[]{"xml"};
    private final boolean locationInfo;
    private final boolean properties;
    private final boolean complete;
    private final String namespacePrefix;
    private final String eol;
    private final String indent1;
    private final String indent2;
    private final String indent3;

    protected LegacyXMLLayout(boolean locationInfo, boolean properties, boolean complete, boolean compact, String nsPrefix, Charset charset) {
        super(charset);
        this.locationInfo = locationInfo;
        this.properties = properties;
        this.complete = complete;
        this.eol = compact ? "" : DEFAULT_EOL;
        this.indent1 = compact ? "" : DEFAULT_INDENT;
        this.indent2 = this.indent1 + this.indent1;
        this.indent3 = this.indent2 + this.indent1;
        this.namespacePrefix = (Strings.isEmpty(nsPrefix) ? DEFAULT_NS_PREFIX : nsPrefix) + ":";
    }

    @Override
    public String toSerializable(LogEvent event) {
        Throwable throwable;
        StringBuilder buf2 = new StringBuilder(256);
        buf2.append(this.indent1);
        buf2.append('<');
        if (!this.complete) {
            buf2.append(this.namespacePrefix);
        }
        buf2.append("Event logger=\"");
        String name = event.getLoggerName();
        if (name.isEmpty()) {
            name = "root";
        }
        buf2.append(Transform.escapeHtmlTags(name));
        buf2.append("\" timestamp=\"");
        buf2.append(event.getTimeMillis());
        buf2.append("\" level=\"");
        buf2.append(Transform.escapeHtmlTags(String.valueOf(event.getLevel())));
        buf2.append("\" thread=\"");
        buf2.append(Transform.escapeHtmlTags(event.getThreadName()));
        buf2.append("\">");
        buf2.append(this.eol);
        Message msg = event.getMessage();
        if (msg != null) {
            boolean xmlSupported = false;
            if (msg instanceof MultiformatMessage) {
                String[] formats = ((MultiformatMessage)msg).getFormats();
                for (String format : formats) {
                    if (!format.equalsIgnoreCase("XML")) continue;
                    xmlSupported = true;
                    break;
                }
            }
            buf2.append(this.indent2);
            buf2.append('<');
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("Message>");
            if (xmlSupported) {
                buf2.append(((MultiformatMessage)msg).getFormattedMessage(FORMATS));
            } else {
                buf2.append("<![CDATA[");
                Transform.appendEscapingCData(buf2, event.getMessage().getFormattedMessage());
                buf2.append("]]>");
            }
            buf2.append("</");
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("Message>");
            buf2.append(this.eol);
        }
        if (event.getContextStack().getDepth() > 0) {
            buf2.append(this.indent2);
            buf2.append('<');
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("NDC><![CDATA[");
            Transform.appendEscapingCData(buf2, event.getContextStack().toString());
            buf2.append("]]></");
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("NDC>");
            buf2.append(this.eol);
        }
        if ((throwable = event.getThrown()) != null) {
            List<String> s2 = Throwables.toStringList(throwable);
            buf2.append(this.indent2);
            buf2.append('<');
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("Throwable><![CDATA[");
            for (String str : s2) {
                Transform.appendEscapingCData(buf2, str);
                buf2.append(this.eol);
            }
            buf2.append("]]></");
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("Throwable>");
            buf2.append(this.eol);
        }
        if (this.locationInfo) {
            StackTraceElement element = event.getSource();
            buf2.append(this.indent2);
            buf2.append('<');
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("LocationInfo class=\"");
            buf2.append(Transform.escapeHtmlTags(element.getClassName()));
            buf2.append("\" method=\"");
            buf2.append(Transform.escapeHtmlTags(element.getMethodName()));
            buf2.append("\" file=\"");
            buf2.append(Transform.escapeHtmlTags(element.getFileName()));
            buf2.append("\" line=\"");
            buf2.append(element.getLineNumber());
            buf2.append("\"/>");
            buf2.append(this.eol);
        }
        if (this.properties && event.getContextMap().size() > 0) {
            buf2.append(this.indent2);
            buf2.append('<');
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("Properties>");
            buf2.append(this.eol);
            for (Map.Entry<String, String> entry : event.getContextMap().entrySet()) {
                buf2.append(this.indent3);
                buf2.append('<');
                if (!this.complete) {
                    buf2.append(this.namespacePrefix);
                }
                buf2.append("Data name=\"");
                buf2.append(Transform.escapeHtmlTags(entry.getKey()));
                buf2.append("\" value=\"");
                buf2.append(Transform.escapeHtmlTags(String.valueOf(entry.getValue())));
                buf2.append("\"/>");
                buf2.append(this.eol);
            }
            buf2.append(this.indent2);
            buf2.append("</");
            if (!this.complete) {
                buf2.append(this.namespacePrefix);
            }
            buf2.append("Properties>");
            buf2.append(this.eol);
        }
        buf2.append(this.indent1);
        buf2.append("</");
        if (!this.complete) {
            buf2.append(this.namespacePrefix);
        }
        buf2.append("Event>");
        buf2.append(this.eol);
        return buf2.toString();
    }

    @Override
    public byte[] getHeader() {
        if (!this.complete) {
            return null;
        }
        StringBuilder buf2 = new StringBuilder();
        buf2.append("<?xml version=\"1.0\" encoding=\"");
        buf2.append(this.getCharset().name());
        buf2.append("\"?>");
        buf2.append(this.eol);
        buf2.append('<');
        buf2.append(ROOT_TAG);
        buf2.append(" xmlns=\"http://logging.apache.org/log4j/2.0/events\">");
        buf2.append(this.eol);
        return buf2.toString().getBytes(this.getCharset());
    }

    @Override
    public byte[] getFooter() {
        if (!this.complete) {
            return null;
        }
        return ("</Events>" + this.eol).getBytes(this.getCharset());
    }

    @Override
    public Map<String, String> getContentFormat() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("xsd", "log4j-events.xsd");
        result.put("version", "2.0");
        return result;
    }

    @Override
    public String getContentType() {
        return "text/xml; charset=" + this.getCharset();
    }

    @PluginFactory
    public static LegacyXMLLayout createLayout(@PluginAttribute(value="locationInfo") boolean locationInfo, @PluginAttribute(value="properties") boolean properties, @PluginAttribute(value="complete") boolean completeStr, @PluginAttribute(value="compact") boolean compactStr, @PluginAttribute(value="namespacePrefix") String namespacePrefix, @PluginAttribute(value="charset", defaultString="UTF-8") Charset charset) {
        return new LegacyXMLLayout(locationInfo, properties, completeStr, compactStr, namespacePrefix, charset);
    }
}


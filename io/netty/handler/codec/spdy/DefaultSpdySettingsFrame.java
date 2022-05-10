package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.util.internal.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DefaultSpdySettingsFrame
implements SpdySettingsFrame {
    private boolean clear;
    private final Map<Integer, Setting> settingsMap = new TreeMap<Integer, Setting>();

    @Override
    public Set<Integer> ids() {
        return this.settingsMap.keySet();
    }

    @Override
    public boolean isSet(int id2) {
        return this.settingsMap.containsKey(id2);
    }

    @Override
    public int getValue(int id2) {
        Setting setting = this.settingsMap.get(id2);
        return setting != null ? setting.getValue() : -1;
    }

    @Override
    public SpdySettingsFrame setValue(int id2, int value) {
        return this.setValue(id2, value, false, false);
    }

    @Override
    public SpdySettingsFrame setValue(int id2, int value, boolean persistValue, boolean persisted) {
        if (id2 < 0 || id2 > 0xFFFFFF) {
            throw new IllegalArgumentException("Setting ID is not valid: " + id2);
        }
        Integer key = id2;
        Setting setting = this.settingsMap.get(key);
        if (setting != null) {
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
        } else {
            this.settingsMap.put(key, new Setting(value, persistValue, persisted));
        }
        return this;
    }

    @Override
    public SpdySettingsFrame removeValue(int id2) {
        this.settingsMap.remove(id2);
        return this;
    }

    @Override
    public boolean isPersistValue(int id2) {
        Setting setting = this.settingsMap.get(id2);
        return setting != null && setting.isPersist();
    }

    @Override
    public SpdySettingsFrame setPersistValue(int id2, boolean persistValue) {
        Setting setting = this.settingsMap.get(id2);
        if (setting != null) {
            setting.setPersist(persistValue);
        }
        return this;
    }

    @Override
    public boolean isPersisted(int id2) {
        Setting setting = this.settingsMap.get(id2);
        return setting != null && setting.isPersisted();
    }

    @Override
    public SpdySettingsFrame setPersisted(int id2, boolean persisted) {
        Setting setting = this.settingsMap.get(id2);
        if (setting != null) {
            setting.setPersisted(persisted);
        }
        return this;
    }

    @Override
    public boolean clearPreviouslyPersistedSettings() {
        return this.clear;
    }

    @Override
    public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean clear) {
        this.clear = clear;
        return this;
    }

    private Set<Map.Entry<Integer, Setting>> getSettings() {
        return this.settingsMap.entrySet();
    }

    private void appendSettings(StringBuilder buf2) {
        for (Map.Entry<Integer, Setting> e2 : this.getSettings()) {
            Setting setting = e2.getValue();
            buf2.append("--> ");
            buf2.append(e2.getKey());
            buf2.append(':');
            buf2.append(setting.getValue());
            buf2.append(" (persist value: ");
            buf2.append(setting.isPersist());
            buf2.append("; persisted: ");
            buf2.append(setting.isPersisted());
            buf2.append(')');
            buf2.append(StringUtil.NEWLINE);
        }
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder().append(StringUtil.simpleClassName(this)).append(StringUtil.NEWLINE);
        this.appendSettings(buf2);
        buf2.setLength(buf2.length() - StringUtil.NEWLINE.length());
        return buf2.toString();
    }

    private static final class Setting {
        private int value;
        private boolean persist;
        private boolean persisted;

        Setting(int value, boolean persist, boolean persisted) {
            this.value = value;
            this.persist = persist;
            this.persisted = persisted;
        }

        int getValue() {
            return this.value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isPersist() {
            return this.persist;
        }

        void setPersist(boolean persist) {
            this.persist = persist;
        }

        boolean isPersisted() {
            return this.persisted;
        }

        void setPersisted(boolean persisted) {
            this.persisted = persisted;
        }
    }
}


package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class Locale {
    private static final Splitter SPLITTER = Splitter.on('=').limit(2);
    private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    Map<String, String> properties = Maps.newHashMap();
    private boolean unicode;

    public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageList) {
        this.properties.clear();
        for (String s2 : languageList) {
            String s1 = String.format("lang/%s.lang", s2);
            for (String s22 : resourceManager.getResourceDomains()) {
                try {
                    this.loadLocaleData(resourceManager.getAllResources(new ResourceLocation(s22, s1)));
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        this.checkUnicode();
    }

    public boolean isUnicode() {
        return this.unicode;
    }

    private void checkUnicode() {
        this.unicode = false;
        int i2 = 0;
        int j2 = 0;
        for (String s2 : this.properties.values()) {
            int k2 = s2.length();
            j2 += k2;
            for (int l2 = 0; l2 < k2; ++l2) {
                if (s2.charAt(l2) < '\u0100') continue;
                ++i2;
            }
        }
        float f2 = (float)i2 / (float)j2;
        this.unicode = (double)f2 > 0.1;
    }

    private void loadLocaleData(List<IResource> resourcesList) throws IOException {
        for (IResource iresource : resourcesList) {
            InputStream inputstream = iresource.getInputStream();
            try {
                this.loadLocaleData(inputstream);
            }
            finally {
                IOUtils.closeQuietly(inputstream);
            }
        }
    }

    private void loadLocaleData(InputStream inputStreamIn) throws IOException {
        for (String s2 : IOUtils.readLines(inputStreamIn, StandardCharsets.UTF_8)) {
            String[] astring;
            if (s2.isEmpty() || s2.charAt(0) == '#' || (astring = Iterables.toArray(SPLITTER.split(s2), String.class)) == null || astring.length != 2) continue;
            String s1 = astring[0];
            String s22 = PATTERN.matcher(astring[1]).replaceAll("%$1s");
            this.properties.put(s1, s22);
        }
    }

    private String translateKeyPrivate(String translateKey) {
        String s2 = this.properties.get(translateKey);
        return s2 == null ? translateKey : s2;
    }

    public String formatMessage(String translateKey, Object[] parameters) {
        String s2 = this.translateKeyPrivate(translateKey);
        try {
            return String.format(s2, parameters);
        }
        catch (IllegalFormatException var5) {
            return "Format error: " + s2;
        }
    }

    public boolean hasKey(String key) {
        return this.properties.containsKey(key);
    }
}


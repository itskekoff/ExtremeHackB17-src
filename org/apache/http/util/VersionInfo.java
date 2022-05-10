package org.apache.http.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.apache.http.util.Args;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VersionInfo {
    public static final String UNAVAILABLE = "UNAVAILABLE";
    public static final String VERSION_PROPERTY_FILE = "version.properties";
    public static final String PROPERTY_MODULE = "info.module";
    public static final String PROPERTY_RELEASE = "info.release";
    public static final String PROPERTY_TIMESTAMP = "info.timestamp";
    private final String infoPackage;
    private final String infoModule;
    private final String infoRelease;
    private final String infoTimestamp;
    private final String infoClassloader;

    protected VersionInfo(String pckg, String module, String release, String time, String clsldr) {
        Args.notNull(pckg, "Package identifier");
        this.infoPackage = pckg;
        this.infoModule = module != null ? module : UNAVAILABLE;
        this.infoRelease = release != null ? release : UNAVAILABLE;
        this.infoTimestamp = time != null ? time : UNAVAILABLE;
        this.infoClassloader = clsldr != null ? clsldr : UNAVAILABLE;
    }

    public final String getPackage() {
        return this.infoPackage;
    }

    public final String getModule() {
        return this.infoModule;
    }

    public final String getRelease() {
        return this.infoRelease;
    }

    public final String getTimestamp() {
        return this.infoTimestamp;
    }

    public final String getClassloader() {
        return this.infoClassloader;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder(20 + this.infoPackage.length() + this.infoModule.length() + this.infoRelease.length() + this.infoTimestamp.length() + this.infoClassloader.length());
        sb2.append("VersionInfo(").append(this.infoPackage).append(':').append(this.infoModule);
        if (!UNAVAILABLE.equals(this.infoRelease)) {
            sb2.append(':').append(this.infoRelease);
        }
        if (!UNAVAILABLE.equals(this.infoTimestamp)) {
            sb2.append(':').append(this.infoTimestamp);
        }
        sb2.append(')');
        if (!UNAVAILABLE.equals(this.infoClassloader)) {
            sb2.append('@').append(this.infoClassloader);
        }
        return sb2.toString();
    }

    public static VersionInfo[] loadVersionInfo(String[] pckgs, ClassLoader clsldr) {
        Args.notNull(pckgs, "Package identifier array");
        ArrayList<VersionInfo> vil = new ArrayList<VersionInfo>(pckgs.length);
        for (String pckg : pckgs) {
            VersionInfo vi2 = VersionInfo.loadVersionInfo(pckg, clsldr);
            if (vi2 == null) continue;
            vil.add(vi2);
        }
        return vil.toArray(new VersionInfo[vil.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static VersionInfo loadVersionInfo(String pckg, ClassLoader clsldr) {
        Properties vip;
        ClassLoader cl2;
        block6: {
            Args.notNull(pckg, "Package identifier");
            cl2 = clsldr != null ? clsldr : Thread.currentThread().getContextClassLoader();
            vip = null;
            try {
                InputStream is2 = cl2.getResourceAsStream(pckg.replace('.', '/') + "/" + VERSION_PROPERTY_FILE);
                if (is2 == null) break block6;
                try {
                    Properties props = new Properties();
                    props.load(is2);
                    vip = props;
                }
                finally {
                    is2.close();
                }
            }
            catch (IOException ex2) {
                // empty catch block
            }
        }
        VersionInfo result = null;
        if (vip != null) {
            result = VersionInfo.fromMap(pckg, vip, cl2);
        }
        return result;
    }

    protected static VersionInfo fromMap(String pckg, Map<?, ?> info, ClassLoader clsldr) {
        Args.notNull(pckg, "Package identifier");
        String module = null;
        String release = null;
        String timestamp = null;
        if (info != null) {
            module = (String)info.get(PROPERTY_MODULE);
            if (module != null && module.length() < 1) {
                module = null;
            }
            if ((release = (String)info.get(PROPERTY_RELEASE)) != null && (release.length() < 1 || release.equals("${pom.version}"))) {
                release = null;
            }
            if ((timestamp = (String)info.get(PROPERTY_TIMESTAMP)) != null && (timestamp.length() < 1 || timestamp.equals("${mvn.timestamp}"))) {
                timestamp = null;
            }
        }
        String clsldrstr = null;
        if (clsldr != null) {
            clsldrstr = clsldr.toString();
        }
        return new VersionInfo(pckg, module, release, timestamp, clsldrstr);
    }

    public static String getUserAgent(String name, String pkg, Class<?> cls) {
        VersionInfo vi2 = VersionInfo.loadVersionInfo(pkg, cls.getClassLoader());
        String release = vi2 != null ? vi2.getRelease() : UNAVAILABLE;
        String javaVersion = System.getProperty("java.version");
        return name + "/" + release + " (Java 1.5 minimum; Java/" + javaVersion + ")";
    }
}


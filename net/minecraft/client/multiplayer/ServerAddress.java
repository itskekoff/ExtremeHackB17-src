package net.minecraft.client.multiplayer;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class ServerAddress {
    private final String ipAddress;
    private final int serverPort;

    private ServerAddress(String address, int port) {
        this.ipAddress = address;
        this.serverPort = port;
    }

    public String getIP() {
        try {
            return IDN.toASCII(this.ipAddress);
        }
        catch (IllegalArgumentException var2) {
            return "";
        }
    }

    public int getPort() {
        return this.serverPort;
    }

    public static ServerAddress fromString(String addrString) {
        int j2;
        int i2;
        if (addrString == null) {
            return null;
        }
        String[] astring = addrString.split(":");
        if (addrString.startsWith("[") && (i2 = addrString.indexOf("]")) > 0) {
            String s2 = addrString.substring(1, i2);
            String s1 = addrString.substring(i2 + 1).trim();
            if (s1.startsWith(":") && !s1.isEmpty()) {
                s1 = s1.substring(1);
                astring = new String[]{s2, s1};
            } else {
                astring = new String[]{s2};
            }
        }
        if (astring.length > 2) {
            astring = new String[]{addrString};
        }
        String s2 = astring[0];
        int n2 = j2 = astring.length > 1 ? ServerAddress.getInt(astring[1], 25565) : 25565;
        if (j2 == 25565) {
            String[] astring1 = ServerAddress.getServerAddress(s2);
            s2 = astring1[0];
            j2 = ServerAddress.getInt(astring1[1], 25565);
        }
        return new ServerAddress(s2, j2);
    }

    private static String[] getServerAddress(String p_78863_0_) {
        try {
            String s2 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext dircontext = new InitialDirContext(hashtable);
            Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + p_78863_0_, new String[]{"SRV"});
            String[] astring = attributes.get("srv").get().toString().split(" ", 4);
            return new String[]{astring[3], astring[2]};
        }
        catch (Throwable var6) {
            return new String[]{p_78863_0_, Integer.toString(25565)};
        }
    }

    private static int getInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        }
        catch (Exception var3) {
            return defaultValue;
        }
    }
}


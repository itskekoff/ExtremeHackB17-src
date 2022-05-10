package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.resolver.dns.NoopDnsServerAddressStreamProvider;
import io.netty.util.NetUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UnixResolverDnsServerAddressStreamProvider
implements DnsServerAddressStreamProvider {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnixResolverDnsServerAddressStreamProvider.class);
    private static final String NAMESERVER_ROW_LABEL = "nameserver";
    private static final String SORTLIST_ROW_LABEL = "sortlist";
    private static final String DOMAIN_ROW_LABEL = "domain";
    private static final String PORT_ROW_LABEL = "port";
    private final DnsServerAddresses defaultNameServerAddresses;
    private final Map<String, DnsServerAddresses> domainToNameServerStreamMap;

    public static DnsServerAddressStreamProvider parseSilently() {
        try {
            UnixResolverDnsServerAddressStreamProvider nameServerCache = new UnixResolverDnsServerAddressStreamProvider("/etc/resolv.conf", "/etc/resolver");
            return nameServerCache.mayOverrideNameServers() ? nameServerCache : NoopDnsServerAddressStreamProvider.INSTANCE;
        }
        catch (Exception e2) {
            logger.debug("failed to parse /etc/resolv.conf and/or /etc/resolver", e2);
            return NoopDnsServerAddressStreamProvider.INSTANCE;
        }
    }

    public UnixResolverDnsServerAddressStreamProvider(File etcResolvConf, File ... etcResolverFiles) throws IOException {
        if (etcResolvConf == null && (etcResolverFiles == null || etcResolverFiles.length == 0)) {
            throw new IllegalArgumentException("no files to parse");
        }
        if (etcResolverFiles != null) {
            this.domainToNameServerStreamMap = UnixResolverDnsServerAddressStreamProvider.parse(etcResolverFiles);
            if (etcResolvConf != null) {
                Map<String, DnsServerAddresses> etcResolvConfMap = UnixResolverDnsServerAddressStreamProvider.parse(etcResolvConf);
                this.defaultNameServerAddresses = etcResolvConfMap.remove(etcResolvConf.getName());
                this.domainToNameServerStreamMap.putAll(etcResolvConfMap);
            } else {
                this.defaultNameServerAddresses = null;
            }
        } else {
            this.domainToNameServerStreamMap = UnixResolverDnsServerAddressStreamProvider.parse(etcResolvConf);
            this.defaultNameServerAddresses = this.domainToNameServerStreamMap.remove(etcResolvConf.getName());
        }
    }

    public UnixResolverDnsServerAddressStreamProvider(String etcResolvConf, String etcResolverDir) throws IOException {
        this(etcResolvConf == null ? null : new File(etcResolvConf), etcResolverDir == null ? null : new File(etcResolverDir).listFiles());
    }

    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        int i2;
        while ((i2 = hostname.indexOf(46, 1)) >= 0 && i2 != hostname.length() - 1) {
            DnsServerAddresses addresses = this.domainToNameServerStreamMap.get(hostname);
            if (addresses != null) {
                return addresses.stream();
            }
            hostname = hostname.substring(i2 + 1);
        }
        return this.defaultNameServerAddresses != null ? this.defaultNameServerAddresses.stream() : null;
    }

    boolean mayOverrideNameServers() {
        return !this.domainToNameServerStreamMap.isEmpty() || this.defaultNameServerAddresses != null && this.defaultNameServerAddresses.stream().next() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Map<String, DnsServerAddresses> parse(File ... etcResolverFiles) throws IOException {
        HashMap<String, DnsServerAddresses> domainToNameServerStreamMap = new HashMap<String, DnsServerAddresses>(etcResolverFiles.length << 1);
        for (File etcResolverFile : etcResolverFiles) {
            if (!etcResolverFile.isFile()) continue;
            FileReader fr = new FileReader(etcResolverFile);
            BufferedReader br2 = null;
            try {
                String line;
                br2 = new BufferedReader(fr);
                ArrayList<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(2);
                String domainName = etcResolverFile.getName();
                int port = 53;
                while ((line = br2.readLine()) != null) {
                    int i2;
                    char c2;
                    if ((line = line.trim()).isEmpty() || (c2 = line.charAt(0)) == '#' || c2 == ';') continue;
                    if (line.startsWith(NAMESERVER_ROW_LABEL)) {
                        i2 = StringUtil.indexOfNonWhiteSpace(line, NAMESERVER_ROW_LABEL.length());
                        if (i2 < 0) {
                            throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". value: " + line);
                        }
                        String maybeIP = line.substring(i2);
                        if (!NetUtil.isValidIpV4Address(maybeIP) && !NetUtil.isValidIpV6Address(maybeIP)) {
                            i2 = maybeIP.lastIndexOf(46);
                            if (i2 + 1 >= maybeIP.length()) {
                                throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". invalid IP value: " + line);
                            }
                            port = Integer.parseInt(maybeIP.substring(i2 + 1));
                            maybeIP = maybeIP.substring(0, i2);
                        }
                        addresses.add(new InetSocketAddress(SocketUtils.addressByName(maybeIP), port));
                        continue;
                    }
                    if (line.startsWith(DOMAIN_ROW_LABEL)) {
                        i2 = StringUtil.indexOfNonWhiteSpace(line, DOMAIN_ROW_LABEL.length());
                        if (i2 < 0) {
                            throw new IllegalArgumentException("error parsing label domain in file " + etcResolverFile + " value: " + line);
                        }
                        domainName = line.substring(i2);
                        if (addresses != null && !addresses.isEmpty()) {
                            UnixResolverDnsServerAddressStreamProvider.putIfAbsent(domainToNameServerStreamMap, domainName, addresses);
                        }
                        addresses = new ArrayList(2);
                        continue;
                    }
                    if (line.startsWith(PORT_ROW_LABEL)) {
                        i2 = StringUtil.indexOfNonWhiteSpace(line, PORT_ROW_LABEL.length());
                        if (i2 < 0) {
                            throw new IllegalArgumentException("error parsing label port in file " + etcResolverFile + " value: " + line);
                        }
                        port = Integer.parseInt(line.substring(i2));
                        continue;
                    }
                    if (!line.startsWith(SORTLIST_ROW_LABEL)) continue;
                    logger.info("row type {} not supported. ignoring line: {}", (Object)SORTLIST_ROW_LABEL, (Object)line);
                }
                if (addresses == null || addresses.isEmpty()) continue;
                UnixResolverDnsServerAddressStreamProvider.putIfAbsent(domainToNameServerStreamMap, domainName, addresses);
            }
            finally {
                if (br2 == null) {
                    fr.close();
                } else {
                    br2.close();
                }
            }
        }
        return domainToNameServerStreamMap;
    }

    private static void putIfAbsent(Map<String, DnsServerAddresses> domainToNameServerStreamMap, String domainName, List<InetSocketAddress> addresses) {
        UnixResolverDnsServerAddressStreamProvider.putIfAbsent(domainToNameServerStreamMap, domainName, DnsServerAddresses.shuffled(addresses));
    }

    private static void putIfAbsent(Map<String, DnsServerAddresses> domainToNameServerStreamMap, String domainName, DnsServerAddresses addresses) {
        DnsServerAddresses existingAddresses = domainToNameServerStreamMap.put(domainName, addresses);
        if (existingAddresses != null) {
            domainToNameServerStreamMap.put(domainName, existingAddresses);
            logger.debug("Domain name {} already maps to addresses {} so new addresses {} will be discarded", domainName, existingAddresses, addresses);
        }
    }
}


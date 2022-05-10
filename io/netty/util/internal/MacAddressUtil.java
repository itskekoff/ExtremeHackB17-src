package io.netty.util.internal;

import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MacAddressUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
    private static final int EUI64_MAC_ADDRESS_LENGTH = 8;
    private static final int EUI48_MAC_ADDRESS_LENGTH = 6;

    public static byte[] bestAvailableMac() {
        byte[] bestMacAddr = EmptyArrays.EMPTY_BYTES;
        InetAddress bestInetAddr = NetUtil.LOCALHOST4;
        LinkedHashMap<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<NetworkInterface, InetAddress>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    InetAddress a2;
                    NetworkInterface iface = interfaces.nextElement();
                    Enumeration<InetAddress> addrs = SocketUtils.addressesFromNetworkInterface(iface);
                    if (!addrs.hasMoreElements() || (a2 = addrs.nextElement()).isLoopbackAddress()) continue;
                    ifaces.put(iface, a2);
                }
            }
        }
        catch (SocketException e2) {
            logger.warn("Failed to retrieve the list of available network interfaces", e2);
        }
        for (Map.Entry entry : ifaces.entrySet()) {
            byte[] macAddr;
            NetworkInterface iface = (NetworkInterface)entry.getKey();
            InetAddress inetAddr = (InetAddress)entry.getValue();
            if (iface.isVirtual()) continue;
            try {
                macAddr = SocketUtils.hardwareAddressFromNetworkInterface(iface);
            }
            catch (SocketException e3) {
                logger.debug("Failed to get the hardware address of a network interface: {}", (Object)iface, (Object)e3);
                continue;
            }
            boolean replace = false;
            int res = MacAddressUtil.compareAddresses(bestMacAddr, macAddr);
            if (res < 0) {
                replace = true;
            } else if (res == 0) {
                res = MacAddressUtil.compareAddresses(bestInetAddr, inetAddr);
                if (res < 0) {
                    replace = true;
                } else if (res == 0 && bestMacAddr.length < macAddr.length) {
                    replace = true;
                }
            }
            if (!replace) continue;
            bestMacAddr = macAddr;
            bestInetAddr = inetAddr;
        }
        if (bestMacAddr == EmptyArrays.EMPTY_BYTES) {
            return null;
        }
        switch (bestMacAddr.length) {
            case 6: {
                byte[] newAddr = new byte[8];
                System.arraycopy(bestMacAddr, 0, newAddr, 0, 3);
                newAddr[3] = -1;
                newAddr[4] = -2;
                System.arraycopy(bestMacAddr, 3, newAddr, 5, 3);
                bestMacAddr = newAddr;
                break;
            }
            default: {
                bestMacAddr = Arrays.copyOf(bestMacAddr, 8);
            }
        }
        return bestMacAddr;
    }

    public static byte[] defaultMachineId() {
        byte[] bestMacAddr = MacAddressUtil.bestAvailableMac();
        if (bestMacAddr == null) {
            bestMacAddr = new byte[8];
            PlatformDependent.threadLocalRandom().nextBytes(bestMacAddr);
            logger.warn("Failed to find a usable hardware address from the network interfaces; using random bytes: {}", (Object)MacAddressUtil.formatAddress(bestMacAddr));
        }
        return bestMacAddr;
    }

    public static byte[] parseMAC(String value) {
        byte[] machineId;
        char separator;
        switch (value.length()) {
            case 17: {
                separator = value.charAt(2);
                MacAddressUtil.validateMacSeparator(separator);
                machineId = new byte[6];
                break;
            }
            case 23: {
                separator = value.charAt(2);
                MacAddressUtil.validateMacSeparator(separator);
                machineId = new byte[8];
                break;
            }
            default: {
                throw new IllegalArgumentException("value is not supported [MAC-48, EUI-48, EUI-64]");
            }
        }
        int end = machineId.length - 1;
        int j2 = 0;
        int i2 = 0;
        while (i2 < end) {
            int sIndex = j2 + 2;
            machineId[i2] = (byte)Integer.parseInt(value.substring(j2, sIndex), 16);
            if (value.charAt(sIndex) != separator) {
                throw new IllegalArgumentException("expected separator '" + separator + " but got '" + value.charAt(sIndex) + "' at index: " + sIndex);
            }
            ++i2;
            j2 += 3;
        }
        machineId[end] = (byte)Integer.parseInt(value.substring(j2, value.length()), 16);
        return machineId;
    }

    private static void validateMacSeparator(char separator) {
        if (separator != ':' && separator != '-') {
            throw new IllegalArgumentException("unsupported seperator: " + separator + " (expected: [:-])");
        }
    }

    public static String formatAddress(byte[] addr) {
        StringBuilder buf2 = new StringBuilder(24);
        for (byte b2 : addr) {
            buf2.append(String.format("%02x:", b2 & 0xFF));
        }
        return buf2.substring(0, buf2.length() - 1);
    }

    static int compareAddresses(byte[] current, byte[] candidate) {
        if (candidate == null || candidate.length < 6) {
            return 1;
        }
        boolean onlyZeroAndOne = true;
        for (byte b2 : candidate) {
            if (b2 == 0 || b2 == 1) continue;
            onlyZeroAndOne = false;
            break;
        }
        if (onlyZeroAndOne) {
            return 1;
        }
        if ((candidate[0] & 1) != 0) {
            return 1;
        }
        if ((candidate[0] & 2) == 0) {
            if (current.length != 0 && (current[0] & 2) == 0) {
                return 0;
            }
            return -1;
        }
        if (current.length != 0 && (current[0] & 2) == 0) {
            return 1;
        }
        return 0;
    }

    private static int compareAddresses(InetAddress current, InetAddress candidate) {
        return MacAddressUtil.scoreAddress(current) - MacAddressUtil.scoreAddress(candidate);
    }

    private static int scoreAddress(InetAddress addr) {
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
            return 0;
        }
        if (addr.isMulticastAddress()) {
            return 1;
        }
        if (addr.isLinkLocalAddress()) {
            return 2;
        }
        if (addr.isSiteLocalAddress()) {
            return 3;
        }
        return 4;
    }

    private MacAddressUtil() {
    }
}


package viamcp.protocols;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionRange;
import viamcp.protocols.ProtocolInfo;
import viamcp.protocols.ProtocolInfoCollection;

public enum ProtocolCollection {
    R1_18_2(new ProtocolVersion(758, "1.18.2"), ProtocolInfoCollection.R1_18_2),
    R1_18(new ProtocolVersion(757, -1, "1.18-1.18.1", new VersionRange("1.18", 0, 1)), ProtocolInfoCollection.R1_18),
    R1_17_1(new ProtocolVersion(756, "1.17.1"), ProtocolInfoCollection.R1_17_1),
    R1_17(new ProtocolVersion(755, "1.17"), ProtocolInfoCollection.R1_17),
    R1_16_4(new ProtocolVersion(754, "1.16.4-1.16.5"), ProtocolInfoCollection.R1_16_4),
    R1_16_3(new ProtocolVersion(753, "1.16.3"), ProtocolInfoCollection.R1_16_3),
    R1_16_2(new ProtocolVersion(751, "1.16.2"), ProtocolInfoCollection.R1_16_2),
    R1_16_1(new ProtocolVersion(736, "1.16.1"), ProtocolInfoCollection.R1_16_1),
    R1_16(new ProtocolVersion(735, "1.16"), ProtocolInfoCollection.R1_16),
    R1_15_2(new ProtocolVersion(578, "1.15.2"), ProtocolInfoCollection.R1_15_2),
    R1_15_1(new ProtocolVersion(575, "1.15.1"), ProtocolInfoCollection.R1_15_1),
    R1_15(new ProtocolVersion(573, "1.15"), ProtocolInfoCollection.R1_15),
    R1_14_4(new ProtocolVersion(498, "1.14.4"), ProtocolInfoCollection.R1_14_4),
    R1_14_3(new ProtocolVersion(490, "1.14.3"), ProtocolInfoCollection.R1_14_3),
    R1_14_2(new ProtocolVersion(485, "1.14.2"), ProtocolInfoCollection.R1_14_2),
    R1_14_1(new ProtocolVersion(480, "1.14.1"), ProtocolInfoCollection.R1_14_1),
    R1_14(new ProtocolVersion(477, "1.14"), ProtocolInfoCollection.R1_14),
    R1_13_2(new ProtocolVersion(404, "1.13.2"), ProtocolInfoCollection.R1_13_2),
    R1_13_1(new ProtocolVersion(401, "1.13.1"), ProtocolInfoCollection.R1_13_1),
    R1_13(new ProtocolVersion(393, "1.13"), ProtocolInfoCollection.R1_13),
    R1_12_2(new ProtocolVersion(340, "1.12.2"), ProtocolInfoCollection.R1_12_2),
    R1_12_1(new ProtocolVersion(338, "1.12.1"), ProtocolInfoCollection.R1_12_1),
    R1_12(new ProtocolVersion(335, "1.12"), ProtocolInfoCollection.R1_12),
    R1_11_1(new ProtocolVersion(316, "1.11.1-1.11.2"), ProtocolInfoCollection.R1_11_1),
    R1_11(new ProtocolVersion(315, "1.11"), ProtocolInfoCollection.R1_11),
    R1_10(new ProtocolVersion(210, "1.10.x"), ProtocolInfoCollection.R1_10),
    R1_9_3(new ProtocolVersion(110, "1.9.3-1.9.4"), ProtocolInfoCollection.R1_9_3),
    R1_9_2(new ProtocolVersion(109, "1.9.2"), ProtocolInfoCollection.R1_9_2),
    R1_9_1(new ProtocolVersion(108, "1.9.1"), ProtocolInfoCollection.R1_9_1),
    R1_9(new ProtocolVersion(107, "1.9"), ProtocolInfoCollection.R1_9),
    R1_8(new ProtocolVersion(47, "1.8.x"), ProtocolInfoCollection.R1_8),
    R1_7_6(new ProtocolVersion(5, -1, "1.7.6-1.7.10", new VersionRange("1.7", 6, 10)), ProtocolInfoCollection.R1_7_6),
    R1_7(new ProtocolVersion(4, -1, "1.7-1.7.5", new VersionRange("1.7", 0, 5)), ProtocolInfoCollection.R1_7);

    private ProtocolVersion version;
    private ProtocolInfo info;

    private ProtocolCollection(ProtocolVersion version, ProtocolInfo info) {
        this.version = version;
        this.info = info;
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }

    public ProtocolInfo getInfo() {
        return this.info;
    }

    public static ProtocolCollection getProtocolCollectionById(int id2) {
        for (ProtocolCollection coll : ProtocolCollection.values()) {
            if (coll.getVersion().getVersion() != id2) continue;
            return coll;
        }
        return null;
    }

    public static ProtocolVersion getProtocolById(int id2) {
        for (ProtocolCollection coll : ProtocolCollection.values()) {
            if (coll.getVersion().getVersion() != id2) continue;
            return coll.getVersion();
        }
        return null;
    }

    public static ProtocolInfo getProtocolInfoById(int id2) {
        for (ProtocolCollection coll : ProtocolCollection.values()) {
            if (coll.getVersion().getVersion() != id2) continue;
            return coll.getInfo();
        }
        return null;
    }
}


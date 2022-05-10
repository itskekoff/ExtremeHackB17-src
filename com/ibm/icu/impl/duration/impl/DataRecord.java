package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.impl.RecordReader;
import com.ibm.icu.impl.duration.impl.RecordWriter;
import java.util.ArrayList;

public class DataRecord {
    byte pl;
    String[][] pluralNames;
    byte[] genders;
    String[] singularNames;
    String[] halfNames;
    String[] numberNames;
    String[] mediumNames;
    String[] shortNames;
    String[] measures;
    String[] rqdSuffixes;
    String[] optSuffixes;
    String[] halves;
    byte[] halfPlacements;
    byte[] halfSupport;
    String fifteenMinutes;
    String fiveMinutes;
    boolean requiresDigitSeparator;
    String digitPrefix;
    String countSep;
    String shortUnitSep;
    String[] unitSep;
    boolean[] unitSepRequiresDP;
    boolean[] requiresSkipMarker;
    byte numberSystem;
    char zero;
    char decimalSep;
    boolean omitSingularCount;
    boolean omitDualCount;
    byte zeroHandling;
    byte decimalHandling;
    byte fractionHandling;
    String skippedUnitMarker;
    boolean allowZero;
    boolean weeksAloneOnly;
    byte useMilliseconds;
    ScopeData[] scopeData;

    public static DataRecord read(String ln2, RecordReader in2) {
        if (in2.open("DataRecord")) {
            DataRecord record = new DataRecord();
            record.pl = in2.namedIndex("pl", EPluralization.names);
            record.pluralNames = in2.stringTable("pluralName");
            record.genders = in2.namedIndexArray("gender", EGender.names);
            record.singularNames = in2.stringArray("singularName");
            record.halfNames = in2.stringArray("halfName");
            record.numberNames = in2.stringArray("numberName");
            record.mediumNames = in2.stringArray("mediumName");
            record.shortNames = in2.stringArray("shortName");
            record.measures = in2.stringArray("measure");
            record.rqdSuffixes = in2.stringArray("rqdSuffix");
            record.optSuffixes = in2.stringArray("optSuffix");
            record.halves = in2.stringArray("halves");
            record.halfPlacements = in2.namedIndexArray("halfPlacement", EHalfPlacement.names);
            record.halfSupport = in2.namedIndexArray("halfSupport", EHalfSupport.names);
            record.fifteenMinutes = in2.string("fifteenMinutes");
            record.fiveMinutes = in2.string("fiveMinutes");
            record.requiresDigitSeparator = in2.bool("requiresDigitSeparator");
            record.digitPrefix = in2.string("digitPrefix");
            record.countSep = in2.string("countSep");
            record.shortUnitSep = in2.string("shortUnitSep");
            record.unitSep = in2.stringArray("unitSep");
            record.unitSepRequiresDP = in2.boolArray("unitSepRequiresDP");
            record.requiresSkipMarker = in2.boolArray("requiresSkipMarker");
            record.numberSystem = in2.namedIndex("numberSystem", ENumberSystem.names);
            record.zero = in2.character("zero");
            record.decimalSep = in2.character("decimalSep");
            record.omitSingularCount = in2.bool("omitSingularCount");
            record.omitDualCount = in2.bool("omitDualCount");
            record.zeroHandling = in2.namedIndex("zeroHandling", EZeroHandling.names);
            record.decimalHandling = in2.namedIndex("decimalHandling", EDecimalHandling.names);
            record.fractionHandling = in2.namedIndex("fractionHandling", EFractionHandling.names);
            record.skippedUnitMarker = in2.string("skippedUnitMarker");
            record.allowZero = in2.bool("allowZero");
            record.weeksAloneOnly = in2.bool("weeksAloneOnly");
            record.useMilliseconds = in2.namedIndex("useMilliseconds", EMilliSupport.names);
            if (in2.open("ScopeDataList")) {
                ScopeData data;
                ArrayList<ScopeData> list = new ArrayList<ScopeData>();
                while (null != (data = ScopeData.read(in2))) {
                    list.add(data);
                }
                if (in2.close()) {
                    record.scopeData = list.toArray(new ScopeData[list.size()]);
                }
            }
            if (in2.close()) {
                return record;
            }
        } else {
            throw new InternalError("did not find DataRecord while reading " + ln2);
        }
        throw new InternalError("null data read while reading " + ln2);
    }

    public void write(RecordWriter out) {
        out.open("DataRecord");
        out.namedIndex("pl", EPluralization.names, this.pl);
        out.stringTable("pluralName", this.pluralNames);
        out.namedIndexArray("gender", EGender.names, this.genders);
        out.stringArray("singularName", this.singularNames);
        out.stringArray("halfName", this.halfNames);
        out.stringArray("numberName", this.numberNames);
        out.stringArray("mediumName", this.mediumNames);
        out.stringArray("shortName", this.shortNames);
        out.stringArray("measure", this.measures);
        out.stringArray("rqdSuffix", this.rqdSuffixes);
        out.stringArray("optSuffix", this.optSuffixes);
        out.stringArray("halves", this.halves);
        out.namedIndexArray("halfPlacement", EHalfPlacement.names, this.halfPlacements);
        out.namedIndexArray("halfSupport", EHalfSupport.names, this.halfSupport);
        out.string("fifteenMinutes", this.fifteenMinutes);
        out.string("fiveMinutes", this.fiveMinutes);
        out.bool("requiresDigitSeparator", this.requiresDigitSeparator);
        out.string("digitPrefix", this.digitPrefix);
        out.string("countSep", this.countSep);
        out.string("shortUnitSep", this.shortUnitSep);
        out.stringArray("unitSep", this.unitSep);
        out.boolArray("unitSepRequiresDP", this.unitSepRequiresDP);
        out.boolArray("requiresSkipMarker", this.requiresSkipMarker);
        out.namedIndex("numberSystem", ENumberSystem.names, this.numberSystem);
        out.character("zero", this.zero);
        out.character("decimalSep", this.decimalSep);
        out.bool("omitSingularCount", this.omitSingularCount);
        out.bool("omitDualCount", this.omitDualCount);
        out.namedIndex("zeroHandling", EZeroHandling.names, this.zeroHandling);
        out.namedIndex("decimalHandling", EDecimalHandling.names, this.decimalHandling);
        out.namedIndex("fractionHandling", EFractionHandling.names, this.fractionHandling);
        out.string("skippedUnitMarker", this.skippedUnitMarker);
        out.bool("allowZero", this.allowZero);
        out.bool("weeksAloneOnly", this.weeksAloneOnly);
        out.namedIndex("useMilliseconds", EMilliSupport.names, this.useMilliseconds);
        if (this.scopeData != null) {
            out.open("ScopeDataList");
            for (int i2 = 0; i2 < this.scopeData.length; ++i2) {
                this.scopeData[i2].write(out);
            }
            out.close();
        }
        out.close();
    }

    public static interface EGender {
        public static final byte M = 0;
        public static final byte F = 1;
        public static final byte N = 2;
        public static final String[] names = new String[]{"M", "F", "N"};
    }

    public static interface ESeparatorVariant {
        public static final byte NONE = 0;
        public static final byte SHORT = 1;
        public static final byte FULL = 2;
        public static final String[] names = new String[]{"NONE", "SHORT", "FULL"};
    }

    public static interface EMilliSupport {
        public static final byte YES = 0;
        public static final byte NO = 1;
        public static final byte WITH_SECONDS = 2;
        public static final String[] names = new String[]{"YES", "NO", "WITH_SECONDS"};
    }

    public static interface EHalfSupport {
        public static final byte YES = 0;
        public static final byte NO = 1;
        public static final byte ONE_PLUS = 2;
        public static final String[] names = new String[]{"YES", "NO", "ONE_PLUS"};
    }

    public static interface EFractionHandling {
        public static final byte FPLURAL = 0;
        public static final byte FSINGULAR_PLURAL = 1;
        public static final byte FSINGULAR_PLURAL_ANDAHALF = 2;
        public static final byte FPAUCAL = 3;
        public static final String[] names = new String[]{"FPLURAL", "FSINGULAR_PLURAL", "FSINGULAR_PLURAL_ANDAHALF", "FPAUCAL"};
    }

    public static interface EDecimalHandling {
        public static final byte DPLURAL = 0;
        public static final byte DSINGULAR = 1;
        public static final byte DSINGULAR_SUBONE = 2;
        public static final byte DPAUCAL = 3;
        public static final String[] names = new String[]{"DPLURAL", "DSINGULAR", "DSINGULAR_SUBONE", "DPAUCAL"};
    }

    public static interface EZeroHandling {
        public static final byte ZPLURAL = 0;
        public static final byte ZSINGULAR = 1;
        public static final String[] names = new String[]{"ZPLURAL", "ZSINGULAR"};
    }

    public static interface ENumberSystem {
        public static final byte DEFAULT = 0;
        public static final byte CHINESE_TRADITIONAL = 1;
        public static final byte CHINESE_SIMPLIFIED = 2;
        public static final byte KOREAN = 3;
        public static final String[] names = new String[]{"DEFAULT", "CHINESE_TRADITIONAL", "CHINESE_SIMPLIFIED", "KOREAN"};
    }

    public static interface EHalfPlacement {
        public static final byte PREFIX = 0;
        public static final byte AFTER_FIRST = 1;
        public static final byte LAST = 2;
        public static final String[] names = new String[]{"PREFIX", "AFTER_FIRST", "LAST"};
    }

    public static interface EPluralization {
        public static final byte NONE = 0;
        public static final byte PLURAL = 1;
        public static final byte DUAL = 2;
        public static final byte PAUCAL = 3;
        public static final byte HEBREW = 4;
        public static final byte ARABIC = 5;
        public static final String[] names = new String[]{"NONE", "PLURAL", "DUAL", "PAUCAL", "HEBREW", "ARABIC"};
    }

    public static interface ECountVariant {
        public static final byte INTEGER = 0;
        public static final byte INTEGER_CUSTOM = 1;
        public static final byte HALF_FRACTION = 2;
        public static final byte DECIMAL1 = 3;
        public static final byte DECIMAL2 = 4;
        public static final byte DECIMAL3 = 5;
        public static final String[] names = new String[]{"INTEGER", "INTEGER_CUSTOM", "HALF_FRACTION", "DECIMAL1", "DECIMAL2", "DECIMAL3"};
    }

    public static interface EUnitVariant {
        public static final byte PLURALIZED = 0;
        public static final byte MEDIUM = 1;
        public static final byte SHORT = 2;
        public static final String[] names = new String[]{"PLURALIZED", "MEDIUM", "SHORT"};
    }

    public static interface ETimeDirection {
        public static final byte NODIRECTION = 0;
        public static final byte PAST = 1;
        public static final byte FUTURE = 2;
        public static final String[] names = new String[]{"NODIRECTION", "PAST", "FUTURE"};
    }

    public static interface ETimeLimit {
        public static final byte NOLIMIT = 0;
        public static final byte LT = 1;
        public static final byte MT = 2;
        public static final String[] names = new String[]{"NOLIMIT", "LT", "MT"};
    }

    public static class ScopeData {
        String prefix;
        boolean requiresDigitPrefix;
        String suffix;

        public void write(RecordWriter out) {
            out.open("ScopeData");
            out.string("prefix", this.prefix);
            out.bool("requiresDigitPrefix", this.requiresDigitPrefix);
            out.string("suffix", this.suffix);
            out.close();
        }

        public static ScopeData read(RecordReader in2) {
            if (in2.open("ScopeData")) {
                ScopeData scope = new ScopeData();
                scope.prefix = in2.string("prefix");
                scope.requiresDigitPrefix = in2.bool("requiresDigitPrefix");
                scope.suffix = in2.string("suffix");
                if (in2.close()) {
                    return scope;
                }
            }
            return null;
        }
    }
}

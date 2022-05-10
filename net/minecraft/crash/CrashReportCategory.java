package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.math.BlockPos;

public class CrashReportCategory {
    private final CrashReport crashReport;
    private final String name;
    private final List<Entry> children = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportCategory(CrashReport report, String name) {
        this.crashReport = report;
        this.name = name;
    }

    public static String getCoordinateInfo(double x2, double y2, double z2) {
        return String.format("%.2f,%.2f,%.2f - %s", x2, y2, z2, CrashReportCategory.getCoordinateInfo(new BlockPos(x2, y2, z2)));
    }

    public static String getCoordinateInfo(BlockPos pos) {
        return CrashReportCategory.getCoordinateInfo(pos.getX(), pos.getY(), pos.getZ());
    }

    public static String getCoordinateInfo(int x2, int y2, int z2) {
        StringBuilder stringbuilder = new StringBuilder();
        try {
            stringbuilder.append(String.format("World: (%d,%d,%d)", x2, y2, z2));
        }
        catch (Throwable var16) {
            stringbuilder.append("(Error finding world loc)");
        }
        stringbuilder.append(", ");
        try {
            int i2 = x2 >> 4;
            int j2 = z2 >> 4;
            int k2 = x2 & 0xF;
            int l2 = y2 >> 4;
            int i1 = z2 & 0xF;
            int j1 = i2 << 4;
            int k1 = j2 << 4;
            int l1 = (i2 + 1 << 4) - 1;
            int i22 = (j2 + 1 << 4) - 1;
            stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", k2, l2, i1, i2, j2, j1, k1, l1, i22));
        }
        catch (Throwable var15) {
            stringbuilder.append("(Error finding chunk loc)");
        }
        stringbuilder.append(", ");
        try {
            int k2 = x2 >> 9;
            int l2 = z2 >> 9;
            int i3 = k2 << 5;
            int j3 = l2 << 5;
            int k3 = (k2 + 1 << 5) - 1;
            int l3 = (l2 + 1 << 5) - 1;
            int i4 = k2 << 9;
            int j4 = l2 << 9;
            int k4 = (k2 + 1 << 9) - 1;
            int j2 = (l2 + 1 << 9) - 1;
            stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", k2, l2, i3, j3, k3, l3, i4, j4, k4, j2));
        }
        catch (Throwable var14) {
            stringbuilder.append("(Error finding world loc)");
        }
        return stringbuilder.toString();
    }

    public void setDetail(String nameIn, ICrashReportDetail<String> detail) {
        try {
            this.addCrashSection(nameIn, detail.call());
        }
        catch (Throwable throwable) {
            this.addCrashSectionThrowable(nameIn, throwable);
        }
    }

    public void addCrashSection(String sectionName, Object value) {
        this.children.add(new Entry(sectionName, value));
    }

    public void addCrashSectionThrowable(String sectionName, Throwable throwable) {
        this.addCrashSection(sectionName, throwable);
    }

    public int getPrunedStackTrace(int size) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        if (astacktraceelement.length <= 0) {
            return 0;
        }
        this.stackTrace = new StackTraceElement[astacktraceelement.length - 3 - size];
        System.arraycopy(astacktraceelement, 3 + size, this.stackTrace, 0, this.stackTrace.length);
        return this.stackTrace.length;
    }

    public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement s1, StackTraceElement s2) {
        if (this.stackTrace.length != 0 && s1 != null) {
            StackTraceElement stacktraceelement = this.stackTrace[0];
            if (stacktraceelement.isNativeMethod() == s1.isNativeMethod() && stacktraceelement.getClassName().equals(s1.getClassName()) && stacktraceelement.getFileName().equals(s1.getFileName()) && stacktraceelement.getMethodName().equals(s1.getMethodName())) {
                if (s2 != null != this.stackTrace.length > 1) {
                    return false;
                }
                if (s2 != null && !this.stackTrace[1].equals(s2)) {
                    return false;
                }
                this.stackTrace[0] = s1;
                return true;
            }
            return false;
        }
        return false;
    }

    public void trimStackTraceEntriesFromBottom(int amount) {
        StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - amount];
        System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
        this.stackTrace = astacktraceelement;
    }

    public void appendToStringBuilder(StringBuilder builder) {
        builder.append("-- ").append(this.name).append(" --\n");
        builder.append("Details:");
        for (Entry crashreportcategory$entry : this.children) {
            builder.append("\n\t");
            builder.append(crashreportcategory$entry.getKey());
            builder.append(": ");
            builder.append(crashreportcategory$entry.getValue());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            builder.append("\nStacktrace:");
            StackTraceElement[] arrstackTraceElement = this.stackTrace;
            int n2 = this.stackTrace.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                StackTraceElement stacktraceelement = arrstackTraceElement[i2];
                builder.append("\n\tat ");
                builder.append(stacktraceelement);
            }
        }
    }

    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }

    public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData) {
        final int i2 = Block.getIdFromBlock(blockIn);
        category.setDetail("Block type", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                try {
                    return String.format("ID #%d (%s // %s)", i2, blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
                }
                catch (Throwable var2) {
                    return "ID #" + i2;
                }
            }
        });
        category.setDetail("Block data value", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                if (blockData < 0) {
                    return "Unknown? (Got " + blockData + ")";
                }
                String s2 = String.format("%4s", Integer.toBinaryString(blockData)).replace(" ", "0");
                return String.format("%1$d / 0x%1$X / 0b%2$s", blockData, s2);
            }
        });
        category.setDetail("Block location", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
        });
    }

    public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final IBlockState state) {
        category.setDetail("Block", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return state.toString();
            }
        });
        category.setDetail("Block location", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
        });
    }

    static class Entry {
        private final String key;
        private final String value;

        public Entry(String key, Object value) {
            this.key = key;
            if (value == null) {
                this.value = "~~NULL~~";
            } else if (value instanceof Throwable) {
                Throwable throwable = (Throwable)value;
                this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.value = value.toString();
            }
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
}


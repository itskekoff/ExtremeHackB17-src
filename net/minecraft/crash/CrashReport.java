package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import optifine.CrashReporter;
import optifine.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String description;
    private final Throwable cause;
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
    private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();
    private File crashReportFile;
    private boolean firstCategoryInCrashReport = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private boolean reported = false;

    public CrashReport(String descriptionIn, Throwable causeThrowable) {
        this.description = descriptionIn;
        this.cause = causeThrowable;
        this.populateEnvironment();
    }

    private void populateEnvironment() {
        this.theReportCategory.setDetail("Minecraft Version", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return "1.12.2";
            }
        });
        this.theReportCategory.setDetail("Operating System", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return String.valueOf(System.getProperty("os.name")) + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.theReportCategory.setDetail("Java Version", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return String.valueOf(System.getProperty("java.version")) + ", " + System.getProperty("java.vendor");
            }
        });
        this.theReportCategory.setDetail("Java VM Version", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return String.valueOf(System.getProperty("java.vm.name")) + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.theReportCategory.setDetail("Memory", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                Runtime runtime = Runtime.getRuntime();
                long i2 = runtime.maxMemory();
                long j2 = runtime.totalMemory();
                long k2 = runtime.freeMemory();
                long l2 = i2 / 1024L / 1024L;
                long i1 = j2 / 1024L / 1024L;
                long j1 = k2 / 1024L / 1024L;
                return String.valueOf(k2) + " bytes (" + j1 + " MB) / " + j2 + " bytes (" + i1 + " MB) up to " + i2 + " bytes (" + l2 + " MB)";
            }
        });
        this.theReportCategory.setDetail("JVM Flags", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List<String> list = runtimemxbean.getInputArguments();
                int i2 = 0;
                StringBuilder stringbuilder = new StringBuilder();
                for (String s2 : list) {
                    if (!s2.startsWith("-X")) continue;
                    if (i2++ > 0) {
                        stringbuilder.append(" ");
                    }
                    stringbuilder.append(s2);
                }
                return String.format("%d total; %s", i2, stringbuilder.toString());
            }
        });
        this.theReportCategory.setDetail("IntCache", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return IntCache.getCacheSizes();
            }
        });
        if (Reflector.FMLCommonHandler_enhanceCrashReport.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            Reflector.callString(object, Reflector.FMLCommonHandler_enhanceCrashReport, this, this.theReportCategory);
        }
    }

    public String getDescription() {
        return this.description;
    }

    public Throwable getCrashCause() {
        return this.cause;
    }

    public void getSectionsInStringBuilder(StringBuilder builder) {
        if (!(this.stacktrace != null && this.stacktrace.length > 0 || this.crashReportSections.isEmpty())) {
            this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
        }
        if (this.stacktrace != null && this.stacktrace.length > 0) {
            builder.append("-- Head --\n");
            builder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
            builder.append("Stacktrace:\n");
            StackTraceElement[] arrstackTraceElement = this.stacktrace;
            int n2 = this.stacktrace.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                StackTraceElement stacktraceelement = arrstackTraceElement[i2];
                builder.append("\t").append("at ").append(stacktraceelement);
                builder.append("\n");
            }
            builder.append("\n");
        }
        for (CrashReportCategory crashreportcategory : this.crashReportSections) {
            crashreportcategory.appendToStringBuilder(builder);
            builder.append("\n\n");
        }
        this.theReportCategory.appendToStringBuilder(builder);
    }

    public String getCauseStackTraceOrString() {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Throwable throwable = this.cause;
        if (throwable.getMessage() == null) {
            if (throwable instanceof NullPointerException) {
                throwable = new NullPointerException(this.description);
            } else if (throwable instanceof StackOverflowError) {
                throwable = new StackOverflowError(this.description);
            } else if (throwable instanceof OutOfMemoryError) {
                throwable = new OutOfMemoryError(this.description);
            }
            throwable.setStackTrace(this.cause.getStackTrace());
        }
        String s2 = throwable.toString();
        try {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            throwable.printStackTrace(printwriter);
            s2 = stringwriter.toString();
        }
        catch (Throwable throwable2) {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
            throw throwable2;
        }
        IOUtils.closeQuietly(stringwriter);
        IOUtils.closeQuietly(printwriter);
        return s2;
    }

    public String getCompleteReport() {
        if (!this.reported) {
            this.reported = true;
            CrashReporter.onCrashReport(this, this.theReportCategory);
        }
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Crash Report ----\n");
        Reflector.call(Reflector.BlamingTransformer_onCrash, stringbuilder);
        Reflector.call(Reflector.CoreModManager_onCrash, stringbuilder);
        stringbuilder.append("// ");
        stringbuilder.append(CrashReport.getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time: ");
        stringbuilder.append(new SimpleDateFormat().format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("Description: ");
        stringbuilder.append(this.description);
        stringbuilder.append("\n\n");
        stringbuilder.append(this.getCauseStackTraceOrString());
        stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
        for (int i2 = 0; i2 < 87; ++i2) {
            stringbuilder.append("-");
        }
        stringbuilder.append("\n\n");
        this.getSectionsInStringBuilder(stringbuilder);
        return stringbuilder.toString();
    }

    public File getFile() {
        return this.crashReportFile;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean saveToFile(File toFile) {
        boolean bl2;
        if (this.crashReportFile != null) {
            return false;
        }
        if (toFile.getParentFile() != null) {
            toFile.getParentFile().mkdirs();
        }
        OutputStreamWriter writer = null;
        try {
            boolean flag1;
            boolean flag2;
            writer = new OutputStreamWriter((OutputStream)new FileOutputStream(toFile), StandardCharsets.UTF_8);
            writer.write(this.getCompleteReport());
            this.crashReportFile = toFile;
            bl2 = flag2 = (flag1 = true);
        }
        catch (Throwable throwable1) {
            boolean flag;
            try {
                LOGGER.error("Could not save crash report to {}", (Object)toFile, (Object)throwable1);
                flag = false;
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(writer);
                throw throwable;
            }
            IOUtils.closeQuietly(writer);
            return flag;
        }
        IOUtils.closeQuietly(writer);
        return bl2;
    }

    public CrashReportCategory getCategory() {
        return this.theReportCategory;
    }

    public CrashReportCategory makeCategory(String name) {
        return this.makeCategoryDepth(name, 1);
    }

    public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
        CrashReportCategory crashreportcategory = new CrashReportCategory(this, categoryName);
        if (this.firstCategoryInCrashReport) {
            int i2 = crashreportcategory.getPrunedStackTrace(stacktraceLength);
            StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
            StackTraceElement stacktraceelement = null;
            StackTraceElement stacktraceelement1 = null;
            int j2 = astacktraceelement.length - i2;
            if (j2 < 0) {
                System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i2 + ")");
            }
            if (astacktraceelement != null && j2 >= 0 && j2 < astacktraceelement.length) {
                stacktraceelement = astacktraceelement[j2];
                if (astacktraceelement.length + 1 - i2 < astacktraceelement.length) {
                    stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i2];
                }
            }
            this.firstCategoryInCrashReport = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);
            if (i2 > 0 && !this.crashReportSections.isEmpty()) {
                CrashReportCategory crashreportcategory1 = this.crashReportSections.get(this.crashReportSections.size() - 1);
                crashreportcategory1.trimStackTraceEntriesFromBottom(i2);
            } else if (astacktraceelement != null && astacktraceelement.length >= i2 && j2 >= 0 && j2 < astacktraceelement.length) {
                this.stacktrace = new StackTraceElement[j2];
                System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
            } else {
                this.firstCategoryInCrashReport = false;
            }
        }
        this.crashReportSections.add(crashreportcategory);
        return crashreportcategory;
    }

    private static String getWittyComment() {
        String[] astring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};
        try {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
        CrashReport crashreport = causeIn instanceof ReportedException ? ((ReportedException)causeIn).getCrashReport() : new CrashReport(descriptionIn, causeIn);
        return crashreport;
    }
}


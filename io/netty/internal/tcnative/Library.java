package io.netty.internal.tcnative;

import io.netty.internal.tcnative.SSL;
import java.io.File;

public final class Library {
    private static final String[] NAMES = new String[]{"netty-tcnative", "libnetty-tcnative", "netty-tcnative-1", "libnetty-tcnative-1"};
    private static Library _instance = null;

    private Library() throws Exception {
        boolean loaded = false;
        String path = System.getProperty("java.library.path");
        String[] paths = path.split(File.pathSeparator);
        StringBuilder err = new StringBuilder();
        for (int i2 = 0; i2 < NAMES.length; ++i2) {
            try {
                System.loadLibrary(NAMES[i2]);
                loaded = true;
            }
            catch (ThreadDeath t2) {
                throw t2;
            }
            catch (VirtualMachineError t3) {
                throw t3;
            }
            catch (Throwable t4) {
                String name = System.mapLibraryName(NAMES[i2]);
                for (int j2 = 0; j2 < paths.length; ++j2) {
                    File fd2 = new File(paths[j2], name);
                    if (!fd2.exists()) continue;
                    throw new RuntimeException(t4);
                }
                if (i2 > 0) {
                    err.append(", ");
                }
                err.append(t4.getMessage());
            }
            if (loaded) break;
        }
        if (!loaded) {
            throw new UnsatisfiedLinkError(err.toString());
        }
    }

    private Library(String libraryName) {
        if (!"provided".equals(libraryName)) {
            System.loadLibrary(libraryName);
        }
    }

    private static native boolean initialize0();

    private static native boolean has(int var0);

    private static native int version(int var0);

    private static native String aprVersionString();

    public static boolean initialize() throws Exception {
        return Library.initialize("provided", null);
    }

    public static boolean initialize(String libraryName, String engine) throws Exception {
        if (_instance == null) {
            _instance = libraryName == null ? new Library() : new Library(libraryName);
            int aprMajor = Library.version(17);
            if (aprMajor < 1) {
                throw new UnsatisfiedLinkError("Unsupported APR Version (" + Library.aprVersionString() + ")");
            }
            boolean aprHasThreads = Library.has(2);
            if (!aprHasThreads) {
                throw new UnsatisfiedLinkError("Missing APR_HAS_THREADS");
            }
        }
        return Library.initialize0() && SSL.initialize(engine) == 0;
    }
}


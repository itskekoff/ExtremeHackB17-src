package com.sun.jna.platform;

import com.sun.jna.platform.mac.MacFileUtils;
import com.sun.jna.platform.win32.W32FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class FileUtils {
    public boolean hasTrash() {
        return false;
    }

    public abstract void moveToTrash(File[] var1) throws IOException;

    public static FileUtils getInstance() {
        return Holder.INSTANCE;
    }

    private static class DefaultFileUtils
    extends FileUtils {
        private DefaultFileUtils() {
        }

        private File getTrashDirectory() {
            File desktop;
            File home = new File(System.getProperty("user.home"));
            File trash = new File(home, ".Trash");
            if (!(trash.exists() || (trash = new File(home, "Trash")).exists() || !(desktop = new File(home, "Desktop")).exists() || (trash = new File(desktop, ".Trash")).exists() || (trash = new File(desktop, "Trash")).exists())) {
                trash = new File(System.getProperty("fileutils.trash", "Trash"));
            }
            return trash;
        }

        public boolean hasTrash() {
            return this.getTrashDirectory().exists();
        }

        public void moveToTrash(File[] files) throws IOException {
            File trash = this.getTrashDirectory();
            if (!trash.exists()) {
                throw new IOException("No trash location found (define fileutils.trash to be the path to the trash)");
            }
            ArrayList<File> failed = new ArrayList<File>();
            for (int i2 = 0; i2 < files.length; ++i2) {
                File src = files[i2];
                File target = new File(trash, src.getName());
                if (src.renameTo(target)) continue;
                failed.add(src);
            }
            if (failed.size() > 0) {
                throw new IOException("The following files could not be trashed: " + failed);
            }
        }
    }

    private static class Holder {
        public static final FileUtils INSTANCE;

        private Holder() {
        }

        static {
            String os2 = System.getProperty("os.name");
            INSTANCE = os2.startsWith("Windows") ? new W32FileUtils() : (os2.startsWith("Mac") ? new MacFileUtils() : new DefaultFileUtils());
        }
    }
}


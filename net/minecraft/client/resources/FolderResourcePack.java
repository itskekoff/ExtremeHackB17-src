package net.minecraft.client.resources;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class FolderResourcePack
extends AbstractResourcePack {
    private static final boolean field_191386_b = Util.getOSType() == Util.EnumOS.WINDOWS;
    private static final CharMatcher field_191387_c = CharMatcher.is('\\');

    public FolderResourcePack(File resourcePackFileIn) {
        super(resourcePackFileIn);
    }

    protected static boolean func_191384_a(File p_191384_0_, String p_191384_1_) throws IOException {
        String s2 = p_191384_0_.getCanonicalPath();
        if (field_191386_b) {
            s2 = field_191387_c.replaceFrom((CharSequence)s2, '/');
        }
        return s2.endsWith(p_191384_1_);
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException {
        File file1 = this.func_191385_d(name);
        if (file1 == null) {
            throw new ResourcePackFileNotFoundException(this.resourcePackFile, name);
        }
        return new BufferedInputStream(new FileInputStream(file1));
    }

    @Override
    protected boolean hasResourceName(String name) {
        return this.func_191385_d(name) != null;
    }

    @Nullable
    private File func_191385_d(String p_191385_1_) {
        try {
            File file1 = new File(this.resourcePackFile, p_191385_1_);
            if (file1.isFile() && FolderResourcePack.func_191384_a(file1, p_191385_1_)) {
                return file1;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public Set<String> getResourceDomains() {
        HashSet<String> set = Sets.newHashSet();
        File file1 = new File(this.resourcePackFile, "assets/");
        if (file1.isDirectory()) {
            for (File file2 : file1.listFiles(DirectoryFileFilter.DIRECTORY)) {
                String s2 = FolderResourcePack.getRelativeName(file1, file2);
                if (s2.equals(s2.toLowerCase(Locale.ROOT))) {
                    set.add(s2.substring(0, s2.length() - 1));
                    continue;
                }
                this.logNameNotLowercase(s2);
            }
        }
        return set;
    }
}


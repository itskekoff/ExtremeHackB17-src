package org.apache.commons.compress.changes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.changes.Change;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetResults;
import org.apache.commons.compress.utils.IOUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChangeSetPerformer {
    private final Set<Change> changes;

    public ChangeSetPerformer(ChangeSet changeSet) {
        this.changes = changeSet.getChanges();
    }

    public ChangeSetResults perform(ArchiveInputStream in2, ArchiveOutputStream out) throws IOException {
        return this.perform(new ArchiveInputStreamIterator(in2), out);
    }

    public ChangeSetResults perform(ZipFile in2, ArchiveOutputStream out) throws IOException {
        return this.perform(new ZipFileIterator(in2), out);
    }

    private ChangeSetResults perform(ArchiveEntryIterator entryIterator, ArchiveOutputStream out) throws IOException {
        ChangeSetResults results = new ChangeSetResults();
        LinkedHashSet<Change> workingSet = new LinkedHashSet<Change>(this.changes);
        Iterator it2 = workingSet.iterator();
        while (it2.hasNext()) {
            Change change = (Change)it2.next();
            if (change.type() != 2 || !change.isReplaceMode()) continue;
            this.copyStream(change.getInput(), out, change.getEntry());
            it2.remove();
            results.addedFromChangeSet(change.getEntry().getName());
        }
        while (entryIterator.hasNext()) {
            ArchiveEntry entry = entryIterator.next();
            boolean copy = true;
            Iterator it3 = workingSet.iterator();
            while (it3.hasNext()) {
                Change change = (Change)it3.next();
                int type = change.type();
                String name = entry.getName();
                if (type == 1 && name != null) {
                    if (!name.equals(change.targetFile())) continue;
                    copy = false;
                    it3.remove();
                    results.deleted(name);
                    break;
                }
                if (type != 4 || name == null || !name.startsWith(change.targetFile() + "/")) continue;
                copy = false;
                results.deleted(name);
                break;
            }
            if (!copy || this.isDeletedLater(workingSet, entry) || results.hasBeenAdded(entry.getName())) continue;
            this.copyStream(entryIterator.getInputStream(), out, entry);
            results.addedFromStream(entry.getName());
        }
        it2 = workingSet.iterator();
        while (it2.hasNext()) {
            Change change = (Change)it2.next();
            if (change.type() != 2 || change.isReplaceMode() || results.hasBeenAdded(change.getEntry().getName())) continue;
            this.copyStream(change.getInput(), out, change.getEntry());
            it2.remove();
            results.addedFromChangeSet(change.getEntry().getName());
        }
        out.finish();
        return results;
    }

    private boolean isDeletedLater(Set<Change> workingSet, ArchiveEntry entry) {
        String source = entry.getName();
        if (!workingSet.isEmpty()) {
            for (Change change : workingSet) {
                int type = change.type();
                String target = change.targetFile();
                if (type == 1 && source.equals(target)) {
                    return true;
                }
                if (type != 4 || !source.startsWith(target + "/")) continue;
                return true;
            }
        }
        return false;
    }

    private void copyStream(InputStream in2, ArchiveOutputStream out, ArchiveEntry entry) throws IOException {
        out.putArchiveEntry(entry);
        IOUtils.copy(in2, out);
        out.closeArchiveEntry();
    }

    private static class ZipFileIterator
    implements ArchiveEntryIterator {
        private final ZipFile in;
        private final Enumeration<ZipArchiveEntry> nestedEnum;
        private ZipArchiveEntry current;

        ZipFileIterator(ZipFile in2) {
            this.in = in2;
            this.nestedEnum = in2.getEntriesInPhysicalOrder();
        }

        public boolean hasNext() {
            return this.nestedEnum.hasMoreElements();
        }

        public ArchiveEntry next() {
            this.current = this.nestedEnum.nextElement();
            return this.current;
        }

        public InputStream getInputStream() throws IOException {
            return this.in.getInputStream(this.current);
        }
    }

    private static class ArchiveInputStreamIterator
    implements ArchiveEntryIterator {
        private final ArchiveInputStream in;
        private ArchiveEntry next;

        ArchiveInputStreamIterator(ArchiveInputStream in2) {
            this.in = in2;
        }

        public boolean hasNext() throws IOException {
            this.next = this.in.getNextEntry();
            return this.next != null;
        }

        public ArchiveEntry next() {
            return this.next;
        }

        public InputStream getInputStream() {
            return this.in;
        }
    }

    static interface ArchiveEntryIterator {
        public boolean hasNext() throws IOException;

        public ArchiveEntry next();

        public InputStream getInputStream() throws IOException;
    }
}


package org.apache.commons.compress.archivers.cpio;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.archivers.cpio.CpioUtil;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;

public class CpioArchiveOutputStream
extends ArchiveOutputStream
implements CpioConstants {
    private CpioArchiveEntry entry;
    private boolean closed = false;
    private boolean finished;
    private final short entryFormat;
    private final HashMap<String, CpioArchiveEntry> names = new HashMap();
    private long crc = 0L;
    private long written;
    private final OutputStream out;
    private final int blockSize;
    private long nextArtificalDeviceAndInode = 1L;
    private final ZipEncoding encoding;

    public CpioArchiveOutputStream(OutputStream out, short format) {
        this(out, format, 512, "US-ASCII");
    }

    public CpioArchiveOutputStream(OutputStream out, short format, int blockSize) {
        this(out, format, blockSize, "US-ASCII");
    }

    public CpioArchiveOutputStream(OutputStream out, short format, int blockSize, String encoding) {
        this.out = out;
        switch (format) {
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown format: " + format);
            }
        }
        this.entryFormat = format;
        this.blockSize = blockSize;
        this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
    }

    public CpioArchiveOutputStream(OutputStream out) {
        this(out, 1);
    }

    public CpioArchiveOutputStream(OutputStream out, String encoding) {
        this(out, 1, 512, encoding);
    }

    private void ensureOpen() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }

    public void putArchiveEntry(ArchiveEntry entry) throws IOException {
        short format;
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        CpioArchiveEntry e2 = (CpioArchiveEntry)entry;
        this.ensureOpen();
        if (this.entry != null) {
            this.closeArchiveEntry();
        }
        if (e2.getTime() == -1L) {
            e2.setTime(System.currentTimeMillis() / 1000L);
        }
        if ((format = e2.getFormat()) != this.entryFormat) {
            throw new IOException("Header format: " + format + " does not match existing format: " + this.entryFormat);
        }
        if (this.names.put(e2.getName(), e2) != null) {
            throw new IOException("duplicate entry: " + e2.getName());
        }
        this.writeHeader(e2);
        this.entry = e2;
        this.written = 0L;
    }

    private void writeHeader(CpioArchiveEntry e2) throws IOException {
        switch (e2.getFormat()) {
            case 1: {
                this.out.write(ArchiveUtils.toAsciiBytes("070701"));
                this.count(6);
                this.writeNewEntry(e2);
                break;
            }
            case 2: {
                this.out.write(ArchiveUtils.toAsciiBytes("070702"));
                this.count(6);
                this.writeNewEntry(e2);
                break;
            }
            case 4: {
                this.out.write(ArchiveUtils.toAsciiBytes("070707"));
                this.count(6);
                this.writeOldAsciiEntry(e2);
                break;
            }
            case 8: {
                boolean swapHalfWord = true;
                this.writeBinaryLong(29127L, 2, swapHalfWord);
                this.writeOldBinaryEntry(e2, swapHalfWord);
                break;
            }
            default: {
                throw new IOException("unknown format " + e2.getFormat());
            }
        }
    }

    private void writeNewEntry(CpioArchiveEntry entry) throws IOException {
        long inode = entry.getInode();
        long devMin = entry.getDeviceMin();
        if ("TRAILER!!!".equals(entry.getName())) {
            devMin = 0L;
            inode = 0L;
        } else if (inode == 0L && devMin == 0L) {
            inode = this.nextArtificalDeviceAndInode & 0xFFFFFFFFFFFFFFFFL;
            devMin = this.nextArtificalDeviceAndInode++ >> 32 & 0xFFFFFFFFFFFFFFFFL;
        } else {
            this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 0x100000000L * devMin) + 1L;
        }
        this.writeAsciiLong(inode, 8, 16);
        this.writeAsciiLong(entry.getMode(), 8, 16);
        this.writeAsciiLong(entry.getUID(), 8, 16);
        this.writeAsciiLong(entry.getGID(), 8, 16);
        this.writeAsciiLong(entry.getNumberOfLinks(), 8, 16);
        this.writeAsciiLong(entry.getTime(), 8, 16);
        this.writeAsciiLong(entry.getSize(), 8, 16);
        this.writeAsciiLong(entry.getDeviceMaj(), 8, 16);
        this.writeAsciiLong(devMin, 8, 16);
        this.writeAsciiLong(entry.getRemoteDeviceMaj(), 8, 16);
        this.writeAsciiLong(entry.getRemoteDeviceMin(), 8, 16);
        this.writeAsciiLong(entry.getName().length() + 1, 8, 16);
        this.writeAsciiLong(entry.getChksum(), 8, 16);
        this.writeCString(entry.getName());
        this.pad(entry.getHeaderPadCount());
    }

    private void writeOldAsciiEntry(CpioArchiveEntry entry) throws IOException {
        long inode = entry.getInode();
        long device = entry.getDevice();
        if ("TRAILER!!!".equals(entry.getName())) {
            device = 0L;
            inode = 0L;
        } else if (inode == 0L && device == 0L) {
            inode = this.nextArtificalDeviceAndInode & 0x3FFFFL;
            device = this.nextArtificalDeviceAndInode++ >> 18 & 0x3FFFFL;
        } else {
            this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 262144L * device) + 1L;
        }
        this.writeAsciiLong(device, 6, 8);
        this.writeAsciiLong(inode, 6, 8);
        this.writeAsciiLong(entry.getMode(), 6, 8);
        this.writeAsciiLong(entry.getUID(), 6, 8);
        this.writeAsciiLong(entry.getGID(), 6, 8);
        this.writeAsciiLong(entry.getNumberOfLinks(), 6, 8);
        this.writeAsciiLong(entry.getRemoteDevice(), 6, 8);
        this.writeAsciiLong(entry.getTime(), 11, 8);
        this.writeAsciiLong(entry.getName().length() + 1, 6, 8);
        this.writeAsciiLong(entry.getSize(), 11, 8);
        this.writeCString(entry.getName());
    }

    private void writeOldBinaryEntry(CpioArchiveEntry entry, boolean swapHalfWord) throws IOException {
        long inode = entry.getInode();
        long device = entry.getDevice();
        if ("TRAILER!!!".equals(entry.getName())) {
            device = 0L;
            inode = 0L;
        } else if (inode == 0L && device == 0L) {
            inode = this.nextArtificalDeviceAndInode & 0xFFFFL;
            device = this.nextArtificalDeviceAndInode++ >> 16 & 0xFFFFL;
        } else {
            this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 65536L * device) + 1L;
        }
        this.writeBinaryLong(device, 2, swapHalfWord);
        this.writeBinaryLong(inode, 2, swapHalfWord);
        this.writeBinaryLong(entry.getMode(), 2, swapHalfWord);
        this.writeBinaryLong(entry.getUID(), 2, swapHalfWord);
        this.writeBinaryLong(entry.getGID(), 2, swapHalfWord);
        this.writeBinaryLong(entry.getNumberOfLinks(), 2, swapHalfWord);
        this.writeBinaryLong(entry.getRemoteDevice(), 2, swapHalfWord);
        this.writeBinaryLong(entry.getTime(), 4, swapHalfWord);
        this.writeBinaryLong(entry.getName().length() + 1, 2, swapHalfWord);
        this.writeBinaryLong(entry.getSize(), 4, swapHalfWord);
        this.writeCString(entry.getName());
        this.pad(entry.getHeaderPadCount());
    }

    public void closeArchiveEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        this.ensureOpen();
        if (this.entry == null) {
            throw new IOException("Trying to close non-existent entry");
        }
        if (this.entry.getSize() != this.written) {
            throw new IOException("invalid entry size (expected " + this.entry.getSize() + " but got " + this.written + " bytes)");
        }
        this.pad(this.entry.getDataPadCount());
        if (this.entry.getFormat() == 2 && this.crc != this.entry.getChksum()) {
            throw new IOException("CRC Error");
        }
        this.entry = null;
        this.crc = 0L;
        this.written = 0L;
    }

    public void write(byte[] b2, int off, int len) throws IOException {
        this.ensureOpen();
        if (off < 0 || len < 0 || off > b2.length - len) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        if (this.entry == null) {
            throw new IOException("no current CPIO entry");
        }
        if (this.written + (long)len > this.entry.getSize()) {
            throw new IOException("attempt to write past end of STORED entry");
        }
        this.out.write(b2, off, len);
        this.written += (long)len;
        if (this.entry.getFormat() == 2) {
            for (int pos = 0; pos < len; ++pos) {
                this.crc += (long)(b2[pos] & 0xFF);
            }
        }
        this.count(len);
    }

    public void finish() throws IOException {
        this.ensureOpen();
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.entry != null) {
            throw new IOException("This archive contains unclosed entries.");
        }
        this.entry = new CpioArchiveEntry(this.entryFormat);
        this.entry.setName("TRAILER!!!");
        this.entry.setNumberOfLinks(1L);
        this.writeHeader(this.entry);
        this.closeArchiveEntry();
        int lengthOfLastBlock = (int)(this.getBytesWritten() % (long)this.blockSize);
        if (lengthOfLastBlock != 0) {
            this.pad(this.blockSize - lengthOfLastBlock);
        }
        this.finished = true;
    }

    public void close() throws IOException {
        if (!this.finished) {
            this.finish();
        }
        if (!this.closed) {
            this.out.close();
            this.closed = true;
        }
    }

    private void pad(int count) throws IOException {
        if (count > 0) {
            byte[] buff = new byte[count];
            this.out.write(buff);
            this.count(count);
        }
    }

    private void writeBinaryLong(long number, int length, boolean swapHalfWord) throws IOException {
        byte[] tmp = CpioUtil.long2byteArray(number, length, swapHalfWord);
        this.out.write(tmp);
        this.count(tmp.length);
    }

    private void writeAsciiLong(long number, int length, int radix) throws IOException {
        String tmpStr;
        StringBuilder tmp = new StringBuilder();
        if (radix == 16) {
            tmp.append(Long.toHexString(number));
        } else if (radix == 8) {
            tmp.append(Long.toOctalString(number));
        } else {
            tmp.append(Long.toString(number));
        }
        if (tmp.length() <= length) {
            long insertLength = length - tmp.length();
            int pos = 0;
            while ((long)pos < insertLength) {
                tmp.insert(0, "0");
                ++pos;
            }
            tmpStr = tmp.toString();
        } else {
            tmpStr = tmp.substring(tmp.length() - length);
        }
        byte[] b2 = ArchiveUtils.toAsciiBytes(tmpStr);
        this.out.write(b2);
        this.count(b2.length);
    }

    private void writeCString(String str) throws IOException {
        ByteBuffer buf2 = this.encoding.encode(str);
        int len = buf2.limit() - buf2.position();
        this.out.write(buf2.array(), buf2.arrayOffset(), len);
        this.out.write(0);
        this.count(len + 1);
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        return new CpioArchiveEntry(inputFile, entryName);
    }
}


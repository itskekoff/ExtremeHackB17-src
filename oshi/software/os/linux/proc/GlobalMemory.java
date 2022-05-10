package oshi.software.os.linux.proc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import oshi.hardware.Memory;

public class GlobalMemory
implements Memory {
    private long totalMemory = 0L;

    public long getAvailable() {
        long returnCurrentUsageMemory = 0L;
        Scanner in2 = null;
        try {
            in2 = new Scanner(new FileReader("/proc/meminfo"));
        }
        catch (FileNotFoundException e2) {
            return returnCurrentUsageMemory;
        }
        in2.useDelimiter("\n");
        while (in2.hasNext()) {
            String checkLine = in2.next();
            if (!checkLine.startsWith("MemFree:") && !checkLine.startsWith("MemAvailable:")) continue;
            String[] memorySplit = checkLine.split("\\s+");
            returnCurrentUsageMemory = new Long(memorySplit[1]);
            if (memorySplit[2].equals("kB")) {
                returnCurrentUsageMemory *= 1024L;
            }
            if (!memorySplit[0].equals("MemAvailable:")) continue;
            break;
        }
        in2.close();
        return returnCurrentUsageMemory;
    }

    public long getTotal() {
        if (this.totalMemory == 0L) {
            Scanner in2 = null;
            try {
                in2 = new Scanner(new FileReader("/proc/meminfo"));
            }
            catch (FileNotFoundException e2) {
                this.totalMemory = 0L;
                return this.totalMemory;
            }
            in2.useDelimiter("\n");
            while (in2.hasNext()) {
                String checkLine = in2.next();
                if (!checkLine.startsWith("MemTotal:")) continue;
                String[] memorySplit = checkLine.split("\\s+");
                this.totalMemory = new Long(memorySplit[1]);
                if (!memorySplit[2].equals("kB")) break;
                this.totalMemory *= 1024L;
                break;
            }
            in2.close();
        }
        return this.totalMemory;
    }
}


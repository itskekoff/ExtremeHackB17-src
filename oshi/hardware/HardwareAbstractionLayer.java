package oshi.hardware;

import oshi.hardware.Memory;
import oshi.hardware.Processor;

public interface HardwareAbstractionLayer {
    public Processor[] getProcessors();

    public Memory getMemory();
}


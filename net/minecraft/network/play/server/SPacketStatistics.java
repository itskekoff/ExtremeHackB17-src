package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class SPacketStatistics
implements Packet<INetHandlerPlayClient> {
    private Map<StatBase, Integer> statisticMap;

    public SPacketStatistics() {
    }

    public SPacketStatistics(Map<StatBase, Integer> statisticMapIn) {
        this.statisticMap = statisticMapIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleStatistics(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        int i2 = buf2.readVarIntFromBuffer();
        this.statisticMap = Maps.newHashMap();
        for (int j2 = 0; j2 < i2; ++j2) {
            StatBase statbase = StatList.getOneShotStat(buf2.readStringFromBuffer(32767));
            int k2 = buf2.readVarIntFromBuffer();
            if (statbase == null) continue;
            this.statisticMap.put(statbase, k2);
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.statisticMap.size());
        for (Map.Entry<StatBase, Integer> entry : this.statisticMap.entrySet()) {
            buf2.writeString(entry.getKey().statId);
            buf2.writeVarIntToBuffer(entry.getValue());
        }
    }

    public Map<StatBase, Integer> getStatisticMap() {
        return this.statisticMap;
    }
}


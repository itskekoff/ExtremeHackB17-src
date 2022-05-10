package net.minecraft.network.play.server;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.modules.ModuleManager;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketTabComplete
implements Packet<INetHandlerPlayClient> {
    private String[] matches;

    public SPacketTabComplete() {
    }

    public SPacketTabComplete(String[] matchesIn) {
        this.matches = matchesIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.matches = new String[buf2.readVarIntFromBuffer()];
        for (int i2 = 0; i2 < this.matches.length; ++i2) {
            this.matches[i2] = buf2.readStringFromBuffer(32767);
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.matches.length);
        String[] arrstring = this.matches;
        int n2 = this.matches.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            String s2 = arrstring[i2];
            buf2.writeString(s2);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("===PlugFinder===").isEnabled()) {
            String[] commands = this.getMatches();
            String message = "";
            int size = 0;
            String[] arrstring = commands;
            int n2 = commands.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                String command = arrstring[i2];
                String pluginName = command.split(":")[0].substring(1);
                if (message.contains(pluginName) || !command.contains(":") || pluginName.equalsIgnoreCase("minecraft") || pluginName.equalsIgnoreCase("bukkit")) continue;
                ++size;
                message = message.isEmpty() ? String.valueOf(message) + pluginName : String.valueOf(message) + "\u00a78, \u00a7a" + pluginName;
            }
            if (!message.isEmpty()) {
                ChatUtils.emessage("\u00a77Plugins (\u00a7f" + size + "\u00a77): \u00a7a " + message + "\u00a77.");
                ChatUtils.emessage(String.valueOf(ChatUtils.red) + "SUCCESS!");
            } else {
                ChatUtils.emessage("Plugins not found!");
            }
        } else {
            handler.handleTabComplete(this);
        }
    }

    public String[] getMatches() {
        return this.matches;
    }
}


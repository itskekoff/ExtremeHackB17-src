package net.minecraft.client.gui;

import ShwepSS.B17.IGuiMultiplayer;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;

public class ServerSelectionList
extends GuiListExtended {
    private final IGuiMultiplayer owner;
    private final List<ServerListEntryNormal> serverListInternet = Lists.newArrayList();
    private final List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public ServerSelectionList(IGuiMultiplayer guimp, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = guimp;
    }

    @Override
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        if (index < this.serverListInternet.size()) {
            return this.serverListInternet.get(index);
        }
        if ((index -= this.serverListInternet.size()) == 0) {
            return this.lanScanEntry;
        }
        return this.serverListLan.get(--index);
    }

    @Override
    public int getSize() {
        return this.serverListInternet.size() + 1 + this.serverListLan.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn) {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelected() {
        return this.selectedSlotIndex;
    }

    public void updateOnlineServers(ServerList p_148195_1_) {
        this.serverListInternet.clear();
        for (int i2 = 0; i2 < p_148195_1_.countServers(); ++i2) {
            this.serverListInternet.add(new ServerListEntryNormal(this.owner, p_148195_1_.getServerData(i2)));
        }
    }

    public void updateOnlineShkoloServers(ServerList p_148195_1_) {
        this.serverListInternet.clear();
        for (int i2 = 0; i2 < p_148195_1_.countShkoloServers(); ++i2) {
            this.serverListInternet.add(new ServerListEntryNormal(this.owner, p_148195_1_.getShkoloServerData(i2)));
        }
    }

    public void updateNetworkServers(List<LanServerInfo> p_148194_1_) {
        this.serverListLan.clear();
        for (LanServerInfo lanserverinfo : p_148194_1_) {
            this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanserverinfo));
        }
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 85;
    }
}


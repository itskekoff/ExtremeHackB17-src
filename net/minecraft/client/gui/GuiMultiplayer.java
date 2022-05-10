package net.minecraft.client.gui;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.IGuiMultiplayer;
import ShwepSS.B17.ShkiloMonik;
import ShwepSS.B17.Utils.EnumProxyType;
import ShwepSS.B17.Utils.ProxyGui;
import ShwepSS.B17.Utils.ServerFinder;
import ShwepSS.B17.Utils.ServerHook;
import ShwepSS.B17.modules.hacks.GuiServerFinder;
import ShwepSS.B17.modules.hacks.bot.ProxyManager;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import viamcp.gui.GuiProtocolSelector;

public class GuiMultiplayer
extends GuiScreen
implements IGuiMultiplayer {
    public static final Logger LOGGER = LogManager.getLogger();
    public final ServerPinger oldServerPinger = new ServerPinger();
    public final GuiScreen parentScreen;
    public ServerSelectionList serverListSelector;
    public ServerList savedServerList;
    public GuiButton btnEditServer;
    public GuiButton btnSelectServer;
    public GuiButton btnDeleteServer;
    public boolean deletingServer;
    public boolean addingServer;
    public boolean editingServer;
    public boolean directConnect;
    public String hoveringText;
    public ServerData selectedServer;
    public LanServerDetector.LanServerList lanServerList;
    public LanServerDetector.ThreadLanServerFind lanServerDetector;
    public boolean initialized;

    public GuiMultiplayer(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        if (this.initialized) {
            this.serverListSelector.setDimensions(width, height, 32, height - 64);
        } else {
            this.initialized = true;
            this.savedServerList = new ServerList(this.mc);
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();
            try {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }
            this.serverListSelector = new ServerSelectionList(this, this.mc, width, height, 32, height - 64, 36);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }
        this.createButtons();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.serverListSelector.handleMouseInput();
    }

    public void createButtons() {
        this.btnEditServer = this.addButton(new GuiButton(7, width / 2 - 154, height - 28, 70, 20, I18n.format("selectServer.edit", new Object[0])));
        this.btnDeleteServer = this.addButton(new GuiButton(2, width / 2 - 74, height - 28, 70, 20, I18n.format("selectServer.delete", new Object[0])));
        this.btnSelectServer = this.addButton(new GuiButton(1, width / 2 - 154, height - 52, 100, 20, I18n.format("selectServer.select", new Object[0])));
        this.buttonList.add(new GuiButton(4, width / 2 - 50, height - 52, 100, 20, I18n.format("selectServer.direct", new Object[0])));
        this.buttonList.add(new GuiButton(3, width / 2 + 4 + 50, height - 52, 100, 20, I18n.format("selectServer.add", new Object[0])));
        if (ExtremeHack.instance.getProxyType() != EnumProxyType.NONE && ExtremeHack.instance.getProxyType() != EnumProxyType.SOCKS4) {
            this.buttonList.add(new GuiButton(8, width / 2 + 4, height - 28, 70, 20, I18n.format("\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u0440\u043e\u043a\u0441\u0438", new Object[0])));
        } else {
            this.buttonList.add(new GuiButton(8, width / 2 + 4, height - 28, 70, 20, I18n.format("selectServer.refresh", new Object[0])));
        }
        this.buttonList.add(new GuiButton(0, width / 2 + 4 + 76, height - 28, 75, 20, I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(new GuiButton(97, width / 2 - 250, height - 28, 75, 20, I18n.format("Proxy IP:port", new Object[0])));
        this.buttonList.add(new GuiButton(69, width / 2 - 250, height - 52, 75, 20, I18n.format("Mine version", new Object[0])));
        this.buttonList.add(new GuiButton(98, width / 2 + 180, height - 28, 75, 20, I18n.format("SAVE_SERVERS", new Object[0])));
        this.buttonList.add(new GuiButton(99, width / 2 + 180, height - 52, 75, 20, I18n.format("LOAD_SERVERS", new Object[0])));
        this.buttonList.add(new GuiButton(100, width / 2 - 330, height - 52, 75, 20, I18n.format("SERVER_FINDER", new Object[0])));
        this.buttonList.add(new GuiButton(101, width / 2 - 330, height - 28, 75, 20, I18n.format("SERVER_FINDER 2", new Object[0])));
        this.buttonList.add(new GuiButton(105, width / 2 + 260, height - 28, 75, 20, I18n.format("\u041e\u0444\u0444\u043d\u0443\u0442\u044c \u043f\u0440\u043e\u043a\u0441\u0438", new Object[0])));
        this.buttonList.add(new GuiButton(106, width / 2 + 260, height - 52, 75, 20, I18n.format("\u0428\u043a\u043e\u043b\u043e\u043c\u043e\u043d\u0438\u0442\u043e\u0440\u0438\u043d\u0433", new Object[0])));
        this.selectServer(this.serverListSelector.getSelected());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.lanServerList.getWasUpdated()) {
            List<LanServerInfo> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.updateNetworkServers(list);
        }
        this.oldServerPinger.pingPendingNetworks();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.oldServerPinger.clearPendingNetworks();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
            GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
            if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                String s4 = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverName;
                if (s4 != null) {
                    this.deletingServer = true;
                    String s2 = I18n.format("selectServer.deleteQuestion", new Object[0]);
                    String s1 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning", new Object[0]);
                    String s22 = I18n.format("selectServer.deleteButton", new Object[0]);
                    String s3 = I18n.format("gui.cancel", new Object[0]);
                    GuiYesNo guiyesno = new GuiYesNo(this, s2, s1, s22, s3, this.serverListSelector.getSelected());
                    this.mc.displayGuiScreen(guiyesno);
                }
            } else if (button.id == 97) {
                this.mc.displayGuiScreen(new ProxyGui(this.parentScreen));
            } else if (button.id == 1) {
                this.connectToSelected();
            } else if (button.id == 69) {
                this.mc.displayGuiScreen(new GuiProtocolSelector(this));
            } else if (button.id == 97) {
                this.mc.displayGuiScreen(new GuiMainMenu());
            } else if (button.id == 4) {
                this.directConnect = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false);
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer));
            } else if (button.id == 3) {
                this.addingServer = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            } else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.editingServer = true;
                ServerData serverdata = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 8) {
                ExtremeHack.proxyIP = ProxyManager.stringProxy.get(RandomUtils.nextInt(0, ProxyManager.stringProxy.size()));
                this.refreshServerList();
            } else if (button.id == 100) {
                this.mc.displayGuiScreen(new ServerFinder(this));
            } else if (button.id == 101) {
                new GuiServerFinder().setVisible(true);
            } else if (button.id == 99) {
                new Thread(() -> ServerHook.importServers(this)).start();
            } else if (button.id == 98) {
                new Thread(() -> ServerHook.exportServers(this)).start();
            } else if (button.id == 105) {
                ExtremeHack.instance.proxyType = EnumProxyType.NONE;
            } else if (button.id == 106) {
                this.mc.displayGuiScreen(new ShkiloMonik(this.parentScreen));
            }
        }
    }

    public void refreshServerList() {
        this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
        GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
        if (this.deletingServer) {
            this.deletingServer = false;
            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.savedServerList.removeServerData(this.serverListSelector.getSelected());
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }
            this.mc.displayGuiScreen(this);
        } else if (this.directConnect) {
            this.directConnect = false;
            if (result) {
                this.connectToServer(this.selectedServer);
            } else {
                this.mc.displayGuiScreen(this);
            }
        } else if (this.addingServer) {
            this.addingServer = false;
            if (result) {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }
            this.mc.displayGuiScreen(this);
        } else if (this.editingServer) {
            this.editingServer = false;
            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                ServerData serverdata = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
        int i2 = this.serverListSelector.getSelected();
        GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = i2 < 0 ? null : this.serverListSelector.getListEntry(i2);
        if (keyCode == 63) {
            this.refreshServerList();
        } else if (i2 >= 0) {
            if (keyCode == 200) {
                if (GuiMultiplayer.isShiftKeyDown()) {
                    if (i2 > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                        this.savedServerList.swapServers(i2, i2 - 1);
                        this.selectServer(this.serverListSelector.getSelected() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        this.serverListSelector.updateOnlineServers(this.savedServerList);
                    }
                } else if (i2 > 0) {
                    this.selectServer(this.serverListSelector.getSelected() - 1);
                    this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                    if (this.serverListSelector.getListEntry(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                        if (this.serverListSelector.getSelected() > 0) {
                            this.selectServer(this.serverListSelector.getSize() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        } else {
                            this.selectServer(-1);
                        }
                    }
                } else {
                    this.selectServer(-1);
                }
            } else if (keyCode == 208) {
                if (GuiMultiplayer.isShiftKeyDown()) {
                    if (i2 < this.savedServerList.countServers() - 1) {
                        this.savedServerList.swapServers(i2, i2 + 1);
                        this.selectServer(i2 + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        this.serverListSelector.updateOnlineServers(this.savedServerList);
                    }
                } else if (i2 < this.serverListSelector.getSize()) {
                    this.selectServer(this.serverListSelector.getSelected() + 1);
                    this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                    if (this.serverListSelector.getListEntry(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                        if (this.serverListSelector.getSelected() < this.serverListSelector.getSize() - 1) {
                            this.selectServer(this.serverListSelector.getSize() + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        } else {
                            this.selectServer(-1);
                        }
                    }
                } else {
                    this.selectServer(-1);
                }
            } else if (keyCode != 28 && keyCode != 156) {
                super.keyTyped(typedChar, keyCode);
            } else {
                this.actionPerformed((GuiButton)this.buttonList.get(2));
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveringText = null;
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.title", new Object[0]), width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveringText != null) {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
    }

    @Override
    public void connectToSelected() {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
        GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
        if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            this.connectToServer(((ServerListEntryNormal)guilistextended$iguilistentry).getServerData());
        } else if (guilistextended$iguilistentry instanceof ServerListEntryLanDetected) {
            LanServerInfo lanserverinfo = ((ServerListEntryLanDetected)guilistextended$iguilistentry).getServerData();
            this.connectToServer(new ServerData(lanserverinfo.getServerMotd(), lanserverinfo.getServerIpPort(), true));
        }
    }

    public void connectToServer(ServerData server) {
        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
    }

    @Override
    public void selectServer(int index) {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        this.btnEditServer.enabled = false;
        this.btnDeleteServer.enabled = false;
        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan)) {
            this.btnSelectServer.enabled = true;
            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.btnEditServer.enabled = true;
                this.btnDeleteServer.enabled = true;
            }
        }
    }

    @Override
    public ServerPinger getOldServerPinger() {
        return this.oldServerPinger;
    }

    @Override
    public void setHoveringText(String p_146793_1_) {
        this.hoveringText = p_146793_1_;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public ServerList getServerList() {
        return this.savedServerList;
    }

    @Override
    public boolean canMoveUp(ServerListEntryNormal p_175392_1_, int p_175392_2_) {
        return p_175392_2_ > 0;
    }

    @Override
    public boolean canMoveDown(ServerListEntryNormal p_175394_1_, int p_175394_2_) {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    @Override
    public void moveServerUp(ServerListEntryNormal p_175391_1_, int p_175391_2_, boolean p_175391_3_) {
        int i2 = p_175391_3_ ? 0 : p_175391_2_ - 1;
        this.savedServerList.swapServers(p_175391_2_, i2);
        if (this.serverListSelector.getSelected() == p_175391_2_) {
            this.selectServer(i2);
        }
        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }

    @Override
    public void moveServerDown(ServerListEntryNormal p_175393_1_, int p_175393_2_, boolean p_175393_3_) {
        int i2 = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
        this.savedServerList.swapServers(p_175393_2_, i2);
        if (this.serverListSelector.getSelected() == p_175393_2_) {
            this.selectServer(i2);
        }
        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }
}


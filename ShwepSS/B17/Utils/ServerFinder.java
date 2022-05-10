package ShwepSS.B17.Utils;

import ShwepSS.B17.Utils.WurstServerPinger;
import ShwepSS.B17.cg.MathUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

public class ServerFinder
extends GuiScreen {
    private static final String[] stateStrings;
    private GuiMultiplayer prevScreen;
    private GuiTextField ipBox;
    private GuiTextField maxThreadsBox;
    private GuiTextField port1;
    private int checked;
    private int working;
    private ServerFinderState state;
    public static int threads;
    public static int startPort;

    static {
        threads = 128;
        startPort = 25564;
        stateStrings = new String[]{"", "\u00a72Searching...", "\u00a72Resolving...", "\u00a74Unknown Host!", "\u00a74Cancelled!", "\u00a72Done!", "\u00a74An error occurred!"};
    }

    public ServerFinder(GuiMultiplayer prevMultiplayerMenu) {
        this.prevScreen = prevMultiplayerMenu;
    }

    @Override
    public void updateScreen() {
        this.ipBox.updateCursorCounter();
        ((GuiButton)this.buttonList.get((int)0)).displayString = this.state.isRunning() ? "Cancel" : "Search";
        this.ipBox.setEnabled(!this.state.isRunning());
        this.maxThreadsBox.setEnabled(!this.state.isRunning());
        ((GuiButton)this.buttonList.get((int)0)).enabled = MathUtils.isInteger(this.maxThreadsBox.getText()) && !this.ipBox.getText().isEmpty();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Search"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 144 + 12, "Back"));
        this.ipBox = new GuiTextField(0, this.fontRendererObj, width / 2 - 100, height / 4 + 34, 200, 20);
        this.ipBox.setMaxStringLength(200);
        this.ipBox.setFocused(true);
        this.maxThreadsBox = new GuiTextField(1, this.fontRendererObj, width / 2 - 32, height / 4 + 58, 26, 12);
        this.maxThreadsBox.setMaxStringLength(3);
        this.port1 = new GuiTextField(55, this.fontRendererObj, width / 2 - 80, height / 4 - 10, 40, 20);
        this.port1.setMaxStringLength(5);
        this.port1.setText("25564");
        this.maxThreadsBox.setFocused(false);
        this.maxThreadsBox.setText(Integer.toString(50));
        this.state = ServerFinderState.NOT_RUNNING;
    }

    @Override
    public void onGuiClosed() {
        this.state = ServerFinderState.CANCELLED;
        if (MathUtils.isInteger(this.maxThreadsBox.getText())) {
            threads = Integer.valueOf(this.maxThreadsBox.getText());
        }
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton clickedButton) {
        if (clickedButton.enabled) {
            if (clickedButton.id == 0) {
                if (this.state.isRunning()) {
                    this.state = ServerFinderState.CANCELLED;
                } else {
                    if (MathUtils.isInteger(this.maxThreadsBox.getText())) {
                        threads = Integer.valueOf(this.maxThreadsBox.getText());
                        startPort = Integer.parseInt(this.port1.getText());
                    }
                    this.state = ServerFinderState.RESOLVING;
                    this.checked = 0;
                    this.working = 0;
                    new Thread("Server Finder"){

                        @Override
                        public void run() {
                            try {
                                int[] changes;
                                InetAddress addr = InetAddress.getByName(ServerFinder.this.ipBox.getText().split(":")[0].trim());
                                int[] ipParts = new int[4];
                                for (int i2 = 0; i2 < 4; ++i2) {
                                    ipParts[i2] = addr.getAddress()[i2] & 0xFF;
                                }
                                ServerFinder.access$2(ServerFinder.this, ServerFinderState.SEARCHING);
                                ArrayList<WurstServerPinger> pingers = new ArrayList<WurstServerPinger>();
                                int[] arrn = new int[7];
                                arrn[1] = 1;
                                arrn[2] = -1;
                                arrn[3] = 2;
                                arrn[4] = -2;
                                arrn[5] = 3;
                                arrn[6] = -3;
                                int[] array = changes = arrn;
                                int length = changes.length;
                                for (int j2 = 0; j2 < length; ++j2) {
                                    int change = array[j2];
                                    for (int i2 = 0; i2 <= 255; ++i2) {
                                        if (ServerFinder.this.state == ServerFinderState.CANCELLED) {
                                            return;
                                        }
                                        int[] ipParts2 = (int[])ipParts.clone();
                                        ipParts2[2] = ipParts[2] + change & 0xFF;
                                        ipParts2[3] = i2;
                                        String ip2 = String.valueOf(String.valueOf(ipParts2[0])) + "." + ipParts2[1] + "." + ipParts2[2] + "." + ipParts2[3];
                                        WurstServerPinger pinger = new WurstServerPinger();
                                        pinger.ping(ip2, startPort);
                                        pingers.add(pinger);
                                        while (pingers.size() >= threads) {
                                            if (ServerFinder.this.state == ServerFinderState.CANCELLED) {
                                                return;
                                            }
                                            ServerFinder.this.updatePingers(pingers);
                                        }
                                    }
                                }
                                while (pingers.size() > 0) {
                                    if (ServerFinder.this.state == ServerFinderState.CANCELLED) {
                                        return;
                                    }
                                    ServerFinder.this.updatePingers(pingers);
                                }
                                ServerFinder.access$2(ServerFinder.this, ServerFinderState.DONE);
                            }
                            catch (UnknownHostException e2) {
                                ServerFinder.access$2(ServerFinder.this, ServerFinderState.UNKNOWN_HOST);
                            }
                            catch (Exception e2) {
                                e2.printStackTrace();
                                ServerFinder.access$2(ServerFinder.this, ServerFinderState.ERROR);
                            }
                        }
                    }.start();
                }
            } else if (clickedButton.id == 2) {
                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    private boolean serverInList(String ip2) {
        for (int i2 = 0; i2 < this.prevScreen.savedServerList.countServers(); ++i2) {
            if (!this.prevScreen.savedServerList.getServerData((int)i2).serverIP.equals(ip2)) continue;
            return true;
        }
        return false;
    }

    private void updatePingers(ArrayList<WurstServerPinger> pingers) {
        for (int i2 = 0; i2 < pingers.size(); ++i2) {
            if (pingers.get(i2).isStillPinging()) continue;
            ++this.checked;
            if (pingers.get(i2).isWorking()) {
                ++this.working;
                if (!this.serverInList(pingers.get((int)i2).server.serverIP)) {
                    this.prevScreen.savedServerList.addServerData(new ServerData("Grief me #" + this.working, pingers.get((int)i2).server.serverIP, false));
                    this.prevScreen.savedServerList.saveServerList();
                    this.prevScreen.serverListSelector.setSelectedSlotIndex(-1);
                    this.prevScreen.serverListSelector.updateOnlineServers(this.prevScreen.savedServerList);
                }
            }
            pingers.remove(i2);
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        this.ipBox.textboxKeyTyped(par1, par2);
        this.maxThreadsBox.textboxKeyTyped(par1, par2);
        this.port1.textboxKeyTyped(par1, par2);
        if (par2 == 28 || par2 == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.ipBox.mouseClicked(par1, par2, par3);
        this.port1.mouseClicked(par1, par2, par3);
        this.maxThreadsBox.mouseClicked(par1, par2, par3);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        ServerFinder.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Server Finder \u0438\u0437 \u0447\u0438\u0442\u0430 Wurst", width / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "This will search for servers with similar IPs", width / 2, 40, 0xA0A0A0);
        this.drawCenteredString(this.fontRendererObj, "to the IP you type into the field below.", width / 2, 50, 0xA0A0A0);
        this.drawCenteredString(this.fontRendererObj, "The servers it finds will be added to your server list.", width / 2, 60, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "Server address:", width / 2 - 100, height / 4 + 24, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "port:", width / 2 - 100, height / 4 - 4, 0xA0A0A0);
        this.ipBox.drawTextBox();
        this.drawString(this.fontRendererObj, "Max. threads:", width / 2 - 100, height / 4 + 60, 0xA0A0A0);
        this.maxThreadsBox.drawTextBox();
        this.port1.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, this.state.toString(), width / 2, height / 4 + 73, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "Checked: " + this.checked + " / 1792", width / 2 - 100, height / 4 + 84, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "Working: " + this.working, width / 2 - 100, height / 4 + 94, 0xA0A0A0);
        super.drawScreen(par1, par2, par3);
    }

    static void access$2(ServerFinder guiServerFinder, ServerFinderState state) {
        guiServerFinder.state = state;
    }

    static enum ServerFinderState {
        NOT_RUNNING("NOT_RUNNING", 0),
        SEARCHING("SEARCHING", 1),
        RESOLVING("RESOLVING", 2),
        UNKNOWN_HOST("UNKNOWN_HOST", 3),
        CANCELLED("CANCELLED", 4),
        DONE("DONE", 5),
        ERROR("ERROR", 6);


        private ServerFinderState(String s2, int n3) {
        }

        public boolean isRunning() {
            return this == SEARCHING || this == RESOLVING;
        }

        public String toString() {
            return stateStrings[this.ordinal()];
        }
    }
}


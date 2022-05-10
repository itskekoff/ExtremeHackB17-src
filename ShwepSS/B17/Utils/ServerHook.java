package ShwepSS.B17.Utils;

import ShwepSS.B17.Utils.FrameHook;
import ShwepSS.B17.Utils.WurstFolders;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

public class ServerHook {
    private static String currentServerIP;
    private static ServerListEntryNormal lastServer;

    public static void importServers(GuiMultiplayer guiMultiplayer) {
        JFileChooser fileChooser = new JFileChooser(WurstFolders.SERVERLISTS.toFile()){

            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        fileChooser.setFileSelectionMode(0);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
        int action = fileChooser.showOpenDialog(FrameHook.getFrame());
        if (action == 0) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader load = new BufferedReader(new FileReader(file));
                int i2 = 0;
                String line = "";
                while ((line = load.readLine()) != null) {
                    guiMultiplayer.savedServerList.addServerData(new ServerData("SAVED_SERVER_#" + ++i2, line, false));
                    guiMultiplayer.savedServerList.saveServerList();
                    guiMultiplayer.serverListSelector.setSelectedSlotIndex(-1);
                    guiMultiplayer.serverListSelector.updateOnlineServers(guiMultiplayer.savedServerList);
                }
                load.close();
                guiMultiplayer.refreshServerList();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void exportServers(GuiMultiplayer guiMultiplayer) {
        JFileChooser fileChooser = new JFileChooser(WurstFolders.SERVERLISTS.toFile()){

            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        fileChooser.setFileSelectionMode(0);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
        int action = fileChooser.showSaveDialog(FrameHook.getFrame());
        if (action == 0) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new File(String.valueOf(String.valueOf(file.getPath())) + ".txt");
                }
                PrintWriter save = new PrintWriter(new FileWriter(file));
                for (int i2 = 0; i2 < guiMultiplayer.savedServerList.countServers(); ++i2) {
                    save.println(guiMultiplayer.savedServerList.getServerData((int)i2).serverIP);
                }
                save.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void joinLastServer(GuiMultiplayer guiMultiplayer) {
        if (lastServer == null) {
            return;
        }
        currentServerIP = ServerHook.lastServer.getServerData().serverIP;
        if (!currentServerIP.contains(":")) {
            currentServerIP = String.valueOf(String.valueOf(currentServerIP)) + ":25565";
        }
        guiMultiplayer.connectToServer(lastServer.getServerData());
    }

    public static void reconnectToLastServer(GuiScreen prevScreen) {
        if (lastServer == null) {
            return;
        }
        currentServerIP = ServerHook.lastServer.getServerData().serverIP;
        if (!currentServerIP.contains(":")) {
            currentServerIP = String.valueOf(String.valueOf(currentServerIP)) + ":25565";
        }
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(new GuiConnecting(prevScreen, mc, lastServer.getServerData()));
    }

    public static void updateLastServerFromServerlist(GuiListExtended.IGuiListEntry entry, GuiMultiplayer guiMultiplayer) {
        if (entry instanceof ServerListEntryNormal) {
            currentServerIP = ((ServerListEntryNormal)entry).getServerData().serverIP;
            if (!currentServerIP.contains(":")) {
                currentServerIP = String.valueOf(String.valueOf(currentServerIP)) + ":25565";
            }
            lastServer = (ServerListEntryNormal)(guiMultiplayer.serverListSelector.getSelected() < 0 ? null : guiMultiplayer.serverListSelector.getListEntry(guiMultiplayer.serverListSelector.getSelected()));
        } else if (entry instanceof ServerListEntryLanDetected) {
            currentServerIP = ((ServerListEntryLanDetected)entry).getServerData().getServerIpPort();
            lastServer = new ServerListEntryNormal(guiMultiplayer, new ServerData("LAN-Server", currentServerIP, false));
        }
    }

    public static boolean hasLastServer() {
        return lastServer != null;
    }

    public static void setCurrentIpToSingleplayer() {
        currentServerIP = "127.0.0.1:25565";
    }

    public static void setCurrentIpToLanServer(String port) {
        currentServerIP = "127.0.0.1:" + port;
    }

    public static String getCurrentServerIP() {
        return currentServerIP;
    }

    public static ServerData getLastServerData() {
        return lastServer.getServerData();
    }
}


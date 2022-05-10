package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final List<ServerData> servers = Lists.newArrayList();
    private final List<ServerData> shkoloservers = Lists.newArrayList();

    public ServerList(Minecraft mcIn) {
        this.mc = mcIn;
        this.loadServerList();
        this.loadShkoloServerList();
    }

    public void loadServerList() {
        try {
            this.servers.clear();
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
            if (nbttagcompound == null) {
                return;
            }
            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);
            for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
                this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i2)));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load server list", (Throwable)exception);
        }
    }

    public void loadShkoloServerList() {
        try {
            this.getShkoloservers().clear();
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "shkoloservers.dat"));
            if (nbttagcompound == null) {
                return;
            }
            NBTTagList nbttaglist = nbttagcompound.getTagList("shkoloservers", 10);
            for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
                this.getShkoloservers().add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i2)));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load server list", (Throwable)exception);
        }
    }

    public void saveServerList() {
        try {
            NBTTagList nbttaglist = new NBTTagList();
            for (ServerData serverdata : this.servers) {
                nbttaglist.appendTag(serverdata.getNBTCompound());
            }
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", nbttaglist);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.mcDataDir, "servers.dat"));
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save server list", (Throwable)exception);
        }
    }

    public void saveShkoloServerList() {
        try {
            NBTTagList nbttaglist = new NBTTagList();
            for (ServerData serverdata : this.getShkoloservers()) {
                nbttaglist.appendTag(serverdata.getNBTCompound());
            }
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("shkoloservers", nbttaglist);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.mcDataDir, "shkoloservers.dat"));
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save server list", (Throwable)exception);
        }
    }

    public ServerData getServerData(int index) {
        return this.servers.get(index);
    }

    public ServerData getShkoloServerData(int index) {
        return this.getShkoloservers().get(index);
    }

    public void removeServerData(int index) {
        this.servers.remove(index);
    }

    public void removeShkoloServerData(int index) {
        this.getShkoloservers().remove(index);
    }

    public void addServerData(ServerData server) {
        this.servers.add(server);
    }

    public void addShkoloServerData(ServerData server) {
        this.getShkoloservers().add(server);
    }

    public int countServers() {
        return this.servers.size();
    }

    public int countShkoloServers() {
        return this.getShkoloservers().size();
    }

    public void swapServers(int pos1, int pos2) {
        ServerData serverdata = this.getServerData(pos1);
        this.servers.set(pos1, this.getServerData(pos2));
        this.servers.set(pos2, serverdata);
        this.saveServerList();
    }

    public void swapShkoloServers(int pos1, int pos2) {
        ServerData serverdata = this.getShkoloServerData(pos1);
        this.getShkoloservers().set(pos1, this.getShkoloServerData(pos2));
        this.getShkoloservers().set(pos2, serverdata);
        this.saveShkoloServerList();
    }

    public void set(int index, ServerData server) {
        this.servers.set(index, server);
    }

    public void shkoloset(int index, ServerData server) {
        this.getShkoloservers().set(index, server);
    }

    public static void saveSingleShkoloServer(ServerData server) {
        ServerList serverlist = new ServerList(Minecraft.getMinecraft());
        serverlist.loadShkoloServerList();
        for (int i2 = 0; i2 < serverlist.countShkoloServers(); ++i2) {
            ServerData serverdata = serverlist.getShkoloServerData(i2);
            if (!serverdata.serverName.equals(server.serverName) || !serverdata.serverIP.equals(server.serverIP)) continue;
            serverlist.shkoloset(i2, server);
            break;
        }
        serverlist.saveShkoloServerList();
    }

    public static void saveSingleServer(ServerData server) {
        ServerList serverlist = new ServerList(Minecraft.getMinecraft());
        serverlist.loadServerList();
        for (int i2 = 0; i2 < serverlist.countServers(); ++i2) {
            ServerData serverdata = serverlist.getServerData(i2);
            if (!serverdata.serverName.equals(server.serverName) || !serverdata.serverIP.equals(server.serverIP)) continue;
            serverlist.set(i2, server);
            break;
        }
        serverlist.saveServerList();
    }

    public List<ServerData> getShkoloservers() {
        return this.shkoloservers;
    }
}


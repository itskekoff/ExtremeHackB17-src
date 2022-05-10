package ShwepSS.B17;

import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.ServerPinger;

public interface IGuiMultiplayer {
    public ServerPinger getOldServerPinger();

    public ServerList getServerList();

    public void setHoveringText(String var1);

    public boolean canMoveUp(ServerListEntryNormal var1, int var2);

    public boolean canMoveDown(ServerListEntryNormal var1, int var2);

    public void selectServer(int var1);

    public void connectToSelected();

    public void moveServerUp(ServerListEntryNormal var1, int var2, boolean var3);

    public void moveServerDown(ServerListEntryNormal var1, int var2, boolean var3);
}


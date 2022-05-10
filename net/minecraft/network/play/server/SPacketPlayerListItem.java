package net.minecraft.network.play.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

public class SPacketPlayerListItem
implements Packet<INetHandlerPlayClient> {
    private Action action;
    private final List<AddPlayerData> players = Lists.newArrayList();

    public SPacketPlayerListItem() {
    }

    public SPacketPlayerListItem(Action actionIn, EntityPlayerMP ... playersIn) {
        this.action = actionIn;
        EntityPlayerMP[] arrentityPlayerMP = playersIn;
        int n2 = playersIn.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            EntityPlayerMP entityplayermp = arrentityPlayerMP[i2];
            this.players.add(new AddPlayerData(entityplayermp.getGameProfile(), entityplayermp.ping, entityplayermp.interactionManager.getGameType(), entityplayermp.getTabListDisplayName()));
        }
    }

    public SPacketPlayerListItem(Action actionIn, Iterable<EntityPlayerMP> playersIn) {
        this.action = actionIn;
        for (EntityPlayerMP entityplayermp : playersIn) {
            this.players.add(new AddPlayerData(entityplayermp.getGameProfile(), entityplayermp.ping, entityplayermp.interactionManager.getGameType(), entityplayermp.getTabListDisplayName()));
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.action = buf2.readEnumValue(Action.class);
        int i2 = buf2.readVarIntFromBuffer();
        for (int j2 = 0; j2 < i2; ++j2) {
            GameProfile gameprofile = null;
            int k2 = 0;
            GameType gametype = null;
            ITextComponent itextcomponent = null;
            switch (this.action) {
                case ADD_PLAYER: {
                    gameprofile = new GameProfile(buf2.readUuid(), buf2.readStringFromBuffer(16));
                    int l2 = buf2.readVarIntFromBuffer();
                    for (int i1 = 0; i1 < l2; ++i1) {
                        String s2 = buf2.readStringFromBuffer(32767);
                        String s1 = buf2.readStringFromBuffer(32767);
                        if (buf2.readBoolean()) {
                            gameprofile.getProperties().put(s2, new Property(s2, s1, buf2.readStringFromBuffer(32767)));
                            continue;
                        }
                        gameprofile.getProperties().put(s2, new Property(s2, s1));
                    }
                    gametype = GameType.getByID(buf2.readVarIntFromBuffer());
                    k2 = buf2.readVarIntFromBuffer();
                    if (!buf2.readBoolean()) break;
                    itextcomponent = buf2.readTextComponent();
                    break;
                }
                case UPDATE_GAME_MODE: {
                    gameprofile = new GameProfile(buf2.readUuid(), null);
                    gametype = GameType.getByID(buf2.readVarIntFromBuffer());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameprofile = new GameProfile(buf2.readUuid(), null);
                    k2 = buf2.readVarIntFromBuffer();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameprofile = new GameProfile(buf2.readUuid(), null);
                    if (!buf2.readBoolean()) break;
                    itextcomponent = buf2.readTextComponent();
                    break;
                }
                case REMOVE_PLAYER: {
                    gameprofile = new GameProfile(buf2.readUuid(), null);
                }
            }
            this.players.add(new AddPlayerData(gameprofile, k2, gametype, itextcomponent));
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.action);
        buf2.writeVarIntToBuffer(this.players.size());
        for (AddPlayerData spacketplayerlistitem$addplayerdata : this.players) {
            switch (this.action) {
                case ADD_PLAYER: {
                    buf2.writeUuid(spacketplayerlistitem$addplayerdata.getProfile().getId());
                    buf2.writeString(spacketplayerlistitem$addplayerdata.getProfile().getName());
                    buf2.writeVarIntToBuffer(spacketplayerlistitem$addplayerdata.getProfile().getProperties().size());
                    for (Property property : spacketplayerlistitem$addplayerdata.getProfile().getProperties().values()) {
                        buf2.writeString(property.getName());
                        buf2.writeString(property.getValue());
                        if (property.hasSignature()) {
                            buf2.writeBoolean(true);
                            buf2.writeString(property.getSignature());
                            continue;
                        }
                        buf2.writeBoolean(false);
                    }
                    buf2.writeVarIntToBuffer(spacketplayerlistitem$addplayerdata.getGameMode().getID());
                    buf2.writeVarIntToBuffer(spacketplayerlistitem$addplayerdata.getPing());
                    if (spacketplayerlistitem$addplayerdata.getDisplayName() == null) {
                        buf2.writeBoolean(false);
                        break;
                    }
                    buf2.writeBoolean(true);
                    buf2.writeTextComponent(spacketplayerlistitem$addplayerdata.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    buf2.writeUuid(spacketplayerlistitem$addplayerdata.getProfile().getId());
                    buf2.writeVarIntToBuffer(spacketplayerlistitem$addplayerdata.getGameMode().getID());
                    break;
                }
                case UPDATE_LATENCY: {
                    buf2.writeUuid(spacketplayerlistitem$addplayerdata.getProfile().getId());
                    buf2.writeVarIntToBuffer(spacketplayerlistitem$addplayerdata.getPing());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    buf2.writeUuid(spacketplayerlistitem$addplayerdata.getProfile().getId());
                    if (spacketplayerlistitem$addplayerdata.getDisplayName() == null) {
                        buf2.writeBoolean(false);
                        break;
                    }
                    buf2.writeBoolean(true);
                    buf2.writeTextComponent(spacketplayerlistitem$addplayerdata.getDisplayName());
                    break;
                }
                case REMOVE_PLAYER: {
                    buf2.writeUuid(spacketplayerlistitem$addplayerdata.getProfile().getId());
                }
            }
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerListItem(this);
    }

    public List<AddPlayerData> getEntries() {
        return this.players;
    }

    public Action getAction() {
        return this.action;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", (Object)this.action).add("entries", this.players).toString();
    }

    public static enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;

    }

    public class AddPlayerData {
        private final int ping;
        private final GameType gamemode;
        private final GameProfile profile;
        private final ITextComponent displayName;

        public AddPlayerData(GameProfile profileIn, int latencyIn, @Nullable GameType gameModeIn, ITextComponent displayNameIn) {
            this.profile = profileIn;
            this.ping = latencyIn;
            this.gamemode = gameModeIn;
            this.displayName = displayNameIn;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getPing() {
            return this.ping;
        }

        public GameType getGameMode() {
            return this.gamemode;
        }

        @Nullable
        public ITextComponent getDisplayName() {
            return this.displayName;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.ping).add("gameMode", (Object)this.gamemode).add("profile", this.profile).add("displayName", this.displayName == null ? null : ITextComponent.Serializer.componentToJson(this.displayName)).toString();
        }
    }
}

